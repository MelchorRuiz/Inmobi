package com.example.inmobi.features.main.data

import com.example.inmobi.core.network.RetrofitClient
import com.example.inmobi.features.main.data.dto.DashboardDto
import com.example.inmobi.features.properties.data.Result
import com.example.inmobi.features.properties.data.dto.PropertyDto

class DashboardRepository {
    private val api: DashboardApi = RetrofitClient.create()

    suspend fun getDashboard(): Result<DashboardDto> {
        return try {
            val response = api.getDashboard()
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
