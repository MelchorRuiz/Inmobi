package com.example.inmobi.features.properties.ui.lease

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inmobi.features.properties.data.PropertiesRepository
import com.example.inmobi.features.properties.data.Result
import com.example.inmobi.features.properties.domain.OperationType
import com.example.inmobi.features.properties.domain.Property
import com.example.inmobi.features.properties.domain.toDomain
import com.example.inmobi.features.properties.ui.common.StatusFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LeasePropertiesUiState(
    val isLoading: Boolean = false,
    val properties: List<Property> = emptyList(),
    val error: String? = null
)

class LeasePropertiesViewModel : ViewModel() {

    private val repository = PropertiesRepository()
    private val _uiState = MutableStateFlow(LeasePropertiesUiState(isLoading = true))
    val uiState: StateFlow<LeasePropertiesUiState> = _uiState.asStateFlow()

    fun loadProperties(filter: StatusFilter = StatusFilter.ALL) {
        _uiState.value = LeasePropertiesUiState(isLoading = true)
        viewModelScope.launch {
            when (val result = repository.myProperties(
                operationType = OperationType.RENT.apiName,
                listingStatus = filter.apiValue
            )) {
                is Result.Success -> _uiState.value = LeasePropertiesUiState(
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
