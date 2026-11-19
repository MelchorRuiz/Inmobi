package com.example.inmobi.features.properties.ui.editsaleform

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inmobi.features.properties.data.PropertiesRepository
import com.example.inmobi.features.properties.data.Result
import com.example.inmobi.features.properties.data.dto.PropertyImageRequest
import com.example.inmobi.features.properties.data.dto.PropertyUpdateRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

data class EditSalePropertyFormState(
    val propertyId: Int? = null,
    val currentStep: Int = 0,
    val listingTitle: String = "",
    val description: String = "",
    val price: String = "",
    val propertyType: String = "house",
    val streetAddress: String = "",
    val neighborhood: String = "",
    val postalCode: String = "",
    val city: String = "",
    val stateProvince: String = "",
    val country: String = "Mexico",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val bedrooms: String = "",
    val fullBathrooms: String = "",
    val halfBathrooms: String = "",
    val builtAreaSqm: String = "",
    val lotSizeSqm: String = "",
    val parkingSpaces: String = "",
    val storiesFloors: String = "",
    val propertyAgeYears: String = "",
    val maintenanceFee: String = "",
    val availableAmenities: List<EditAmenityItem> = emptyList(),
    val selectedAmenityIds: Set<Int> = emptySet(),
    val existingPropertyAmenities: List<String> = emptyList(),
    val existingImages: List<ExistingImage> = emptyList(),
    val newImages: List<NewImage> = emptyList(),
    val removedImageIds: Set<Int> = emptySet(),
    val touchedFields: Set<String> = emptySet(),
    val errors: Map<String, String?> = emptyMap(),
    val isLoading: Boolean = false,
    val isLoadingAmenities: Boolean = false,
    val isUploadingImages: Boolean = false,
    val isSubmitting: Boolean = false,
    val isGeocoding: Boolean = false,
    val submitSuccess: Boolean = false,
    val submitError: String? = null
)

data class EditAmenityItem(
    val id: Int,
    val name: String
)

data class ExistingImage(
    val mediaId: Int,
    val url: String,
    val displayOrder: Int
)

data class NewImage(
    val uri: Uri,
    val displayOrder: Int
)

class EditSalePropertyFormViewModel : ViewModel() {

    private val repository = PropertiesRepository()

    private val _formState = MutableStateFlow(EditSalePropertyFormState())
    val formState: StateFlow<EditSalePropertyFormState> = _formState.asStateFlow()

    fun loadProperty(propertyId: Int) {
        _formState.update { it.copy(propertyId = propertyId, currentStep = 0, isLoading = true) }
        viewModelScope.launch {
            val amenitiesResult = repository.getAmenities()
            val propertyResult = repository.getProperty(propertyId)

            val amenities = when (amenitiesResult) {
                is Result.Success -> amenitiesResult.data.map { EditAmenityItem(it.amenityId, it.name) }
                else -> emptyList()
            }

            when (propertyResult) {
                is Result.Success -> {
                    val property = propertyResult.data
                    val propertyAmenityNames = property.amenities
                    val matchedIds = amenities
                        .filter { it.name in propertyAmenityNames }
                        .map { it.id }
                        .toSet()

                    _formState.update { state ->
                        state.copy(
                            listingTitle = property.listingTitle,
                            description = property.description ?: "",
                            price = property.price.toString(),
                            propertyType = property.propertyType,
                            streetAddress = property.streetAddress ?: "",
                            neighborhood = property.neighborhood ?: "",
                            postalCode = property.postalCode ?: "",
                            city = property.city,
                            stateProvince = property.stateProvince,
                            country = property.country,
                            latitude = property.latitude,
                            longitude = property.longitude,
                            bedrooms = property.bedrooms?.toString() ?: "",
                            fullBathrooms = property.fullBathrooms?.toString() ?: "",
                            halfBathrooms = property.halfBathrooms?.toString() ?: "",
                            builtAreaSqm = property.builtAreaSqm?.toString() ?: "",
                            lotSizeSqm = property.lotSizeSqm?.toString() ?: "",
                            parkingSpaces = property.parkingSpaces?.toString() ?: "",
                            storiesFloors = property.storiesFloors?.toString() ?: "",
                            propertyAgeYears = property.propertyAgeYears?.toString() ?: "",
                            maintenanceFee = property.maintenanceFee?.toString() ?: "",
                            availableAmenities = amenities,
                            selectedAmenityIds = matchedIds,
                            existingPropertyAmenities = propertyAmenityNames,
                            existingImages = property.images.map { img ->
                                ExistingImage(img.mediaId, img.fileUrl, img.displayOrder)
                            }.sortedBy { it.displayOrder },
                            isLoading = false,
                            isLoadingAmenities = false
                        )
                    }
                }
                is Result.Error -> {
                    _formState.update {
                        it.copy(
                            isLoading = false,
                            isLoadingAmenities = false,
                            availableAmenities = amenities,
                            submitError = "Error al cargar: ${propertyResult.message}"
                        )
                    }
                }
                is Result.NetworkError -> {
                    _formState.update {
                        it.copy(
                            isLoading = false,
                            isLoadingAmenities = false,
                            availableAmenities = amenities,
                            submitError = "Error de conexión al cargar"
                        )
                    }
                }
            }
        }
    }

