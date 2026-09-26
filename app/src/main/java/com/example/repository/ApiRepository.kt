package com.example.repository

import com.example.model.ApiRequest

interface ApiRepository {
    suspend fun getRequests(): List<ApiRequest>
    suspend fun clearHistory()
}
