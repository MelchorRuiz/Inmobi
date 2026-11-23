package com.example.inmobi.features.admin.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.inmobi.features.admin.data.LegalStaffCreateRequest
import com.example.inmobi.features.admin.data.LegalStaffRepository
import com.example.inmobi.features.admin.data.LegalStaffResponse
import com.example.inmobi.features.admin.data.LegalStaffUpdateRequest
import com.example.inmobi.features.properties.data.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LegalStaffUiState(
    val isLoading: Boolean = false,
    val staff: List<LegalStaffResponse> = emptyList(),
    val error: String? = null,
    val saving: Boolean = false,
    val savingError: String? = null,
    val lastSavedUserId: Long? = null,
    val lastDeletedUserId: Long? = null
)

class LegalStaffViewModel(
    private val repository: LegalStaffRepository = LegalStaffRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LegalStaffUiState())
    val uiState: StateFlow<LegalStaffUiState> = _uiState.asStateFlow()

    init {
        loadStaff()
    }

    fun loadStaff() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = repository.list()) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, staff = result.data) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = "Error ${result.code}: ${result.message}") }
                }
                is Result.NetworkError -> {
                    _uiState.update { it.copy(isLoading = false, error = "Error de red. Verifica tu conexión.") }
                }
            }
        }
    }

    fun createStaff(
        email: String,
        password: String,
        fullName: String,
        idNumber: String,
        specialty: String,
        firmName: String?,
        phone: String?
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(saving = true, savingError = null) }

            val request = LegalStaffCreateRequest(
                email = email.trim().lowercase(),
                password = password,
                fullName = fullName.trim(),
                idNumber = idNumber.trim(),
                specialty = specialty.trim(),
                firmName = firmName?.trim()?.takeIf { it.isNotEmpty() },
                phone = phone?.trim()?.takeIf { it.isNotEmpty() }
            )

            when (val result = repository.create(request)) {
                is Result.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            saving = false,
                            lastSavedUserId = result.data.id,
                            staff = state.staff + result.data
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(saving = false, savingError = "Error ${result.code}: ${result.message}") }
                }
                is Result.NetworkError -> {
                    _uiState.update { it.copy(saving = false, savingError = "Error de red. Verifica tu conexión.") }
                }
            }
        }
    }

    fun updateStaff(
        userId: Long,
        password: String?,
        fullName: String?,
        idNumber: String?,
        specialty: String?,
        firmName: String?,
        phone: String?
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(saving = true, savingError = null) }

            val request = LegalStaffUpdateRequest(
                password = password?.takeIf { it.isNotBlank() },
                fullName = fullName?.trim()?.takeIf { it.isNotEmpty() },
                idNumber = idNumber?.trim()?.takeIf { it.isNotEmpty() },
                specialty = specialty?.trim()?.takeIf { it.isNotEmpty() },
                firmName = firmName?.trim()?.takeIf { it.isNotEmpty() },
                phone = phone?.trim()?.takeIf { it.isNotEmpty() }
            )

            when (val result = repository.update(userId, request)) {
                is Result.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            saving = false,
                            lastSavedUserId = result.data.id,
                            staff = state.staff.map { if (it.id == userId) result.data else it }
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(saving = false, savingError = "Error ${result.code}: ${result.message}") }
                }
                is Result.NetworkError -> {
                    _uiState.update { it.copy(saving = false, savingError = "Error de red. Verifica tu conexión.") }
                }
            }
        }
    }

    fun deleteStaff(userId: Long) {
        viewModelScope.launch {
            when (val result = repository.delete(userId)) {
                is Result.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            staff = state.staff.filter { it.id != userId },
                            lastDeletedUserId = userId
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(savingError = "Error ${result.code}: ${result.message}") }
                }
                is Result.NetworkError -> {
                    _uiState.update { it.copy(savingError = "Error de red. Verifica tu conexión.") }
                }
            }
        }
    }

    fun clearSavedFlag() {
        _uiState.update { it.copy(lastSavedUserId = null, savingError = null) }
    }

    class Factory : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LegalStaffViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LegalStaffViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
