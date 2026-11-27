package com.example.inmobi.features.settings.data.dto

import com.squareup.moshi.Json

data class PasswordSetRequest(
    @Json(name = "new_password") val newPassword: String
)
