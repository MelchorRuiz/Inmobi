package com.example.inmobi.features.properties.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PropertyUpdateRequest(
    @Json(name = "listing_title") val listingTitle: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "price") val price: Double? = null,
    @Json(name = "currency") val currency: String? = null,
    @Json(name = "maintenance_fee") val maintenanceFee: Double? = null,
    @Json(name = "listing_status") val listingStatus: String? = null,
    @Json(name = "property_type") val propertyType: String? = null,
    @Json(name = "street_address") val streetAddress: String? = null,
    @Json(name = "neighborhood") val neighborhood: String? = null,
    @Json(name = "postal_code") val postalCode: String? = null,
    @Json(name = "city") val city: String? = null,
    @Json(name = "state_province") val stateProvince: String? = null,
    @Json(name = "country") val country: String? = null,
    @Json(name = "latitude") val latitude: Double? = null,
    @Json(name = "longitude") val longitude: Double? = null,
    @Json(name = "lot_size_sqm") val lotSizeSqm: Double? = null,
    @Json(name = "built_area_sqm") val builtAreaSqm: Double? = null,
    @Json(name = "bedrooms") val bedrooms: Int? = null,
    @Json(name = "full_bathrooms") val fullBathrooms: Int? = null,
    @Json(name = "half_bathrooms") val halfBathrooms: Int? = null,
    @Json(name = "parking_spaces") val parkingSpaces: Int? = null,
    @Json(name = "stories_floors") val storiesFloors: Int? = null,
    @Json(name = "property_age_years") val propertyAgeYears: Int? = null,
    @Json(name = "amenity_ids") val amenityIds: List<Int>? = null,
    @Json(name = "images") val images: List<PropertyImageRequest>? = null
)
