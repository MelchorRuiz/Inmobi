package com.example.inmobi.features.properties.ui.buy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inmobi.features.properties.data.PropertiesRepository
import com.example.inmobi.features.properties.data.Result
import com.example.inmobi.features.properties.domain.OperationType
import com.example.inmobi.features.properties.domain.Property
import com.example.inmobi.features.properties.domain.toDomain
import com.example.inmobi.features.properties.ui.common.PropertyListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BuyPropertyViewModel : ViewModel() {

    private val repository = PropertiesRepository()
    private val _uiState = MutableStateFlow(PropertyListUiState(isLoading = true))
    val uiState: StateFlow<PropertyListUiState> = _uiState.asStateFlow()

    fun loadProperties() {
        _uiState.value = PropertyListUiState(isLoading = true)
        viewModelScope.launch {
            when (val result = repository.listProperties(
                operationType = OperationType.SALE.apiName,
                listingStatus = "active"
            )) {
                is Result.Success -> _uiState.value = PropertyListUiState(
                    isLoading = false,
                    properties = result.data.toDomain()
                )
                is Result.Error -> _uiState.value = PropertyListUiState(
                    isLoading = false,
                    error = "Error al cargar propiedades: ${result.message}"
                )
                is Result.NetworkError -> _uiState.value = PropertyListUiState(
                    isLoading = false,
                    error = "Error de conexión"
                )
            }
        }
    }
}