    private fun matchAmenitiesFromNames(amenityNames: List<String>) {
        val state = _formState.value
        if (amenityNames.isEmpty()) return

        val matchedIds = state.availableAmenities
            .filter { it.name in amenityNames }
            .map { it.id }
            .toSet()

        if (matchedIds.isNotEmpty()) {
            _formState.update { it.copy(selectedAmenityIds = matchedIds) }
        }
    }

    fun onListingTitleChange(value: String) {
        _formState.update { it.copy(listingTitle = value) }
        markFieldTouched("listingTitle")
        validateField("listingTitle", value)
    }

    fun onDescriptionChange(value: String) {
        _formState.update { it.copy(description = value) }
    }

    fun onPriceChange(value: String) {
        _formState.update { it.copy(price = value) }
        markFieldTouched("price")
        validateField("price", value)
    }

    fun onPropertyTypeChange(value: String) {
        _formState.update { it.copy(propertyType = value) }
        markFieldTouched("propertyType")
        validateField("propertyType", value)
    }

    fun onStreetAddressChange(value: String) {
        _formState.update { it.copy(streetAddress = value) }
        markFieldTouched("streetAddress")
        validateField("streetAddress", value)
    }

    fun onNeighborhoodChange(value: String) {
        _formState.update { it.copy(neighborhood = value) }
    }

    fun onPostalCodeChange(value: String) {
        _formState.update { it.copy(postalCode = value) }
        markFieldTouched("postalCode")
        validateField("postalCode", value)
    }

    fun onCityChange(value: String) {
        _formState.update { it.copy(city = value) }
        markFieldTouched("city")
        validateField("city", value)
    }

    fun onStateProvinceChange(value: String) {
        _formState.update { it.copy(stateProvince = value) }
        markFieldTouched("stateProvince")
        validateField("stateProvince", value)
    }

    fun onCountryChange(value: String) {
        _formState.update { it.copy(country = value) }
    }

    fun onLocationSelected(latitude: Double, longitude: Double) {
        _formState.update { it.copy(latitude = latitude, longitude = longitude) }
    }

    fun onBedroomsChange(value: String) {
        _formState.update { it.copy(bedrooms = value) }
    }

    fun onFullBathroomsChange(value: String) {
        _formState.update { it.copy(fullBathrooms = value) }
    }

    fun onHalfBathroomsChange(value: String) {
        _formState.update { it.copy(halfBathrooms = value) }
    }

    fun onBuiltAreaSqmChange(value: String) {
        _formState.update { it.copy(builtAreaSqm = value) }
    }

    fun onLotSizeSqmChange(value: String) {
        _formState.update { it.copy(lotSizeSqm = value) }
    }

    fun onParkingSpacesChange(value: String) {
        _formState.update { it.copy(parkingSpaces = value) }
    }

    fun onStoriesFloorsChange(value: String) {
        _formState.update { it.copy(storiesFloors = value) }
    }

    fun onPropertyAgeYearsChange(value: String) {
        _formState.update { it.copy(propertyAgeYears = value) }
    }

    fun onMaintenanceFeeChange(value: String) {
        _formState.update { it.copy(maintenanceFee = value) }
    }

    fun toggleAmenity(amenityId: Int) {
        _formState.update { state ->
            val newSelectedIds = if (state.selectedAmenityIds.contains(amenityId)) {
                state.selectedAmenityIds - amenityId
            } else {
                state.selectedAmenityIds + amenityId
            }
            state.copy(selectedAmenityIds = newSelectedIds)
        }
    }

