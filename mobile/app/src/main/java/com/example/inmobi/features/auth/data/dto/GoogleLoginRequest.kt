package com.example.inmobi.features.auth.data.dto

import com.squareup.moshi.Json

data class GoogleLoginRequest(
    @Json(name = "id_token") val idToken: String,
    @Json(name = "client_type") val clientType: String = "mobile"
)
