package com.example.inmobi.features.main.data.dto

import com.example.inmobi.features.properties.data.dto.PropertyDto
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DashboardDto(
    @Json(name = "interested_properties") val interestedProperties: List<PropertyDto>,
    @Json(name = "my_published_properties") val myPublishedProperties: List<PropertyDto>
)
