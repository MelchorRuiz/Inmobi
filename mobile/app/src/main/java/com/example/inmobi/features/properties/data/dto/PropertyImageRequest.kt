package com.example.inmobi.features.properties.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PropertyImageRequest(
    @Json(name = "url") val url: String,
    @Json(name = "media_type") val mediaType: String = "image",
    @Json(name = "display_order") val displayOrder: Int = 0
)
