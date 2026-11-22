package com.example.inmobi.features.main.data

import com.example.inmobi.features.main.data.dto.DashboardDto
import retrofit2.Response
import retrofit2.http.GET

interface DashboardApi {
    @GET("dashboard/")
    suspend fun getDashboard(): Response<DashboardDto>
}
