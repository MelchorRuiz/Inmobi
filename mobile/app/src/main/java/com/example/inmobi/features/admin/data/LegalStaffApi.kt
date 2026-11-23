package com.example.inmobi.features.admin.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface LegalStaffApi {

    @GET("legal-staff/")
    suspend fun list(): Response<List<LegalStaffResponse>>

    @GET("legal-staff/{user_id}/")
    suspend fun get(@Path("user_id") userId: Long): Response<LegalStaffResponse>

    @POST("legal-staff/")
    suspend fun create(@Body request: LegalStaffCreateRequest): Response<LegalStaffResponse>

    @PATCH("legal-staff/{user_id}/")
    suspend fun update(
        @Path("user_id") userId: Long,
        @Body request: LegalStaffUpdateRequest
    ): Response<LegalStaffResponse>

    @DELETE("legal-staff/{user_id}/")
    suspend fun delete(@Path("user_id") userId: Long): Response<Unit>
}
