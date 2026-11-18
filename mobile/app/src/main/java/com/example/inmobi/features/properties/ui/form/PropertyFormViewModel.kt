package com.example.inmobi.features.properties.ui.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inmobi.features.properties.data.PropertiesRepository
import com.example.inmobi.features.properties.data.Result
import com.example.inmobi.features.properties.data.dto.PropertyCreateRequest
import com.example.inmobi.features.properties.data.dto.PropertyUpdateRequest
import com.example.inmobi.features.properties.domain.Property
import com.example.inmobi.features.properties.domain.toDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PropertyFormUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val existingProperty: Property? = null,
    val error: String? = null,
    val savedSuccessfully: Boolean = false
)

data class PropertyFormData(
    val listingTitle: String = "",
    val description: String = "",
    val price: Double? = null,
    val currency: String = "MXN",
    val propertyType: String = "house",
    val city: String = "",
    val stateProvince: String = "",
    val bedrooms: Int? = null,
    val fullBathrooms: Int? = null,
    val halfBathrooms: Int? = null,
    val builtAreaSqm: Double? = null,
    val lotSizeSqm: Double? = null,
    val parkingSpaces: Int? = null
)

class PropertyFormViewModel : ViewModel() {

    private val repository = PropertiesRepository()
    private val _uiState = MutableStateFlow(PropertyFormUiState())
    val uiState: StateFlow<PropertyFormUiState> = _uiState.asStateFlow()

    private val _formData = MutableStateFlow(PropertyFormData())
    val formData: StateFlow<PropertyFormData> = _formData.asStateFlow()

    fun initializeForCreate() {
        _uiState.value = PropertyFormUiState()
        _formData.value = PropertyFormData()
    }

    fun loadPropertyForEdit(propertyId: Int) {
        _uiState.value = PropertyFormUiState(isLoading = true)
        viewModelScope.launch {
            when (val result = repository.getProperty(propertyId)) {
                is Result.Success -> {
                    val property = result.data.toDomain()
                    _uiState.value = PropertyFormUiState(
                        isLoading = false,
                        existingProperty = property
                    )
                    _formData.value = PropertyFormData(
                        listingTitle = property.listingTitle,
                        description = property.description ?: "",
                        price = property.price,
                        currency = property.currency,
                        propertyType = property.propertyType,
                        city = property.city,
                        stateProvince = property.stateProvince,
                        bedrooms = property.bedrooms,
                        fullBathrooms = property.fullBathrooms,
                        halfBathrooms = property.halfBathrooms,
                        builtAreaSqm = property.builtAreaSqm,
                        lotSizeSqm = property.lotSizeSqm,
                        parkingSpaces = property.parkingSpaces
                    )
                }
                is Result.Error -> _uiState.value = PropertyFormUiState(
                    error = "Error al cargar propiedad"
                )
                is Result.NetworkError -> _uiState.value = PropertyFormUiState(
                    error = "Error de conexión"
                )
            }
        }
    }

    fun updateField(update: PropertyFormData.() -> PropertyFormData) {
        _formData.value = _formData.value.update()
    }

    fun saveProperty(operationType: String, propertyId: Int? = null) {
        val data = _formData.value
        if (!validateForm(data)) return

        _uiState.value = _uiState.value.copy(isSaving = true, error = null)

        viewModelScope.launch {
            val result = if (propertyId != null) {
                repository.updateProperty(propertyId, PropertyUpdateRequest(
                    listingTitle = data.listingTitle,
                    description = data.description.ifBlank() ?: null,
                    price = data.price,
                    currency = data.currency,
                    propertyType = data.propertyType,
                    city = data.city,
                    stateProvince = data.stateProvince,
                    bedrooms = data.bedrooms,
                    fullBathrooms = data.fullBathrooms,
                    halfBathrooms = data.halfBathrooms,
                    builtAreaSqm = data.builtAreaSqm,
                    lotSizeSqm = data.lotSizeSqm,
                    parkingSpaces = data.parkingSpaces
                ))
            } else {
                repository.createProperty(PropertyCreateRequest(
                    operationType = operationType,
                    listingTitle = data.listingTitle,
                    description = data.description.ifBlank() ?: null,
                    price = data.price ?: 0.0,
                    currency = data.currency,
                    propertyType = data.propertyType,
                    city = data.city,
                    stateProvince = data.stateProvince,
                    bedrooms = data.bedrooms,
                    fullBathrooms = data.fullBathrooms,
                    halfBathrooms = data.halfBathrooms,
                    builtAreaSqm = data.builtAreaSqm,
                    lotSizeSqm = data.lotSizeSqm,
                    parkingSpaces = data.parkingSpaces
                ))
            }

            when (result) {
                is Result.Success -> _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    savedSuccessfully = true
                )
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Error al guardar: ${result.message}"
                )
                is Result.NetworkError -> _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Error de conexión"
                )
            }
        }
    }

    private fun validateForm(data: PropertyFormData): Boolean {
        return data.listingTitle.isNotBlank() &&
            data.price != null && data.price > 0 &&
            data.city.isNotBlank() &&
            data.stateProvince.isNotBlank()
    }

    private fun String.ifBlank(): String? = ifBlank { null }

    fun clearSavedState() {
        _uiState.value = _uiState.value.copy(savedSuccessfully = false)
    }
}
