package com.example.inmobi.features.properties.ui.sell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inmobi.features.properties.data.PropertiesRepository
import com.example.inmobi.features.properties.data.Result
import com.example.inmobi.features.properties.domain.OperationType
import com.example.inmobi.features.properties.domain.Property
import com.example.inmobi.features.properties.domain.toDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SellPropertiesUiState(
    val isLoading: Boolean = false,
    val properties: List<Property> = emptyList(),
    val error: String? = null
)

class SellPropertiesViewModel : ViewModel() {

    private val repository = PropertiesRepository()
    private val _uiState = MutableStateFlow(SellPropertiesUiState(isLoading = true))
    val uiState: StateFlow<SellPropertiesUiState> = _uiState.asStateFlow()

    fun loadProperties() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            when (val result = repository.myProperties(
                operationType = OperationType.SALE.apiName
            )) {
                is Result.Success -> _uiState.value = SellPropertiesUiState(
                    isLoading = false,
                    properties = result.data.toDomain()
                )
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar propiedades: ${result.message}"
                )
                is Result.NetworkError -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error de conexión"
                )
            }
        }
    }
}
