package com.example.inmobi.features.journey.domain

import com.example.inmobi.features.journey.data.dto.PropertyProgressDto
import com.example.inmobi.features.journey.data.dto.PropertyStepDto

data class PropertyStep(
    val id: Int,
    val userId: Int,
    val propertyId: Int,
    val stepId: Int,
    val completed: Boolean,
    val comments: String?,
    val completedAt: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val title: String,
    val description: String,
    val stepOrder: Int
)

data class PropertyProgress(
    val propertyId: Int,
    val totalSteps: Int,
    val completedSteps: Int,
    val progressPercentage: Float,
    val steps: List<PropertyStep>
)

fun PropertyStepDto.toDomain(): PropertyStep = PropertyStep(
    id = id,
    userId = userId,
    propertyId = propertyId,
    stepId = stepId,
    completed = completed,
    comments = comments,
    completedAt = completedAt,
    createdAt = createdAt,
    updatedAt = updatedAt,
    title = title,
    description = description,
    stepOrder = stepOrder
)

fun PropertyProgressDto.toDomain(): PropertyProgress = PropertyProgress(
    propertyId = propertyId,
    totalSteps = totalSteps,
    completedSteps = completedSteps,
    progressPercentage = progressPercentage,
    steps = steps.map { it.toDomain() }
)
