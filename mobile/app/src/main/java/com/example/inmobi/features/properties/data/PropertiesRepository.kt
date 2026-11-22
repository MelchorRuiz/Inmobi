package com.example.inmobi.features.properties.data

import com.example.inmobi.core.network.RetrofitClient
import com.example.inmobi.features.properties.data.dto.AmenityDto
import com.example.inmobi.features.properties.data.dto.PropertyCreateRequest
import com.example.inmobi.features.properties.data.dto.PropertyDto
import com.example.inmobi.features.properties.data.dto.PropertyUpdateRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val code: Int, val message: String) : Result<Nothing>()
    data object NetworkError : Result<Nothing>()
}

class PropertiesRepository {

    private val api: PropertiesApi = RetrofitClient.create()
    private val filesApi: FilesApi = RetrofitClient.create()
    private val amenitiesApi: AmenitiesApi = RetrofitClient.create()

    suspend fun listProperties(
        operationType: String? = null,
        propertyType: String? = null,
        listingStatus: String? = null,
        city: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null
    ): Result<List<PropertyDto>> {
        return safeApiCall {
            api.listProperties(
                operationType = operationType,
                propertyType = propertyType,
                listingStatus = listingStatus,
                city = city,
                minPrice = minPrice,
                maxPrice = maxPrice
            )
        }
    }

    suspend fun myProperties(
        operationType: String? = null,
        propertyType: String? = null,
        listingStatus: String? = null,
        city: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null
    ): Result<List<PropertyDto>> {
        return safeApiCall {
            api.myProperties(
                operationType = operationType,
                propertyType = propertyType,
                listingStatus = listingStatus,
                city = city,
                minPrice = minPrice,
                maxPrice = maxPrice
            )
        }
    }

    suspend fun getProperty(propertyId: Int): Result<PropertyDto> {
        return safeApiCall { api.getProperty(propertyId) }
    }

    suspend fun createProperty(request: PropertyCreateRequest): Result<PropertyDto> {
        return safeApiCall { api.createProperty(request) }
    }

    suspend fun updateProperty(propertyId: Int, request: PropertyUpdateRequest): Result<PropertyDto> {
        return safeApiCall { api.updateProperty(propertyId, request) }
    }

    suspend fun reactivateProperty(propertyId: Int): Result<PropertyDto> {
        return safeApiCall { api.reactivateProperty(propertyId) }
    }

    suspend fun deleteProperty(propertyId: Int, listingStatus: String): Result<Unit> {
        return safeApiCall { api.deleteProperty(propertyId, listingStatus) }
    }

    suspend fun uploadImage(file: File): Result<String> {
        return try {
            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
            val response = filesApi.uploadFile(part)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(body.url)
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

    suspend fun getAmenities(): Result<List<AmenityDto>> {
        return safeApiCall { amenitiesApi.listAmenities() }
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
