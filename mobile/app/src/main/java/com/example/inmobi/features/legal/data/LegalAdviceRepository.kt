package com.example.inmobi.features.legal.data

import com.example.inmobi.core.network.RetrofitClient
import com.example.inmobi.features.legal.data.dto.AcceptLegalAdviceDto
import com.example.inmobi.features.legal.data.dto.InterestedPropertyDto
import com.example.inmobi.features.legal.data.dto.LegalAdviceRequestCreateDto
import com.example.inmobi.features.legal.data.dto.LegalAdviceRequestDto
import com.example.inmobi.features.legal.data.dto.LegalAdviceRequestListDto
import com.example.inmobi.features.properties.data.Result

class LegalAdviceRepository {

    private val api: LegalAdviceApi = RetrofitClient.create()

    suspend fun getInterestedProperties(): Result<List<InterestedPropertyDto>> {
        return safeApiCall { api.getInterestedProperties() }
    }

    suspend fun createLegalAdviceRequest(
        propertyId: Int,
        description: String
    ): Result<LegalAdviceRequestDto> {
        return safeApiCall {
            api.createLegalAdviceRequest(
                LegalAdviceRequestCreateDto(propertyId, description)
            )
        }
    }

    suspend fun getRequests(): Result<List<LegalAdviceRequestListDto>> {
        return safeApiCall { api.getRequests() }
    }

    suspend fun acceptRequest(requestId: Int): Result<AcceptLegalAdviceDto> {
        return safeApiCall { api.acceptRequest(requestId) }
    }

    private suspend fun <T> safeApiCall(call: suspend () -> retrofit2.Response<T>): Result<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(body)
                } else {
                    Result.Error(response.code(), "Empty response")
                }
            } else {
                Result.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            Result.NetworkError
        }
    }
}
