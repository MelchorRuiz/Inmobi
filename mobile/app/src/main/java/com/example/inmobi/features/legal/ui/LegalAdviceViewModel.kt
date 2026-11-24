package com.example.inmobi.features.legal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.inmobi.features.legal.data.LegalAdviceRepository
import com.example.inmobi.features.legal.data.dto.InterestedPropertyDto
import com.example.inmobi.features.properties.data.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LegalAdviceUiState(
    val isLoading: Boolean = false,
    val properties: List<InterestedPropertyDto> = emptyList(),
    val error: String? = null,
    val isSubmitting: Boolean = false,
    val submitSuccess: Boolean = false
)

class LegalAdviceViewModel(
    private val repository: LegalAdviceRepository = LegalAdviceRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LegalAdviceUiState())
    val uiState: StateFlow<LegalAdviceUiState> = _uiState.asStateFlow()

    init {
        loadProperties()
    }

    fun loadProperties() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = repository.getInterestedProperties()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        properties = result.data,
                        error = null
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error ${result.code}: ${result.message}"
                    )
                }
                is Result.NetworkError -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error de red. Verifica tu conexión."
                    )
                }
            }
        }
    }

    fun submitRequest(propertyId: Int, description: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, error = null)

            when (val result = repository.createLegalAdviceRequest(propertyId, description)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        submitSuccess = true
                    )
                    loadProperties()
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        error = "Error ${result.code}: ${result.message}"
                    )
                }
                is Result.NetworkError -> {
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        error = "Error de red. Verifica tu conexión."
                    )
                }
            }
        }
    }

    fun resetSubmitSuccess() {
        _uiState.value = _uiState.value.copy(submitSuccess = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LegalAdviceViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LegalAdviceViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
