package com.example.inmobi.features.properties.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AmenityDto(
    @Json(name = "amenity_id") val amenityId: Int,
    @Json(name = "name") val name: String
)
