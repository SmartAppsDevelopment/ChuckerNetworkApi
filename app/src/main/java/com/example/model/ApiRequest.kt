package com.example.model

data class ApiRequest(
    val id: String,
    val method: HttpMethod,
    val url: String,
    val host: String,
    val path: String,
    val statusCode: Int,
    val statusMessage: String,
    val timestamp: Long,
    val durationMs: Long,
    val requestHeaders: Map<String, String>,
    val requestBody: String?,
    val responseHeaders: Map<String, String>,
    val responseBody: String?,
    val requestSize: Long,
    val responseSize: Long,
    val protocol: String = "HTTP/1.1",
    val remoteAddress: String = "104.21.72.19:443",
    val connectionDurationMs: Long = 38L
) {
    val isSuccess: Boolean
        get() = statusCode in 200..299

    val isRedirect: Boolean
        get() = statusCode in 300..399

    val isClientError: Boolean
        get() = statusCode in 400..499

    val isServerError: Boolean
        get() = statusCode in 500..599

    val isError: Boolean
        get() = statusCode >= 400

    val statusCategory: StatusCategory
        get() = when (statusCode) {
            in 200..299 -> StatusCategory.SUCCESS
            in 300..399 -> StatusCategory.REDIRECT
            in 400..499 -> StatusCategory.CLIENT_ERROR
            else -> StatusCategory.SERVER_ERROR
        }

    val isHttps: Boolean
        get() = url.startsWith("https://", ignoreCase = true)
}
