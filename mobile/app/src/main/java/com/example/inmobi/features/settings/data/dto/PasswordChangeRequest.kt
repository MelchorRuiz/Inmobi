package com.example.inmobi.features.settings.data.dto

import com.squareup.moshi.Json

data class PasswordChangeRequest(
    @Json(name = "current_password") val currentPassword: String,
    @Json(name = "new_password") val newPassword: String
)
