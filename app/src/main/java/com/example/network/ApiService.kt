package com.example.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// ============================================================================
// MY API
// Place your real Retrofit API interface definitions here.
// When you are ready to switch from FakeApiRepository to real network requests,
// configure Retrofit to create an instance of this interface and call it
// from your RealApiRepository implementation.
// ============================================================================

interface ApiService {

    // TODO: Replace with your real API endpoints
    @GET("requests")
    suspend fun getRequests(
        @Query("limit") limit: Int = 50
    ): List<ApiRequestDto>

    @GET("requests/{id}")
    suspend fun getRequestDetail(
        @Path("id") requestId: String
    ): ApiRequestDto

    // TODO: Add your other custom API endpoints here:
    // @POST("login")
    // suspend fun login(@Body body: LoginRequestDto): LoginResponseDto
}
