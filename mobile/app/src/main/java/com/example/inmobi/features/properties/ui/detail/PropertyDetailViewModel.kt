package com.example.inmobi.features.properties.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inmobi.features.journey.data.JourneyRepository
import com.example.inmobi.features.properties.data.PropertiesRepository
import com.example.inmobi.features.properties.data.Result
import com.example.inmobi.features.properties.domain.Property
import com.example.inmobi.features.properties.domain.toDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class InterestState {
    NONE,
    LOADING,
    REGISTERED,
    ERROR
}

data class PropertyDetailUiState(
    val isLoading: Boolean = false,
    val property: Property? = null,
    val error: String? = null,
    val interestState: InterestState = InterestState.NONE,
    val interestError: String? = null
)

class PropertyDetailViewModel : ViewModel() {

    private val repository = PropertiesRepository()
    private val journeyRepository = JourneyRepository()
    private val _uiState = MutableStateFlow(PropertyDetailUiState(isLoading = true))
    val uiState: StateFlow<PropertyDetailUiState> = _uiState.asStateFlow()

    fun loadProperty(propertyId: Int) {
        _uiState.value = PropertyDetailUiState(isLoading = true)
        viewModelScope.launch {
            when (val result = repository.getProperty(propertyId)) {
                is Result.Success -> {
                    _uiState.value = PropertyDetailUiState(
                        isLoading = false,
                        property = result.data.toDomain()
                    )
                    checkInterest(propertyId)
                }
                is Result.Error -> _uiState.value = PropertyDetailUiState(
                    isLoading = false,
                    error = "Error al cargar propiedad: ${result.message}"
                )
                is Result.NetworkError -> _uiState.value = PropertyDetailUiState(
                    isLoading = false,
                    error = "Error de conexión"
                )
            }
        }
    }

    private suspend fun checkInterest(propertyId: Int) {
        when (val result = journeyRepository.getProgress(propertyId)) {
            is Result.Success -> _uiState.value = _uiState.value.copy(
                interestState = InterestState.REGISTERED
            )
            is Result.Error -> {
                if (result.code == 404) {
                    _uiState.value = _uiState.value.copy(interestState = InterestState.NONE)
                }
            }
            is Result.NetworkError -> { /* Ignore, keep default NONE */ }
        }
    }

    fun registerInterest(propertyId: Int) {
        _uiState.value = _uiState.value.copy(
            interestState = InterestState.LOADING,
            interestError = null
        )
        viewModelScope.launch {
            when (val result = journeyRepository.registerInterest(propertyId)) {
                is Result.Success -> _uiState.value = _uiState.value.copy(
                    interestState = InterestState.REGISTERED
                )
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    interestState = InterestState.ERROR,
                    interestError = if (result.code == 409) "Ya registraste interés en esta propiedad" else "Error al registrar interés"
                )
                is Result.NetworkError -> _uiState.value = _uiState.value.copy(
                    interestState = InterestState.ERROR,
                    interestError = "Error de conexión"
                )
            }
        }
    }

    fun removeInterest(propertyId: Int) {
        _uiState.value = _uiState.value.copy(
            interestState = InterestState.LOADING,
            interestError = null
        )
        viewModelScope.launch {
            when (val result = journeyRepository.removeInterest(propertyId)) {
                is Result.Success -> _uiState.value = _uiState.value.copy(
                    interestState = InterestState.NONE
                )
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    interestState = InterestState.ERROR,
                    interestError = "Error al quitar interés"
                )
                is Result.NetworkError -> _uiState.value = _uiState.value.copy(
                    interestState = InterestState.ERROR,
                    interestError = "Error de conexión"
                )
            }
        }
    }

    fun deleteProperty(propertyId: Int, onDeleted: () -> Unit) {
        viewModelScope.launch {
            when (repository.deleteProperty(propertyId)) {
                is Result.Success -> onDeleted()
                is Result.Error, is Result.NetworkError -> _uiState.value = _uiState.value.copy(
                    error = "Error al eliminar propiedad"
                )
            }
        }
    }
}
