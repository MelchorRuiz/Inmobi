package com.example.inmobi.features.auth.data.dto

import com.squareup.moshi.Json

data class LoginRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "client_type") val clientType: String = "mobile"
)