    fun addNewImages(uris: List<Uri>) {
        _formState.update { state ->
            val currentMaxOrder = (state.existingImages.maxOfOrNull { it.displayOrder } ?: -1)
                .coerceAtLeast(state.newImages.maxOfOrNull { it.displayOrder } ?: -1)
            val newImages = uris.mapIndexed { index, uri ->
                NewImage(uri, currentMaxOrder + index + 1)
            }
            state.copy(newImages = state.newImages + newImages)
        }
    }

    fun removeExistingImage(mediaId: Int) {
        _formState.update { state ->
            state.copy(
                removedImageIds = state.removedImageIds + mediaId,
                existingImages = state.existingImages.filter { it.mediaId != mediaId }
            )
        }
    }

    fun removeNewImage(uri: Uri) {
        _formState.update { state ->
            state.copy(newImages = state.newImages.filter { it.uri != uri })
        }
    }

    fun restoreExistingImage(image: ExistingImage) {
        _formState.update { state ->
            state.copy(
                removedImageIds = state.removedImageIds - image.mediaId,
                existingImages = (state.existingImages + image).sortedBy { it.displayOrder }
            )
        }
    }

    fun nextStep() {
        val state = _formState.value
        if (validateStep(state.currentStep)) {
            _formState.update { it.copy(currentStep = (it.currentStep + 1).coerceAtMost(3)) }
        }
    }

    fun previousStep() {
        _formState.update { it.copy(currentStep = (it.currentStep - 1).coerceAtLeast(0)) }
    }

    private fun markFieldTouched(field: String) {
        _formState.update { it.copy(touchedFields = it.touchedFields + field) }
    }

    private fun validateField(field: String, value: Any) {
        val error = when (field) {
            "listingTitle" -> validateListingTitle(value as String)
            "price" -> validatePrice(value as String)
            "propertyType" -> validatePropertyType(value as String)
            "streetAddress" -> validateStreetAddress(value as String)
            "postalCode" -> validatePostalCode(value as String)
            "city" -> validateCity(value as String)
            "stateProvince" -> validateStateProvince(value as String)
            else -> null
        }
        _formState.update { it.copy(errors = it.errors + (field to error)) }
    }

    private fun validateListingTitle(value: String): String? {
        if (value.isBlank()) return null
        if (value.length > 150) return "Máximo 150 caracteres"
        return null
    }

    private fun validatePrice(value: String): String? {
        if (value.isBlank()) return null
        val priceValue = value.toDoubleOrNull()
        if (priceValue == null) return "Precio inválido"
        if (priceValue <= 0) return "El precio debe ser mayor a 0"
        return null
    }

    private fun validatePropertyType(value: String): String? {
        if (value.isBlank()) return null
        return null
    }

    private fun validateStreetAddress(value: String): String? {
        return null
    }

    private fun validatePostalCode(value: String): String? {
        if (value.isBlank()) return null
        if (!value.matches(Regex("^\\d{5}$"))) return "Código postal inválido (5 dígitos)"
        return null
    }

    private fun validateCity(value: String): String? {
        return null
    }

    private fun validateStateProvince(value: String): String? {
        return null
    }

    private fun validateStep(step: Int): Boolean {
        val state = _formState.value
        val stepFields = when (step) {
            0 -> listOf("listingTitle", "price", "propertyType")
            1 -> listOf("streetAddress", "city", "stateProvince", "postalCode")
            2 -> emptyList<String>()
            3 -> emptyList<String>()
            else -> emptyList()
        }

        _formState.update { it.copy(touchedFields = it.touchedFields + stepFields) }

        val errors = mutableMapOf<String, String?>()
        when (step) {
            0 -> {
                errors["listingTitle"] = if (state.listingTitle.isBlank()) "El título es requerido" else validateListingTitle(state.listingTitle)
                errors["price"] = if (state.price.isBlank()) "El precio es requerido" else validatePrice(state.price)
                errors["propertyType"] = if (state.propertyType.isBlank()) "Selecciona un tipo" else null
            }
            1 -> {
                errors["streetAddress"] = if (state.streetAddress.isBlank()) "La dirección es requerida" else null
                errors["city"] = if (state.city.isBlank()) "La ciudad es requerida" else null
                errors["stateProvince"] = if (state.stateProvince.isBlank()) "El estado es requerido" else null
                errors["postalCode"] = validatePostalCode(state.postalCode)
            }
        }

        _formState.update { it.copy(errors = it.errors + errors) }

        val hasErrors = errors.values.any { it != null }
        return !hasErrors
    }

