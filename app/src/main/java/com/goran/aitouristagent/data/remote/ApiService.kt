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

    @GET("api/v1/trips/{id}/chat")
    suspend fun getChatHistory(@Path("id") tripId: String): List<ChatMessageDto>

    @POST("api/v1/trips/{id}/chat")
    suspend fun postChatMessage(@Path("id") tripId: String, @Body message: ChatMessagePostDto): ChatMessageDto

    @POST("api/v1/chat")
    suspend fun chat(@Body request: ChatRequest): ChatResponse
}
