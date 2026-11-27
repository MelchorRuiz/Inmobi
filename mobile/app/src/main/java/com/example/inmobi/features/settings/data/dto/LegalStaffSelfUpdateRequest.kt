package com.example.inmobi.features.settings.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LegalStaffSelfUpdateRequest(
    @Json(name = "full_name") val fullName: String? = null,
    @Json(name = "id_number") val idNumber: String? = null,
    @Json(name = "specialty") val specialty: String? = null,
    @Json(name = "firm_name") val firmName: String? = null,
    @Json(name = "phone") val phone: String? = null
)
