package com.example.inmobi.features.legal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.inmobi.features.legal.data.LegalAdviceRepository
import com.example.inmobi.features.legal.data.dto.LegalAdviceRequestListDto
import com.example.inmobi.features.properties.data.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LegalWelcomeUiState(
    val isLoading: Boolean = false,
    val requests: List<LegalAdviceRequestListDto> = emptyList(),
    val error: String? = null,
    val acceptingRequestId: Int? = null,
    val acceptedChatId: Int? = null
)

class LegalWelcomeViewModel(
    private val repository: LegalAdviceRepository = LegalAdviceRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LegalWelcomeUiState())
    val uiState: StateFlow<LegalWelcomeUiState> = _uiState.asStateFlow()

    init {
        loadRequests()
    }

    fun loadRequests() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = repository.getRequests()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        requests = result.data,
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

    fun acceptRequest(requestId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(acceptingRequestId = requestId, error = null)

            when (val result = repository.acceptRequest(requestId)) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        acceptingRequestId = null,
                        acceptedChatId = result.data.chatId
                    )
                    loadRequests()
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        acceptingRequestId = null,
                        error = "Error ${result.code}: ${result.message}"
                    )
                }
                is Result.NetworkError -> {
                    _uiState.value = _uiState.value.copy(
                        acceptingRequestId = null,
                        error = "Error de red. Verifica tu conexión."
                    )
                }
            }
        }
    }

    fun clearAcceptedChat() {
        _uiState.value = _uiState.value.copy(acceptedChatId = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LegalWelcomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LegalWelcomeViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
