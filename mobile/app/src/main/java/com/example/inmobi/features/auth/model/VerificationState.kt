package com.example.inmobi.features.auth.model

sealed class VerificationState {
    data object Idle : VerificationState()
    data object Resending : VerificationState()
    data class ResentSuccessfully(val message: String) : VerificationState()
    data class Error(val message: String) : VerificationState()
}