    fun submitForm(context: Context) {
        val state = _formState.value
        val propertyId = state.propertyId ?: return

        val step0Valid = validateStep(0)
        val step1Valid = validateStep(1)

        if (!step0Valid) {
            _formState.update { it.copy(currentStep = 0) }
            return
        }
        if (!step1Valid) {
            _formState.update { it.copy(currentStep = 1) }
            return
        }

        _formState.update { it.copy(isSubmitting = true, submitError = null) }

        viewModelScope.launch {
            val uploadedImageUrls = mutableListOf<String>()
            if (state.newImages.isNotEmpty()) {
                _formState.update { it.copy(isUploadingImages = true) }
                for (image in state.newImages) {
                    val file = createFileFromUri(context, image.uri) ?: continue
                    when (val result = repository.uploadImage(file)) {
                        is Result.Success -> uploadedImageUrls.add(result.data)
                        is Result.Error -> {
                            _formState.update {
                                it.copy(
                                    isSubmitting = false,
                                    isUploadingImages = false,
                                    submitError = "Error al subir imagen: ${result.message}"
                                )
                            }
                            return@launch
                        }
                        is Result.NetworkError -> {
                            _formState.update {
                                it.copy(
                                    isSubmitting = false,
                                    isUploadingImages = false,
                                    submitError = "Error de conexión al subir imagen"
                                )
                            }
                            return@launch
                        }
                    }
                    file.delete()
                }
                _formState.update { it.copy(isUploadingImages = false) }
            }

            val allImages = mutableListOf<PropertyImageRequest>()
            state.existingImages
                .filter { it.mediaId !in state.removedImageIds }
                .forEach { img ->
                    allImages.add(PropertyImageRequest(url = img.url, displayOrder = img.displayOrder))
                }
            uploadedImageUrls.forEachIndexed { index, url ->
                val maxOrder = state.existingImages.maxOfOrNull { it.displayOrder } ?: -1
                allImages.add(PropertyImageRequest(url = url, displayOrder = maxOrder + index + 1))
            }

            val request = PropertyUpdateRequest(
                listingTitle = state.listingTitle,
                description = state.description.ifBlank { null },
                price = state.price.toDoubleOrNull(),
                propertyType = state.propertyType,
                streetAddress = state.streetAddress.ifBlank { null },
                neighborhood = state.neighborhood.ifBlank { null },
                postalCode = state.postalCode.ifBlank { null },
                city = state.city,
                stateProvince = state.stateProvince,
                country = state.country,
                latitude = state.latitude?.let { "%.8f".format(it).toDouble() },
                longitude = state.longitude?.let { "%.8f".format(it).toDouble() },
                bedrooms = state.bedrooms.toIntOrNull(),
                fullBathrooms = state.fullBathrooms.toIntOrNull(),
                halfBathrooms = state.halfBathrooms.toIntOrNull(),
                builtAreaSqm = state.builtAreaSqm.toDoubleOrNull(),
                lotSizeSqm = state.lotSizeSqm.toDoubleOrNull(),
                parkingSpaces = state.parkingSpaces.toIntOrNull(),
                storiesFloors = state.storiesFloors.toIntOrNull(),
                propertyAgeYears = state.propertyAgeYears.toIntOrNull(),
                maintenanceFee = state.maintenanceFee.toDoubleOrNull(),
                amenityIds = state.selectedAmenityIds.toList(),
                images = allImages
            )

            when (val result = repository.updateProperty(propertyId, request)) {
                is Result.Success -> {
                    _formState.update {
                        it.copy(
                            isSubmitting = false,
                            submitSuccess = true
                        )
                    }
                }
                is Result.Error -> {
                    _formState.update {
                        it.copy(
                            isSubmitting = false,
                            submitError = "Error al guardar: ${result.message}"
                        )
                    }
                }
                is Result.NetworkError -> {
                    _formState.update {
                        it.copy(
                            isSubmitting = false,
                            submitError = "Error de conexión"
                        )
                    }
                }
            }
        }
    }

    private fun createFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            inputStream.close()
            tempFile
        } catch (e: Exception) {
            null
        }
    }

    fun clearSubmitState() {
        _formState.update { it.copy(submitSuccess = false, submitError = null) }
    }
}
