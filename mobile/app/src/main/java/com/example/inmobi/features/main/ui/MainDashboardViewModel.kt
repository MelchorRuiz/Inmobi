package com.example.inmobi.features.main.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.inmobi.features.main.data.DashboardRepository
import com.example.inmobi.features.properties.data.Result
import com.example.inmobi.features.properties.domain.Property
import com.example.inmobi.features.properties.domain.toDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean = false,
    val interestedProperties: List<Property> = emptyList(),
    val publishedProperties: List<Property> = emptyList(),
    val error: String? = null
)

class MainDashboardViewModel(
    private val repository: DashboardRepository = DashboardRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = repository.getDashboard()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        interestedProperties = result.data.interestedProperties.toDomain(),
                        publishedProperties = result.data.myPublishedProperties.toDomain()
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

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainDashboardViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainDashboardViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
