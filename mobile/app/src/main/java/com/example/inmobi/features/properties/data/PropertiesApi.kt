package com.example.inmobi.features.properties.data

import com.example.inmobi.features.properties.data.dto.PropertyCreateRequest
import com.example.inmobi.features.properties.data.dto.PropertyDto
import com.example.inmobi.features.properties.data.dto.PropertyUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PropertiesApi {

    @GET("properties/")
    suspend fun listProperties(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 20,
        @Query("operation_type") operationType: String? = null,
        @Query("property_type") propertyType: String? = null,
        @Query("listing_status") listingStatus: String? = null,
        @Query("city") city: String? = null,
        @Query("min_price") minPrice: Double? = null,
        @Query("max_price") maxPrice: Double? = null
    ): Response<List<PropertyDto>>

    @GET("properties/my-properties")
    suspend fun myProperties(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 20,
        @Query("operation_type") operationType: String? = null,
        @Query("property_type") propertyType: String? = null,
        @Query("listing_status") listingStatus: String? = null,
        @Query("city") city: String? = null,
        @Query("min_price") minPrice: Double? = null,
        @Query("max_price") maxPrice: Double? = null
    ): Response<List<PropertyDto>>

    @GET("properties/{propertyId}")
    suspend fun getProperty(
        @Path("propertyId") propertyId: Int
    ): Response<PropertyDto>

    @POST("properties/")
    suspend fun createProperty(
        @Body request: PropertyCreateRequest
    ): Response<PropertyDto>

    @PUT("properties/{propertyId}")
    suspend fun updateProperty(
        @Path("propertyId") propertyId: Int,
        @Body request: PropertyUpdateRequest
    ): Response<PropertyDto>

    @POST("properties/{propertyId}/reactivate")
    suspend fun reactivateProperty(
        @Path("propertyId") propertyId: Int
    ): Response<PropertyDto>

    @DELETE("properties/{propertyId}")
    suspend fun deleteProperty(
        @Path("propertyId") propertyId: Int,
        @Query("listing_status") listingStatus: String
    ): Response<Unit>
}
