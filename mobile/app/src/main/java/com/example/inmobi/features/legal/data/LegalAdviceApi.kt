package com.example.inmobi.features.legal.data

import com.example.inmobi.features.legal.data.dto.AcceptLegalAdviceDto
import com.example.inmobi.features.legal.data.dto.InterestedPropertyDto
import com.example.inmobi.features.legal.data.dto.LegalAdviceRequestCreateDto
import com.example.inmobi.features.legal.data.dto.LegalAdviceRequestDto
import com.example.inmobi.features.legal.data.dto.LegalAdviceRequestListDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface LegalAdviceApi {

    @POST("legal-advice/requests/")
    suspend fun createLegalAdviceRequest(
        @Body request: LegalAdviceRequestCreateDto
    ): Response<LegalAdviceRequestDto>

    @GET("legal-advice/interested-properties/")
    suspend fun getInterestedProperties(): Response<List<InterestedPropertyDto>>

    @GET("legal-advice/requests/")
    suspend fun getRequests(): Response<List<LegalAdviceRequestListDto>>

    @POST("legal-advice/requests/{requestId}/accept/")
    suspend fun acceptRequest(
        @Path("requestId") requestId: Int
    ): Response<AcceptLegalAdviceDto>
}
