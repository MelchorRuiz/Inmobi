package com.example.inmobi.features.settings.data.dto

import com.squareup.moshi.Json

data class LegalStaffData(
    @Json(name = "id_number") val idNumber: String,
    @Json(name = "specialty") val specialty: String,
    @Json(name = "firm_name") val firmName: String? = null,
    @Json(name = "phone") val phone: String? = null
)
