package com.example.inmobi.features.journey.data

import com.example.inmobi.features.journey.data.dto.InterestResponse
import com.example.inmobi.features.journey.data.dto.PropertyProgressDto
import com.example.inmobi.features.journey.data.dto.StepUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface JourneyApi {

    @POST("journey/properties/{propertyId}/interest")
    suspend fun registerInterest(
        @Path("propertyId") propertyId: Int
    ): Response<InterestResponse>

    @GET("journey/properties/{propertyId}/progress")
    suspend fun getProgress(
        @Path("propertyId") propertyId: Int
    ): Response<PropertyProgressDto>

    @PUT("journey/properties/{propertyId}/steps/{stepId}")
    suspend fun updateStep(
        @Path("propertyId") propertyId: Int,
        @Path("stepId") stepId: Int,
        @Body request: StepUpdateRequest
    ): Response<Unit>

    @DELETE("journey/properties/{propertyId}/interest")
    suspend fun removeInterest(
        @Path("propertyId") propertyId: Int
    ): Response<Unit>
}
