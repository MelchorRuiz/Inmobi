package com.example.inmobi.features.journey.data

import com.example.inmobi.core.network.RetrofitClient
import com.example.inmobi.features.journey.data.dto.InterestResponse
import com.example.inmobi.features.journey.data.dto.PropertyProgressDto
import com.example.inmobi.features.journey.data.dto.StepUpdateRequest
import com.example.inmobi.features.properties.data.Result

class JourneyRepository {

    private val api: JourneyApi = RetrofitClient.create()

    suspend fun registerInterest(propertyId: Int): Result<InterestResponse> {
        return safeApiCall { api.registerInterest(propertyId) }
    }

    suspend fun getProgress(propertyId: Int): Result<PropertyProgressDto> {
        return safeApiCall { api.getProgress(propertyId) }
    }

    suspend fun updateStep(
        propertyId: Int,
        stepId: Int,
        completed: Boolean?,
        comments: String?
    ): Result<Unit> {
        return safeApiCall {
            api.updateStep(propertyId, stepId, StepUpdateRequest(completed, comments))
        }
    }

    suspend fun removeInterest(propertyId: Int): Result<Unit> {
        return safeApiCall { api.removeInterest(propertyId) }
    }

    private suspend fun <T> safeApiCall(call: suspend () -> retrofit2.Response<T>): Result<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(body)
                } else {
                    Result.Success(Unit as T)
                }
            } else {
                Result.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            Result.NetworkError
        }
    }
}
