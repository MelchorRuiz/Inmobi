package com.example.inmobi.features.settings.data.dto

import com.squareup.moshi.Json

data class EndUserData(
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "preference") val preference: String? = null,
    @Json(name = "privacy_accepted") val privacyAccepted: Boolean? = null
)
