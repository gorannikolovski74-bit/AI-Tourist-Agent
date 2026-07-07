package com.goran.aitouristagent.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.POST

interface ApiService {
    @GET("api/v1/trips")
    suspend fun getTrips(): List<TripDto>

    @POST("api/v1/trips")
    suspend fun createTrip(@Body trip: TripDto): TripDto

    @PUT("api/v1/trips/{id}")
    suspend fun updateTrip(@Path("id") id: String, @Body trip: TripDto): TripDto

    @DELETE("api/v1/trips/{id}")
    suspend fun deleteTrip(@Path("id") id: String)
}
