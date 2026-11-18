package com.example.inmobi.features.properties.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PropertyCreateRequest(
    @Json(name = "operation_type") val operationType: String,
    @Json(name = "listing_title") val listingTitle: String,
    @Json(name = "description") val description: String? = null,
    @Json(name = "price") val price: Double,
    @Json(name = "currency") val currency: String = "MXN",
    @Json(name = "maintenance_fee") val maintenanceFee: Double? = null,
    @Json(name = "listing_status") val listingStatus: String = "active",
    @Json(name = "property_type") val propertyType: String,
    @Json(name = "street_address") val streetAddress: String? = null,
    @Json(name = "neighborhood") val neighborhood: String? = null,
    @Json(name = "postal_code") val postalCode: String? = null,
    @Json(name = "city") val city: String,
    @Json(name = "state_province") val stateProvince: String,
    @Json(name = "country") val country: String = "Mexico",
    @Json(name = "lot_size_sqm") val lotSizeSqm: Double? = null,
    @Json(name = "built_area_sqm") val builtAreaSqm: Double? = null,
    @Json(name = "bedrooms") val bedrooms: Int? = null,
    @Json(name = "full_bathrooms") val fullBathrooms: Int? = null,
    @Json(name = "half_bathrooms") val halfBathrooms: Int? = null,
    @Json(name = "parking_spaces") val parkingSpaces: Int? = null,
    @Json(name = "stories_floors") val storiesFloors: Int? = null,
    @Json(name = "property_age_years") val propertyAgeYears: Int? = null
)
