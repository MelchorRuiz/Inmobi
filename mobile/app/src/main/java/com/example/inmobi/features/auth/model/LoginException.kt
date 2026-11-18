package com.example.inmobi.features.auth.model

sealed class LoginException(message: String) : Exception(message) {
    data object InvalidCredentials : LoginException("Correo o contraseña incorrectos")
    data class EmailNotVerified(val email: String) : LoginException("Por favor verifica tu correo electrónico primero")
    data object AccountDeactivated : LoginException("Esta cuenta está desactivada")
    data class Generic(override val message: String) : LoginException(message)
}
