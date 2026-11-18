package com.example.inmobi.features.auth.data.dto

import com.squareup.moshi.Json

data class UserResponse(
    @Json(name = "id") val id: Long,
    @Json(name = "full_name") val fullName: String? = null,
    @Json(name = "email") val email: String,
    @Json(name = "birth_date") val birthDate: String? = null,
    @Json(name = "email_verified") val emailVerified: Boolean,
    @Json(name = "role") val role: String,
    @Json(name = "is_active") val isActive: Boolean,
    @Json(name = "created_at") val createdAt: String
)
