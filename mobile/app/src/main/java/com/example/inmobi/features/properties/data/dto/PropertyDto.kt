package com.example.inmobi.features.properties.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PriceEstimationDto(
    @Json(name = "estimated_price") val estimatedPrice: String,
    @Json(name = "min_price") val minPrice: String,
    @Json(name = "max_price") val maxPrice: String,
    @Json(name = "confidence_score") val confidenceScore: Double,
    @Json(name = "model_version") val modelVersion: String
)

@JsonClass(generateAdapter = true)
data class PropertyDto(
    @Json(name = "property_id") val propertyId: Int,
    @Json(name = "owner_user_id") val ownerUserId: Int,
    @Json(name = "operation_type_id") val operationTypeId: Int,
    @Json(name = "operation_name") val operationName: String,
    @Json(name = "listing_title") val listingTitle: String,
    @Json(name = "description") val description: String?,
    @Json(name = "price") val price: Double,
    @Json(name = "currency") val currency: String,
    @Json(name = "maintenance_fee") val maintenanceFee: Double?,
    @Json(name = "listing_status") val listingStatus: String,
    @Json(name = "property_type") val propertyType: String,
    @Json(name = "street_address") val streetAddress: String?,
    @Json(name = "neighborhood") val neighborhood: String?,
    @Json(name = "postal_code") val postalCode: String?,
    @Json(name = "city") val city: String,
    @Json(name = "state_province") val stateProvince: String,
    @Json(name = "country") val country: String,
    @Json(name = "latitude") val latitude: Double?,
    @Json(name = "longitude") val longitude: Double?,
    @Json(name = "lot_size_sqm") val lotSizeSqm: Double?,
    @Json(name = "built_area_sqm") val builtAreaSqm: Double?,
    @Json(name = "bedrooms") val bedrooms: Int?,
    @Json(name = "full_bathrooms") val fullBathrooms: Int?,
    @Json(name = "half_bathrooms") val halfBathrooms: Int?,
    @Json(name = "parking_spaces") val parkingSpaces: Int?,
    @Json(name = "stories_floors") val storiesFloors: Int?,
    @Json(name = "property_age_years") val propertyAgeYears: Int?,
    @Json(name = "published_at") val publishedAt: String?,
    @Json(name = "updated_at") val updatedAt: String?,
    @Json(name = "amenities") val amenities: List<String> = emptyList(),
    @Json(name = "images") val images: List<PropertyImageResponse> = emptyList(),
    @Json(name = "price_estimation") val priceEstimation: PriceEstimationDto? = null
)

@JsonClass(generateAdapter = true)
data class PropertyImageResponse(
    @Json(name = "media_id") val mediaId: Int,
    @Json(name = "media_type") val mediaType: String,
    @Json(name = "file_url") val fileUrl: String,
    @Json(name = "display_order") val displayOrder: Int
)
