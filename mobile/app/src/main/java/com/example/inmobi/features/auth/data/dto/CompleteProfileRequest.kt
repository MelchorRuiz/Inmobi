package com.example.inmobi.features.auth.data.dto

import com.squareup.moshi.Json

data class CompleteProfileRequest(
    @Json(name = "phone") val phone: String,
    @Json(name = "birth_date") val birthDate: String,
    @Json(name = "privacy_accepted") val privacyAccepted: Boolean,
    @Json(name = "preference") val preference: String
)
