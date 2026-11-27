package com.example.inmobi.features.settings.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.inmobi.core.util.UrlResolver
import com.example.inmobi.features.auth.data.AuthRepository
import com.example.inmobi.features.auth.data.dto.UserResponse
import com.example.inmobi.features.settings.data.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsState(
    val user: UserResponse? = null,
    val isLoading: Boolean = false,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val isUploadingAvatar: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val fullName: String = "",
    val birthDate: String = "",
    val phone: String = "",
    val preference: String? = null,
    val privacyAccepted: Boolean? = null,
    val idNumber: String = "",
    val specialty: String = "",
    val firmName: String = "",
    val staffPhone: String = ""
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            authRepository.getUserProfile()
                .onSuccess { user ->
                    _state.update { it.copy(isLoading = false, user = user, error = null) }
                }
                .onFailure { throwable ->
                    _state.update {
                        it.copy(isLoading = false, error = throwable.message ?: "Error al cargar perfil")
                    }
                }
        }
    }

    fun toggleEditMode() {
        val current = _state.value
        if (current.isEditing) {
            _state.update { it.copy(isEditing = false) }
        } else {
            populateFormFields(current.user)
            _state.update { it.copy(isEditing = true, error = null, successMessage = null) }
        }
    }

    fun cancelEdit() {
        _state.update { it.copy(isEditing = false, error = null) }
    }

    fun updateFullName(value: String) {
        _state.update { it.copy(fullName = value) }
    }

    fun updateBirthDate(value: String) {
        _state.update { it.copy(birthDate = value) }
    }

    fun updatePhone(value: String) {
        _state.update { it.copy(phone = value) }
    }

    fun updatePreference(value: String?) {
        _state.update { it.copy(preference = value) }
    }

    fun updatePrivacyAccepted(value: Boolean?) {
        _state.update { it.copy(privacyAccepted = value) }
    }

    fun updateIdNumber(value: String) {
        _state.update { it.copy(idNumber = value) }
    }

    fun updateSpecialty(value: String) {
        _state.update { it.copy(specialty = value) }
    }

    fun updateFirmName(value: String) {
        _state.update { it.copy(firmName = value) }
    }

    fun updateStaffPhone(value: String) {
        _state.update { it.copy(staffPhone = value) }
    }

    fun saveProfile() {
        val state = _state.value
        val user = state.user ?: return

        val validationError = when (user.role) {
            "user" -> validateUserProfile(state)
            "legal_staff" -> validateLegalStaffProfile(state)
            else -> "Los administradores no pueden actualizar su perfil"
        }

        if (validationError != null) {
            _state.update { it.copy(error = validationError) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null, successMessage = null) }

            val result = when (user.role) {
                "user" -> settingsRepository.updateUserProfile(
                    fullName = state.fullName.ifBlank { null },
                    birthDate = state.birthDate.ifBlank { null },
                    phone = state.phone.ifBlank { null },
                    preference = state.preference,
                    privacyAccepted = state.privacyAccepted
                )
                "legal_staff" -> settingsRepository.updateLegalStaffProfile(
                    fullName = state.fullName.ifBlank { null },
                    idNumber = state.idNumber.ifBlank { null },
                    specialty = state.specialty.ifBlank { null },
                    firmName = state.firmName.ifBlank { null },
                    phone = state.staffPhone.ifBlank { null }
                )
                else -> {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            error = "Los administradores no pueden actualizar su perfil"
                        )
                    }
                    return@launch
                }
            }

            result
                .onSuccess { updatedUser ->
                    _state.update {
                        it.copy(
                            isSaving = false,
                            isEditing = false,
                            user = updatedUser,
                            successMessage = "Perfil actualizado correctamente"
                        )
                    }
                }
                .onFailure { throwable ->
                    _state.update {
                        it.copy(isSaving = false, error = translateBackendError(throwable.message))
                    }
                }
        }
    }

    private fun validateUserProfile(state: SettingsState): String? {
        if (state.fullName.isBlank()) {
            return "El nombre completo es obligatorio"
        }
        if (state.birthDate.isBlank()) {
            return "La fecha de nacimiento es obligatoria"
        }
        
        // Validar edad mínima de 18 años
        try {
            val birthDate = java.time.LocalDate.parse(state.birthDate)
            val today = java.time.LocalDate.now()
            val age = today.year - birthDate.year - 
                (if (today.monthValue < birthDate.monthValue || 
                     (today.monthValue == birthDate.monthValue && today.dayOfMonth < birthDate.dayOfMonth)) 1 else 0)
            if (age < 18) {
                return "Debes tener al menos 18 años"
            }
        } catch (e: Exception) {
            return "Fecha de nacimiento inválida"
        }
        
        if (state.phone.isBlank()) {
            return "El teléfono es obligatorio"
        }
        if (state.phone.length != 10 || !state.phone.all { it.isDigit() }) {
            return "El teléfono debe tener exactamente 10 dígitos"
        }
        if (state.preference.isNullOrBlank()) {
            return "La preferencia es obligatoria"
        }
        return null
    }

    private fun validateLegalStaffProfile(state: SettingsState): String? {
        if (state.fullName.isBlank()) {
            return "El nombre completo es obligatorio"
        }
        if (state.idNumber.isBlank()) {
            return "El número de identificación es obligatorio"
        }
        if (state.specialty.isBlank()) {
            return "La especialidad es obligatoria"
        }
        if (state.firmName.isBlank()) {
            return "El despacho es obligatorio"
        }
        if (state.staffPhone.isBlank()) {
            return "El teléfono es obligatorio"
        }
        if (state.staffPhone.length != 10 || !state.staffPhone.all { it.isDigit() }) {
            return "El teléfono debe tener exactamente 10 dígitos"
        }
        return null
    }

    private fun translateBackendError(error: String?): String {
        if (error == null) return "Error al guardar"
        
        return when {
            error.contains("phone number is already registered", ignoreCase = true) ->
                "El número de teléfono ya está registrado"
            error.contains("email", ignoreCase = true) && error.contains("already", ignoreCase = true) ->
                "El correo electrónico ya está registrado"
            error.contains("id number", ignoreCase = true) && error.contains("already", ignoreCase = true) ->
                "El número de identificación ya está registrado"
            error.contains("date of birth cannot be in the future", ignoreCase = true) ->
                "La fecha de nacimiento no puede ser en el futuro"
            error.contains("you must be at least 18 years old", ignoreCase = true) ->
                "Debes tener al menos 18 años"
            error.contains("administrators cannot", ignoreCase = true) ->
                "Los administradores no pueden actualizar su perfil"
            error.contains("current password is incorrect", ignoreCase = true) ->
                "La contraseña actual es incorrecta"
            error.contains("does not have a password", ignoreCase = true) ->
                "Esta cuenta no tiene contraseña configurada"
            error.contains("already has a password", ignoreCase = true) ->
                "Esta cuenta ya tiene contraseña"
            else -> error
        }
    }

    fun uploadAvatar(uri: Uri, context: Context) {
        viewModelScope.launch {
            _state.update { it.copy(isUploadingAvatar = true, error = null, successMessage = null) }

            val tempFile = try {
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: throw Exception("No se pudo abrir la imagen")
                val file = java.io.File(context.cacheDir, "avatar_temp_${System.currentTimeMillis()}.jpg")
                file.outputStream().use { output -> inputStream.copyTo(output) }
                file
            } catch (e: Exception) {
                _state.update {
                    it.copy(isUploadingAvatar = false, error = "No se pudo preparar la imagen")
                }
                return@launch
            }

            settingsRepository.uploadAvatar(tempFile)
                .onSuccess { url ->
                    val updatedUser = _state.value.user?.copy(avatarUrl = url)
                    _state.update {
                        it.copy(
                            isUploadingAvatar = false,
                            user = updatedUser,
                            successMessage = "Foto de perfil actualizada"
                        )
                    }
                }
                .onFailure { throwable ->
                    _state.update {
                        it.copy(isUploadingAvatar = false, error = throwable.message ?: "Error al subir foto")
                    }
                }

            tempFile.delete()
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _state.update { it.copy(error = null, successMessage = null) }
            settingsRepository.changePassword(currentPassword, newPassword)
                .onSuccess {
                    _state.update { it.copy(successMessage = "Contraseña cambiada correctamente") }
                }
                .onFailure { throwable ->
                    _state.update { it.copy(error = translateBackendError(throwable.message)) }
                }
        }
    }

    fun setPassword(newPassword: String) {
        viewModelScope.launch {
            _state.update { it.copy(error = null, successMessage = null) }
            settingsRepository.setPassword(newPassword)
                .onSuccess {
                    val updatedUser = _state.value.user?.copy(hasPassword = true)
                    _state.update {
                        it.copy(
                            user = updatedUser,
                            successMessage = "Contraseña creada correctamente"
                        )
                    }
                }
                .onFailure { throwable ->
                    _state.update { it.copy(error = translateBackendError(throwable.message)) }
                }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
                .onSuccess { onSuccess() }
                .onFailure { onSuccess() }
        }
    }

    fun clearMessage() {
        _state.update { it.copy(error = null, successMessage = null) }
    }

    private fun populateFormFields(user: UserResponse?) {
        if (user == null) return
        _state.update {
            when (user.role) {
                "user" -> it.copy(
                    fullName = user.fullName ?: "",
                    birthDate = user.birthDate ?: "",
                    phone = user.endUser?.phone ?: "",
                    preference = user.endUser?.preference,
                    privacyAccepted = user.endUser?.privacyAccepted
                )
                "legal_staff" -> it.copy(
                    fullName = user.fullName ?: "",
                    idNumber = user.legalStaff?.idNumber ?: "",
                    specialty = user.legalStaff?.specialty ?: "",
                    firmName = user.legalStaff?.firmName ?: "",
                    staffPhone = user.legalStaff?.phone ?: ""
                )
                else -> it
            }
        }
    }

    class Factory(
        private val settingsRepository: SettingsRepository,
        private val authRepository: AuthRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(settingsRepository, authRepository) as T
        }
    }
}
