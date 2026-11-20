package com.example.inmobi.features.journey.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inmobi.features.journey.data.JourneyRepository
import com.example.inmobi.features.journey.domain.PropertyProgress
import com.example.inmobi.features.journey.domain.PropertyStep
import com.example.inmobi.features.journey.domain.toDomain
import com.example.inmobi.features.properties.data.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class JourneyUiState(
    val isLoading: Boolean = false,
    val progress: PropertyProgress? = null,
    val error: String? = null,
    val selectedStep: PropertyStep? = null,
    val errorMessage: String? = null
)

class JourneyViewModel : ViewModel() {

    private val repository = JourneyRepository()
    private val _uiState = MutableStateFlow(JourneyUiState(isLoading = true))
    val uiState: StateFlow<JourneyUiState> = _uiState.asStateFlow()

    private var currentPropertyId: Int? = null

    fun loadProgress(propertyId: Int) {
        currentPropertyId = propertyId
        _uiState.value = JourneyUiState(isLoading = true)
        viewModelScope.launch {
            when (val result = repository.getProgress(propertyId)) {
                is Result.Success -> _uiState.value = JourneyUiState(
                    isLoading = false,
                    progress = result.data.toDomain()
                )
                is Result.Error -> _uiState.value = JourneyUiState(
                    isLoading = false,
                    error = "Error al cargar el progreso: ${result.message}"
                )
                is Result.NetworkError -> _uiState.value = JourneyUiState(
                    isLoading = false,
                    error = "Error de conexión"
                )
            }
        }
    }

    fun openStepDetail(step: PropertyStep) {
        _uiState.value = _uiState.value.copy(selectedStep = step)
    }

    fun closeStepDetail() {
        _uiState.value = _uiState.value.copy(selectedStep = null)
    }

    fun saveStep(completed: Boolean, comments: String?) {
        val propertyId = currentPropertyId ?: return
        val selectedStep = _uiState.value.selectedStep ?: return

        viewModelScope.launch {
            when (val result = repository.updateStep(
                propertyId = propertyId,
                stepId = selectedStep.stepId,
                completed = completed,
                comments = comments
            )) {
                is Result.Success -> {
                    val updatedSteps = _uiState.value.progress?.steps?.map {
                        if (it.id == selectedStep.id) {
                            it.copy(completed = completed, comments = comments)
                        } else it
                    } ?: emptyList()

                    val newProgress = _uiState.value.progress?.copy(steps = updatedSteps)
                    val newCompleted = newProgress?.steps?.count { it.completed } ?: 0
                    val total = newProgress?.totalSteps ?: 1
                    val percentage = if (total > 0) (newCompleted.toFloat() / total * 100f) else 0f

                    _uiState.value = _uiState.value.copy(
                        progress = newProgress?.copy(
                            completedSteps = newCompleted,
                            progressPercentage = percentage
                        ),
                        selectedStep = null,
                        errorMessage = null
                    )
                }
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    errorMessage = "No se pudo guardar el paso"
                )
                is Result.NetworkError -> _uiState.value = _uiState.value.copy(
                    errorMessage = "Error de conexión"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
