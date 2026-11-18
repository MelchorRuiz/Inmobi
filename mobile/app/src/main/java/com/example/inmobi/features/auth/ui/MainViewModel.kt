package com.example.inmobi.features.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.inmobi.features.auth.data.AuthRepository
import com.example.inmobi.features.auth.data.dto.UserResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainState(
    val user: UserResponse? = null,
    val isLoading: Boolean = false,
    val isLoggingOut: Boolean = false,
    val logoutFailed: Boolean = false,
    val error: String? = null
)

class MainViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            repository.getUserProfile()
                .onSuccess { user ->
                    _state.update { it.copy(isLoading = false, user = user, error = null) }
                }
                .onFailure { throwable ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "Error al cargar perfil"
                        )
                    }
                }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        if (_state.value.isLoggingOut) return

        viewModelScope.launch {
            _state.update { it.copy(isLoggingOut = true, logoutFailed = false, error = null) }

            repository.logout()
                .onSuccess {
                    _state.update { it.copy(isLoggingOut = false) }
                    onSuccess()
                }
                .onFailure { throwable ->
                    _state.update {
                        it.copy(
                            isLoggingOut = false,
                            logoutFailed = true,
                            error = throwable.message ?: "Error al cerrar sesión"
                        )
                    }
                }
        }
    }

    class Factory(private val repository: AuthRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(repository) as T
        }
    }
}
