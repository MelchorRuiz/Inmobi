package com.example.inmobi.features.admin.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LegalStaffResponse(
    @Json(name = "id") val id: Long,
    @Json(name = "email") val email: String,
    @Json(name = "full_name") val fullName: String?,
    @Json(name = "is_active") val isActive: Boolean,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "legal_staff") val legalStaff: LegalStaffDetail
)

@JsonClass(generateAdapter = true)
data class LegalStaffDetail(
    @Json(name = "id_number") val idNumber: String,
    @Json(name = "specialty") val specialty: String,
    @Json(name = "firm_name") val firmName: String?,
    @Json(name = "phone") val phone: String?
)

@JsonClass(generateAdapter = true)
data class LegalStaffCreateRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "full_name") val fullName: String,
    @Json(name = "id_number") val idNumber: String,
    @Json(name = "specialty") val specialty: String,
    @Json(name = "firm_name") val firmName: String? = null,
    @Json(name = "phone") val phone: String? = null
)

@JsonClass(generateAdapter = true)
data class LegalStaffUpdateRequest(
    @Json(name = "password") val password: String? = null,
    @Json(name = "full_name") val fullName: String? = null,
    @Json(name = "id_number") val idNumber: String? = null,
    @Json(name = "specialty") val specialty: String? = null,
    @Json(name = "firm_name") val firmName: String? = null,
    @Json(name = "phone") val phone: String? = null
)
