package com.example.inmobi.features.properties.ui.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inmobi.features.properties.data.PropertiesRepository
import com.example.inmobi.features.properties.data.Result
import com.example.inmobi.features.properties.domain.Property
import com.example.inmobi.features.properties.domain.toDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ManagePropertyUiState(
    val isLoading: Boolean = false,
    val property: Property? = null,
    val error: String? = null,
    val deleted: Boolean = false
)

class ManagePropertyViewModel : ViewModel() {
    private val repository = PropertiesRepository()
    
    private val _uiState = MutableStateFlow(ManagePropertyUiState(isLoading = true))
    val uiState: StateFlow<ManagePropertyUiState> = _uiState.asStateFlow()

    fun loadProperty(propertyId: Int) {
        _uiState.value = ManagePropertyUiState(isLoading = true)
        viewModelScope.launch {
            when (val result = repository.getProperty(propertyId)) {
                is Result.Success -> _uiState.value = ManagePropertyUiState(
                    isLoading = false,
                    property = result.data.toDomain()
                )
                is Result.Error -> _uiState.value = ManagePropertyUiState(
                    isLoading = false,
                    error = "Error al cargar propiedad: ${result.message}"
                )
                is Result.NetworkError -> _uiState.value = ManagePropertyUiState(
                    isLoading = false,
                    error = "Error de conexión"
                )
            }
        }
    }

    fun deleteProperty() {
        val propertyId = _uiState.value.property?.id ?: return
        viewModelScope.launch {
            when (repository.deleteProperty(propertyId)) {
                is Result.Success -> _uiState.value = _uiState.value.copy(deleted = true)
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    error = "Error al eliminar propiedad"
                )
                is Result.NetworkError -> _uiState.value = _uiState.value.copy(
                    error = "Error de conexión"
                )
            }
        }
    }
}
