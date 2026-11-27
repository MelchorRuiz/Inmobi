package com.example.inmobi.features.settings.data.dto

import com.squareup.moshi.Json

data class AvatarResponse(
    @Json(name = "url") val url: String
)
