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

data class ProfileCompletionFormState(
    val phone: String = "",
    val birthDate: String = "",
    val preference: String = "",
    val privacyAccepted: Boolean = false,
    val phoneError: String? = null,
    val birthDateError: String? = null,
    val preferenceError: String? = null,
    val privacyError: String? = null,
    val serverError: String? = null,
    val isSubmitting: Boolean = false
)

class ProfileCompletionViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _formState = MutableStateFlow(ProfileCompletionFormState())
    val formState: StateFlow<ProfileCompletionFormState> = _formState.asStateFlow()

    fun onPhoneChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }
        _formState.update {
            it.copy(phone = digitsOnly, phoneError = null, serverError = null)
        }
    }

    fun onBirthDateChange(value: String) {
        _formState.update { it.copy(birthDate = value, birthDateError = null, serverError = null) }
    }

    fun onPreferenceChange(value: String) {
        _formState.update { it.copy(preference = value, preferenceError = null, serverError = null) }
    }

    fun onPrivacyAcceptedChange(value: Boolean) {
        _formState.update { it.copy(privacyAccepted = value, privacyError = null, serverError = null) }
    }

    fun completeProfile(onSuccess: () -> Unit) {
        val state = _formState.value
        val phoneError = validatePhone(state.phone)
        val birthDateError = validateBirthDate(state.birthDate)
        val preferenceError = validatePreference(state.preference)
        val privacyError = validatePrivacy(state.privacyAccepted)

        if (phoneError != null || birthDateError != null || preferenceError != null || privacyError != null) {
            _formState.update {
                it.copy(
                    phoneError = phoneError,
                    birthDateError = birthDateError,
                    preferenceError = preferenceError,
                    privacyError = privacyError
                )
            }
            return
        }

        viewModelScope.launch {
            _formState.update { it.copy(isSubmitting = true, serverError = null) }

            repository.completeProfile(
                phone = state.phone,
                birthDate = state.birthDate,
                privacyAccepted = true,
                preference = state.preference
            )
                .onSuccess {
                    onSuccess()
                }
                .onFailure { throwable ->
                    val errorMessage = throwable.message ?: ""
                    if (errorMessage.contains("phone number is already registered", ignoreCase = true)) {
                        _formState.update { it.copy(isSubmitting = false, phoneError = "Este numero ya esta registrado") }
                    } else {
                        _formState.update { it.copy(isSubmitting = false, serverError = errorMessage) }
                    }
                }
        }
    }

    private fun validatePhone(value: String): String? {
        if (value.isBlank()) return "El telefono es obligatorio"
        if (value.length != 10) return "El telefono debe tener 10 digitos"
        return null
    }

    private fun validateBirthDate(value: String): String? {
        if (value.isBlank()) return "La fecha de nacimiento es obligatoria"
        if (!isAdult(value)) return "Debes ser mayor de 18 anos"
        return null
    }

    private fun validatePreference(value: String): String? {
        if (value.isBlank()) return "Selecciona una preferencia"
        return null
    }

    private fun validatePrivacy(value: Boolean): String? {
        if (!value) return "Debes aceptar la Politica de Privacidad"
        return null
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
            return ProfileCompletionViewModel(repository) as T
        }
    }
}
