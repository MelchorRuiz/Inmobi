package com.example.inmobi.features.properties.domain

import com.example.inmobi.BuildConfig
import com.example.inmobi.features.properties.data.dto.PropertyDto

enum class OperationType(val apiName: String) {
    SALE("sale"),
    RENT("rent");

    companion object {
        fun fromApiName(name: String): OperationType? = entries.find { it.apiName == name }
    }
}

enum class PropertyType(val apiName: String) {
    HOUSE("house"),
    APARTMENT("apartment"),
    LAND("land"),
    OFFICE("office"),
    COMMERCIAL_PREMISE("commercial_premise"),
    WAREHOUSE("warehouse"),
    INDUSTRIAL("industrial"),
    PARKING("parking");

    companion object {
        fun fromApiName(name: String): PropertyType? = entries.find { it.apiName == name }
    }
}

enum class ListingStatus(val apiName: String) {
    ACTIVE("active"),
    CANCELLED("cancelled"),
    CLOSED("closed");

    companion object {
        fun fromApiName(name: String): ListingStatus? = entries.find { it.apiName == name }
    }
}

data class PropertyImage(
    val mediaId: Int,
    val url: String,
    val displayOrder: Int
)

data class PriceEstimation(
    val estimatedPrice: Double,
    val minPrice: Double,
    val maxPrice: Double,
    val confidenceScore: Double,
    val modelVersion: String
)

data class Property(
    val id: Int,
    val ownerUserId: Int,
    val operationName: String,
    val listingTitle: String,
    val description: String?,
    val price: Double,
    val currency: String,
    val listingStatus: String,
    val propertyType: String,
    val streetAddress: String?,
    val neighborhood: String?,
    val postalCode: String?,
    val city: String,
    val stateProvince: String,
    val country: String,
    val bedrooms: Int?,
    val fullBathrooms: Int?,
    val halfBathrooms: Int?,
    val builtAreaSqm: Double?,
    val lotSizeSqm: Double?,
    val parkingSpaces: Int?,
    val storiesFloors: Int?,
    val propertyAgeYears: Int?,
    val maintenanceFee: Double?,
    val amenities: List<String> = emptyList(),
    val publishedAt: String?,
    val images: List<PropertyImage> = emptyList(),
    val priceEstimation: PriceEstimation? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)

fun PropertyDto.toDomain(): Property = Property(
    id = propertyId,
    ownerUserId = ownerUserId,
    operationName = operationName,
    listingTitle = listingTitle,
    description = description,
    price = price,
    currency = currency,
    listingStatus = listingStatus,
    propertyType = propertyType,
    streetAddress = streetAddress,
    neighborhood = neighborhood,
    postalCode = postalCode,
    city = city,
    stateProvince = stateProvince,
    country = country,
    bedrooms = bedrooms,
    fullBathrooms = fullBathrooms,
    halfBathrooms = halfBathrooms,
    builtAreaSqm = builtAreaSqm,
    lotSizeSqm = lotSizeSqm,
    parkingSpaces = parkingSpaces,
    storiesFloors = storiesFloors,
    propertyAgeYears = propertyAgeYears,
    maintenanceFee = maintenanceFee,
    amenities = amenities,
    publishedAt = publishedAt,
    latitude = latitude,
    longitude = longitude,
    images = images.map { img ->
        val fullUrl = if (img.fileUrl.startsWith("http")) {
            img.fileUrl
        } else {
            "${BuildConfig.BASE_URL}${img.fileUrl}"
        }
        PropertyImage(img.mediaId, fullUrl, img.displayOrder)
    }.sortedBy { it.displayOrder },
    priceEstimation = priceEstimation?.let {
        PriceEstimation(
            estimatedPrice = it.estimatedPrice.toDoubleOrNull() ?: 0.0,
            minPrice = it.minPrice.toDoubleOrNull() ?: 0.0,
            maxPrice = it.maxPrice.toDoubleOrNull() ?: 0.0,
            confidenceScore = it.confidenceScore,
            modelVersion = it.modelVersion
        )
    }
)

fun List<PropertyDto>.toDomain(): List<Property> = map { it.toDomain() }
