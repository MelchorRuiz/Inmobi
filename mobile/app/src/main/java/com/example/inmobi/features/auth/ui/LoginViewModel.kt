package com.example.inmobi.features.auth.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.inmobi.core.auth.GoogleSignInManager
import com.example.inmobi.features.auth.data.AuthRepository
import com.example.inmobi.features.auth.model.LoginException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginErrorState(
    val message: String? = null,
    val showResendVerification: Boolean = false,
    val resendEmail: String = ""
)

data class LoginFormState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val serverError: LoginErrorState? = null,
    val isLoading: Boolean = false,
    val isGoogleLoading: Boolean = false,
    val googleError: String? = null
)

class LoginViewModel(
    private val repository: AuthRepository,
    private val googleSignInManager: GoogleSignInManager
) : ViewModel() {

    private val _formState = MutableStateFlow(LoginFormState())
    val formState: StateFlow<LoginFormState> = _formState.asStateFlow()

    fun onEmailChange(value: String) {
        _formState.update { it.copy(email = value, emailError = validateEmail(value), serverError = null) }
    }

    fun onPasswordChange(value: String) {
        _formState.update { it.copy(password = value, passwordError = validatePassword(value), serverError = null) }
    }

    fun login(onSuccess: (role: String) -> Unit) {
        val state = _formState.value
        
        val emailError = when {
            state.email.isBlank() -> "El correo electrónico es obligatorio"
            else -> validateEmail(state.email)
        }
        
        val passwordError = when {
            state.password.isBlank() -> "La contraseña es obligatoria"
            else -> validatePassword(state.password)
        }

        if (emailError != null || passwordError != null) {
            _formState.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError
                )
            }
            return
        }

        viewModelScope.launch {
            _formState.update { it.copy(isLoading = true, serverError = null) }

            repository.login(state.email, state.password)
                .onSuccess { session ->
                    resetForm()
                    onSuccess(session.role)
                }
                .onFailure { throwable ->
                    val errorState = when (throwable) {
                        is LoginException.InvalidCredentials -> LoginErrorState(
                            message = throwable.message
                        )
                        is LoginException.EmailNotVerified -> LoginErrorState(
                            message = throwable.message,
                            showResendVerification = true,
                            resendEmail = throwable.email
                        )
                        is LoginException.AccountDeactivated -> LoginErrorState(
                            message = throwable.message
                        )
                        is LoginException.Generic -> LoginErrorState(
                            message = throwable.message
                        )
                        else -> LoginErrorState(
                            message = throwable.message ?: "Error desconocido"
                        )
                    }
                    _formState.update {
                        it.copy(isLoading = false, serverError = errorState)
                    }
                }
        }
    }

    fun googleSignIn(context: Context, onSuccess: (role: String) -> Unit, onProfileIncomplete: () -> Unit) {
        viewModelScope.launch {
            _formState.update { it.copy(isGoogleLoading = true, googleError = null) }

            googleSignInManager.signIn()
                .mapCatching { idToken ->
                    repository.googleLogin(idToken).getOrThrow()
                }
                .onSuccess { session ->
                    resetForm()
                    if (session.isProfileComplete) {
                        onSuccess(session.role)
                    } else {
                        onProfileIncomplete()
                    }
                }
                .onFailure { throwable ->
                    val message = when (throwable) {
                        is LoginException.AccountDeactivated -> "Esta cuenta está desactivada"
                        is LoginException.Generic -> throwable.message
                        else -> throwable.message ?: "Error al iniciar sesión con Google"
                    }
                    _formState.update {
                        it.copy(isGoogleLoading = false, googleError = message)
                    }
                }
        }
    }

    private fun resetForm() {
        _formState.value = LoginFormState()
    }

    private fun validateEmail(value: String): String? {
        if (value.isBlank()) return null
        val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return if (!emailPattern.matches(value.trim())) "Correo electrónico inválido" else null
    }

    private fun validatePassword(value: String): String? {
        if (value.isBlank()) return null
        return if (value.length < 8) "La contraseña debe tener al menos 8 caracteres" else null
    }

    class Factory(
        private val repository: AuthRepository,
        private val googleSignInManager: GoogleSignInManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LoginViewModel(repository, googleSignInManager) as T
        }
    }
}
