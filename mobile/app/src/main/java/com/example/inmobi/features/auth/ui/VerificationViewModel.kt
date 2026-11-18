package com.example.inmobi.features.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.inmobi.features.auth.data.AuthRepository
import com.example.inmobi.features.auth.model.VerificationState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VerificationViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _verificationState = MutableStateFlow<VerificationState>(VerificationState.Idle)
    val verificationState: StateFlow<VerificationState> = _verificationState.asStateFlow()

    private val _cooldownSeconds = MutableStateFlow(90)
    val cooldownSeconds: StateFlow<Int> = _cooldownSeconds.asStateFlow()

    init {
        startCooldown()
    }

    private fun startCooldown() {
        viewModelScope.launch {
            while (_cooldownSeconds.value > 0) {
                delay(1000L)
                _cooldownSeconds.value = _cooldownSeconds.value - 1
            }
        }
    }

    fun resendVerification(email: String) {
        if (_cooldownSeconds.value > 0) return

        viewModelScope.launch {
            _verificationState.value = VerificationState.Resending
            repository.resendVerification(email)
                .onSuccess {
                    _verificationState.value = VerificationState.ResentSuccessfully(
                        "Correo de verificación reenviado exitosamente"
                    )
                    _cooldownSeconds.value = 90
                    startCooldown()
                }
                .onFailure {
                    _verificationState.value = VerificationState.Error(
                        it.message ?: "Error al reenviar correo"
                    )
                }
        }
    }

    fun resetState() {
        _verificationState.value = VerificationState.Idle
    }

    class Factory(private val repository: AuthRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return VerificationViewModel(repository) as T
        }
    }
}
