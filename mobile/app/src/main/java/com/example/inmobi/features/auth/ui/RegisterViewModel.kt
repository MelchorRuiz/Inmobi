package com.example.inmobi.features.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.inmobi.features.auth.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

data class RegisterFormState(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val birthDate: String = "",
    val preference: String = "",
    val privacyAccepted: Boolean = false,
    val touchedFields: Set<String> = emptySet(),
    val errors: Map<String, String?> = emptyMap(),
    val isSubmitting: Boolean = false,
    val registeredEmail: String? = null
)

class RegisterViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _formState = MutableStateFlow(RegisterFormState())
    val formState: StateFlow<RegisterFormState> = _formState.asStateFlow()

    fun onFullNameChange(value: String) {
        _formState.update { it.copy(fullName = value) }
        markFieldTouched("fullName")
        validateField("fullName", value)
    }

    fun onEmailChange(value: String) {
        _formState.update { it.copy(email = value) }
        markFieldTouched("email")
        validateField("email", value)
        clearServerErrors()
    }

    fun onPhoneChange(value: String) {
        _formState.update { it.copy(phone = value) }
        markFieldTouched("phone")
        validateField("phone", value)
        clearServerErrors()
    }

    fun onPasswordChange(value: String) {
        _formState.update { it.copy(password = value) }
        markFieldTouched("password")
        validateField("password", value)
        if (_formState.value.touchedFields.contains("confirmPassword")) {
            validateField("confirmPassword", _formState.value.confirmPassword)
        }
    }

    fun onConfirmPasswordChange(value: String) {
        _formState.update { it.copy(confirmPassword = value) }
        markFieldTouched("confirmPassword")
        validateField("confirmPassword", value)
    }

    fun onBirthDateChange(value: String) {
        _formState.update { it.copy(birthDate = value) }
        markFieldTouched("birthDate")
        validateField("birthDate", value)
    }

    fun onPreferenceChange(value: String) {
        _formState.update { it.copy(preference = value) }
        markFieldTouched("preference")
        validateField("preference", value)
    }

    fun onPrivacyAcceptedChange(value: Boolean) {
        _formState.update { it.copy(privacyAccepted = value) }
        markFieldTouched("privacyAccepted")
        validateField("privacyAccepted", value)
    }

    private fun markFieldTouched(field: String) {
        _formState.update { it.copy(touchedFields = it.touchedFields + field) }
    }

    private fun validateField(field: String, value: Any) {
        val error = when (field) {
            "fullName" -> validateFullName(value as String)
            "email" -> validateEmail(value as String)
            "phone" -> validatePhone(value as String)
            "password" -> validatePassword(value as String)
            "confirmPassword" -> validateConfirmPassword(value as String)
            "birthDate" -> validateBirthDate(value as String)
            "preference" -> validatePreference(value as String)
            "privacyAccepted" -> validatePrivacyAccepted(value as Boolean)
            else -> null
        }

        _formState.update {
            it.copy(errors = it.errors + (field to error))
        }
    }

    private fun validateFullName(value: String): String? {
        if (value.isBlank()) return null
        if (!isValidFullName(value)) return "Ingresa tu nombre y apellido"
        return null
    }

    private fun validateEmail(value: String): String? {
        if (value.isBlank()) return null
        if (!isValidEmail(value)) return "Correo electrónico inválido"
        return null
    }

    private fun validatePhone(value: String): String? {
        if (value.isBlank()) return null
        if (!isValidMexicanPhone(value)) return "Teléfono mexicano inválido (10 dígitos)"
        return null
    }

    private fun validatePassword(value: String): String? {
        if (value.isBlank()) return null
        if (value.length < 8) return "La contraseña debe tener al menos 8 caracteres"
        return null
    }

    private fun validateConfirmPassword(value: String): String? {
        if (value.isBlank()) return null
        if (value != _formState.value.password) return "Las contraseñas no coinciden"
        return null
    }

    private fun validateBirthDate(value: String): String? {
        if (value.isBlank()) return null
        if (!isAdult(value)) return "Debes ser mayor de 18 años"
        return null
    }

    private fun validatePreference(value: String): String? {
        if (value.isBlank()) return null
        return null
    }

    private fun validatePrivacyAccepted(value: Boolean): String? {
        if (!value) return null
        return null
    }

    private fun clearServerErrors() {
        _formState.update {
            it.copy(errors = it.errors - "general" - "email" - "phone")
        }
    }

    private fun validateAllFields(): Boolean {
        val state = _formState.value
        val allTouched = setOf(
            "fullName", "email", "phone", "password",
            "confirmPassword", "birthDate", "preference", "privacyAccepted"
        )

        _formState.update {
            it.copy(
                touchedFields = allTouched,
                errors = it.errors + mapOf(
                    "fullName" to if (state.fullName.isBlank()) "El nombre completo es requerido" else validateFullName(state.fullName),
                    "email" to if (state.email.isBlank()) "El correo electrónico es requerido" else validateEmail(state.email),
                    "phone" to if (state.phone.isBlank()) "El teléfono es requerido" else validatePhone(state.phone),
                    "password" to if (state.password.isBlank()) "La contraseña es requerida" else validatePassword(state.password),
                    "confirmPassword" to if (state.confirmPassword.isBlank()) "Confirma tu contraseña" else validateConfirmPassword(state.confirmPassword),
                    "birthDate" to if (state.birthDate.isBlank()) "La fecha de nacimiento es requerida" else validateBirthDate(state.birthDate),
                    "preference" to if (state.preference.isBlank()) "Selecciona una preferencia" else null,
                    "privacyAccepted" to if (!state.privacyAccepted) "Debes aceptar la Política de Privacidad" else null
                )
            )
        }

        val hasErrors = _formState.value.errors.values.any { it != null }
        return !hasErrors
    }

    fun register() {
        if (!validateAllFields()) return

        val state = _formState.value
        _formState.update { it.copy(isSubmitting = true) }

        viewModelScope.launch {
            repository.register(
                fullName = state.fullName,
                email = state.email,
                phone = state.phone,
                password = state.password,
                birthDate = state.birthDate,
                preference = state.preference,
                privacyAccepted = state.privacyAccepted
            )
                .onSuccess {
                    _formState.update {
                        it.copy(
                            isSubmitting = false,
                            registeredEmail = state.email
                        )
                    }
                }
                .onFailure { error ->
                    val errorMessage = error.message ?: "Error desconocido"
                    val updatedErrors = when {
                        errorMessage.contains("email is already registered", ignoreCase = true) ->
                            mapOf("email" to "Este correo ya está registrado")
                        errorMessage.contains("phone number is already registered", ignoreCase = true) ->
                            mapOf("phone" to "Este teléfono ya está registrado")
                        else -> mapOf("general" to errorMessage)
                    }
                    _formState.update {
                        it.copy(
                            isSubmitting = false,
                            errors = it.errors + updatedErrors
                        )
                    }
                }
        }
    }

    fun resetState() {
        _formState.value = RegisterFormState()
    }

    private fun isValidFullName(name: String): Boolean {
        val parts = name.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
        return parts.size >= 2 && parts.all { it.length >= 2 }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return emailPattern.matches(email.trim())
    }

    private fun isValidMexicanPhone(phone: String): Boolean {
        val cleaned = phone.replace("\\s+".toRegex(), "")
            .replace("+52", "")
            .replace("-", "")
        val mexicanPhonePattern = Regex("^\\d{10}$")
        return mexicanPhonePattern.matches(cleaned)
    }

    private fun isAdult(birthDate: String): Boolean {
        return try {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            val date = LocalDate.parse(birthDate, formatter)
            val age = Period.between(date, LocalDate.now()).years
            age >= 18
        } catch (e: DateTimeParseException) {
            false
        }
    }

    class Factory(private val repository: AuthRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RegisterViewModel(repository) as T
        }
    }
}
