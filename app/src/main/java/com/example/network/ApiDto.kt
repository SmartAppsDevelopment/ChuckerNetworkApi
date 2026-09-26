package com.example.network

import com.example.model.ApiRequest
import com.example.model.HttpMethod

// ============================================================================
// MY API - DATA TRANSFER OBJECTS (DTO)
// These data classes match the exact JSON structure returned by your backend API.
// ============================================================================

data class ApiRequestDto(
    val id: String? = null,
    val method: String? = null,
    val url: String? = null,
    val host: String? = null,
    val endpoint: String? = null,
    val status: Int? = null,
    val message: String? = null,
    val timestamp: Long? = null,
    val duration: Long? = null,
    val requestHeaders: Map<String, String>? = null,
    val requestBody: String? = null,
    val responseHeaders: Map<String, String>? = null,
    val responseBody: String? = null,
    val requestSize: Long? = null,
    val responseSize: Long? = null,
    val protocol: String? = null,
    val remoteAddress: String? = null
)

// ============================================================================
// API RESPONSE -> UI MODEL MAPPING
// This extension function maps the backend DTO into the clean domain model
// (ApiRequest) consumed by the ViewModel and ViewBinding UI layers.
// Modify this mapping function to adapt whatever response structure your server returns.
// ============================================================================

fun ApiRequestDto.toDomain(): ApiRequest {
    val parsedMethod = try {
        HttpMethod.valueOf((method ?: "GET").uppercase())
    } catch (e: IllegalArgumentException) {
        HttpMethod.GET
    }

    val finalUrl = url ?: "https://${host ?: "api.example.com"}${endpoint ?: "/"}"
    val finalHost = host ?: try {
        java.net.URI(finalUrl).host ?: "api.example.com"
    } catch (e: Exception) {
        "api.example.com"
    }
    val finalPath = endpoint ?: try {
        val path = java.net.URI(finalUrl).path
        if (path.isNullOrEmpty()) "/" else path
    } catch (e: Exception) {
        "/"
    }

    val statusCode = status ?: 200
    val statusMessage = message ?: when (statusCode) {
        200 -> "OK"
        201 -> "Created"
        204 -> "No Content"
        301 -> "Moved Permanently"
        302 -> "Found"
        400 -> "Bad Request"
        401 -> "Unauthorized"
        403 -> "Forbidden"
        404 -> "Not Found"
        500 -> "Internal Server Error"
        else -> if (statusCode in 200..299) "Success" else "Error"
    }

    return ApiRequest(
        id = id ?: java.util.UUID.randomUUID().toString(),
        method = parsedMethod,
        url = finalUrl,
        host = finalHost,
        path = finalPath,
        statusCode = statusCode,
        statusMessage = statusMessage,
        timestamp = timestamp ?: System.currentTimeMillis(),
        durationMs = duration ?: 0L,
        requestHeaders = requestHeaders ?: emptyMap(),
        requestBody = requestBody,
        responseHeaders = responseHeaders ?: emptyMap(),
        responseBody = responseBody,
        requestSize = requestSize ?: (requestBody?.toByteArray()?.size?.toLong() ?: 0L),
        responseSize = responseSize ?: (responseBody?.toByteArray()?.size?.toLong() ?: 0L),
        protocol = protocol ?: "HTTP/1.1",
        remoteAddress = remoteAddress ?: "104.21.72.19:443"
    )
}
