package com.example.inmobi.features.journey.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PropertyStepDto(
    @Json(name = "id") val id: Int,
    @Json(name = "user_id") val userId: Int,
    @Json(name = "property_id") val propertyId: Int,
    @Json(name = "step_id") val stepId: Int,
    @Json(name = "completed") val completed: Boolean,
    @Json(name = "comments") val comments: String?,
    @Json(name = "completed_at") val completedAt: String?,
    @Json(name = "created_at") val createdAt: String?,
    @Json(name = "updated_at") val updatedAt: String?,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "step_order") val stepOrder: Int
)

@JsonClass(generateAdapter = true)
data class PropertyProgressDto(
    @Json(name = "property_id") val propertyId: Int,
    @Json(name = "total_steps") val totalSteps: Int,
    @Json(name = "completed_steps") val completedSteps: Int,
    @Json(name = "progress_percentage") val progressPercentage: Float,
    @Json(name = "steps") val steps: List<PropertyStepDto>
)

@JsonClass(generateAdapter = true)
data class StepUpdateRequest(
    @Json(name = "completed") val completed: Boolean? = null,
    @Json(name = "comments") val comments: String? = null
)

@JsonClass(generateAdapter = true)
data class InterestResponse(
    @Json(name = "message") val message: String,
    @Json(name = "steps_count") val stepsCount: Int
)
