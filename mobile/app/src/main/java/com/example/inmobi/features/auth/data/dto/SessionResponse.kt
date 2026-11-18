package com.example.inmobi.features.auth.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SessionResponse(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "refresh_token") val refreshToken: String? = null,
    @Json(name = "role") val role: String,
    @Json(name = "is_profile_complete") val isProfileComplete: Boolean,
    @Json(name = "has_profile_picture") val hasProfilePicture: Boolean
)
