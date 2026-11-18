package com.example.inmobi.features.auth.data.dto

import com.squareup.moshi.Json

data class RegisterRequest(
    @Json(name = "full_name") val fullName: String,
    @Json(name = "email") val email: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "password") val password: String,
    @Json(name = "birth_date") val birthDate: String,
    @Json(name = "privacy_accepted") val privacyAccepted: Boolean,
    @Json(name = "preference") val preference: String
)
