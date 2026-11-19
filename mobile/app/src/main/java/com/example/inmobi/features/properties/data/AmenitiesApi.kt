package com.example.inmobi.features.properties.data

import retrofit2.Response
import retrofit2.http.GET
import com.example.inmobi.features.properties.data.dto.AmenityDto

interface AmenitiesApi {

    @GET("amenities/")
    suspend fun listAmenities(): Response<List<AmenityDto>>
}
