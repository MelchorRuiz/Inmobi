package com.example.inmobi.features.settings.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserProfileUpdateRequest(
    @Json(name = "full_name") val fullName: String? = null,
    @Json(name = "birth_date") val birthDate: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "preference") val preference: String? = null,
    @Json(name = "privacy_accepted") val privacyAccepted: Boolean? = null
)
