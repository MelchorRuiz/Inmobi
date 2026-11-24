package com.example.inmobi.features.legal.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LegalAdviceRequestCreateDto(
    @Json(name = "property_id") val propertyId: Int,
    @Json(name = "description") val description: String
)

@JsonClass(generateAdapter = true)
data class LegalAdviceRequestDto(
    @Json(name = "id") val id: Int,
    @Json(name = "user_id") val userId: Int,
    @Json(name = "property_id") val propertyId: Int,
    @Json(name = "description") val description: String,
    @Json(name = "is_attended") val isAttended: Boolean,
    @Json(name = "created_at") val createdAt: String
)

@JsonClass(generateAdapter = true)
data class LegalAdviceRequestListDto(
    @Json(name = "id") val id: Int,
    @Json(name = "user_id") val userId: Int,
    @Json(name = "user_full_name") val userFullName: String?,
    @Json(name = "user_email") val userEmail: String,
    @Json(name = "property_id") val propertyId: Int,
    @Json(name = "property_title") val propertyTitle: String,
    @Json(name = "description") val description: String,
    @Json(name = "is_attended") val isAttended: Boolean,
    @Json(name = "created_at") val createdAt: String
)

@JsonClass(generateAdapter = true)
data class AcceptLegalAdviceDto(
    @Json(name = "id") val id: Int,
    @Json(name = "user_id") val userId: Int,
    @Json(name = "property_id") val propertyId: Int,
    @Json(name = "description") val description: String,
    @Json(name = "is_attended") val isAttended: Boolean,
    @Json(name = "created_at") val createdAt: String,
    @Json(name = "chat_id") val chatId: Int
)

@JsonClass(generateAdapter = true)
data class InterestedPropertyDto(
    @Json(name = "property_id") val propertyId: Int,
    @Json(name = "listing_title") val listingTitle: String,
    @Json(name = "images") val images: List<String>,
    @Json(name = "has_legal_advice_request") val hasLegalAdviceRequest: Boolean
)
