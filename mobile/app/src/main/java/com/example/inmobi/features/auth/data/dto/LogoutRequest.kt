package com.example.inmobi.features.auth.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LogoutRequest(
    @Json(name = "client_type") val clientType: String = "mobile",
    @Json(name = "refresh_token") val refreshToken: String? = null
)
