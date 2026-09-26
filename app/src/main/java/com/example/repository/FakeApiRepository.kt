package com.example.repository

import com.example.model.ApiRequest
import com.example.model.HttpMethod
import kotlinx.coroutines.delay

class FakeApiRepository : ApiRepository {

    private val now = System.currentTimeMillis()

    private val initialRequests = mutableListOf(
        ApiRequest(
            id = "req-1",
            method = HttpMethod.POST,
            url = "https://httpbin.org/post",
            host = "httpbin.org",
            path = "/post",
            statusCode = 200,
            statusMessage = "OK",
            timestamp = now - 12_000,
            durationMs = 142,
            requestHeaders = mapOf(
                "Content-Type" to "application/json; charset=utf-8",
                "Authorization" to "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6Ik1heWEiLCJpYXQiOjE1MTYyMzkwMjJ9.signature_sample_key",
                "Accept" to "application/json",
                "User-Agent" to "NetworkInspector/2.0 (Android 14; Mobile)"
            ),
            requestBody = """
                {
                  "name": "Maya Lin",
                  "email": "maya@example.com",
                  "role": "Lead Architect",
                  "preferences": {
                    "theme": "dark",
                    "notifications": true
                  }
                }
            """.trimIndent(),
            responseHeaders = mapOf(
                "content-type" to "application/json; charset=utf-8",
                "server" to "gunicorn/19.9.0",
                "date" to "Sat, 26 Sep 2026 15:20:00 GMT",
                "access-control-allow-origin" to "*",
                "x-runtime" to "0.0452s"
            ),
            responseBody = """
                {
                  "id": 1284,
                  "name": "Maya Lin",
                  "email": "maya@example.com",
                  "success": true,
                  "createdAt": "2026-09-26T15:20:00.142Z"
                }
            """.trimIndent(),
            requestSize = 178,
            responseSize = 146,
            protocol = "HTTP/2",
            remoteAddress = "54.161.222.85:443"
        ),
        ApiRequest(
            id = "req-2",
            method = HttpMethod.GET,
            url = "https://api.example.com/cache/30",
            host = "api.example.com",
            path = "/cache/30",
            statusCode = 200,
            statusMessage = "OK",
            timestamp = now - 35_000,
            durationMs = 74,
            requestHeaders = mapOf(
                "Accept" to "application/json",
                "Cache-Control" to "max-age=30",
                "If-None-Match" to "\"w/3194-xyz\""
            ),
            requestBody = null,
            responseHeaders = mapOf(
                "content-type" to "application/json",
                "cache-control" to "public, max-age=30",
                "etag" to "\"w/3194-xyz\"",
                "server" to "cloudflare"
            ),
            responseBody = """
                {
                  "status": "cached",
                  "ttlSeconds": 30,
                  "refreshedAt": "2026-09-26T15:19:40Z",
                  "payload": {
                    "featureFlags": ["new_checkout", "dark_mode_v2"],
                    "appVersion": "3.4.1"
                  }
                }
            """.trimIndent(),
            requestSize = 0,
            responseSize = 184,
            protocol = "HTTP/2",
            remoteAddress = "104.21.72.19:443"
        ),
        ApiRequest(
            id = "req-3",
            method = HttpMethod.POST,
            url = "https://api.example.com/api/payment",
            host = "api.example.com",
            path = "/api/payment",
            statusCode = 201,
            statusMessage = "Created",
            timestamp = now - 62_000,
            durationMs = 389,
            requestHeaders = mapOf(
                "Content-Type" to "application/json",
                "X-Idempotency-Key" to "idem_9108a734bc12",
                "Authorization" to "Bearer pk_live_sample992837482"
            ),
            requestBody = """
                {
                  "amount": 4999,
                  "currency": "USD",
                  "paymentMethod": "pm_card_visa_4242",
                  "receiptEmail": "customer@example.com",
                  "description": "Annual Pro Subscription"
                }
            """.trimIndent(),
            responseHeaders = mapOf(
                "content-type" to "application/json",
                "x-stripe-request-id" to "req_99bxc87a12",
                "strict-transport-security" to "max-age=31536000; includeSubDomains"
            ),
            responseBody = """
                {
                  "chargeId": "ch_3Nk82sKjs921",
                  "status": "succeeded",
                  "amountCaptured": 4999,
                  "currency": "usd",
                  "captured": true,
                  "fee": 175
                }
            """.trimIndent(),
            requestSize = 152,
            responseSize = 142,
            protocol = "HTTP/2",
            remoteAddress = "34.202.112.5:443"
        ),
        ApiRequest(
            id = "req-4",
            method = HttpMethod.GET,
            url = "https://api.example.com/users/profile",
            host = "api.example.com",
            path = "/users/profile",
            statusCode = 200,
            statusMessage = "OK",
            timestamp = now - 110_000,
            durationMs = 96,
            requestHeaders = mapOf(
                "Authorization" to "Bearer session_token_883491",
                "Accept" to "application/json"
            ),
            requestBody = null,
            responseHeaders = mapOf(
                "content-type" to "application/json",
                "vary" to "Accept-Encoding, Authorization",
                "x-cache" to "HIT"
            ),
            responseBody = """
                {
                  "user": {
                    "id": "usr_991823",
                    "username": "developer_pro",
                    "fullName": "Alex Mercer",
                    "email": "alex.m@devstudio.com",
                    "avatarUrl": "https://cdn.example.com/avatars/alex.jpg",
                    "role": "Staff Engineer",
                    "verified": true
                  }
                }
            """.trimIndent(),
            requestSize = 0,
            responseSize = 256,
            protocol = "HTTP/2",
            remoteAddress = "172.67.140.22:443"
        ),
        ApiRequest(
            id = "req-5",
            method = HttpMethod.POST,
            url = "https://auth.example.com/login",
            host = "auth.example.com",
            path = "/login",
            statusCode = 401,
            statusMessage = "Unauthorized",
            timestamp = now - 150_000,
            durationMs = 210,
            requestHeaders = mapOf(
                "Content-Type" to "application/json",
                "X-Device-Id" to "pixel8_hardware_id_99"
            ),
            requestBody = """
                {
                  "email": "wrong.credential@domain.io",
                  "password": "••••••••••••"
                }
            """.trimIndent(),
            responseHeaders = mapOf(
                "content-type" to "application/json",
                "www-authenticate" to "Bearer error=\"invalid_token\"",
                "x-rate-limit-remaining" to "4"
            ),
            responseBody = """
                {
                  "error": "invalid_credentials",
                  "errorDescription": "The provided credentials are not valid. 4 attempts remaining before temporary lockout."
                }
            """.trimIndent(),
            requestSize = 78,
            responseSize = 135,
            protocol = "HTTP/1.1",
            remoteAddress = "192.0.2.14:443"
        ),
        ApiRequest(
            id = "req-6",
            method = HttpMethod.GET,
            url = "https://api.example.com/transactions",
            host = "api.example.com",
            path = "/transactions",
            statusCode = 200,
            statusMessage = "OK",
            timestamp = now - 210_000,
            durationMs = 188,
            requestHeaders = mapOf(
                "Authorization" to "Bearer user_token_live",
                "Accept" to "application/json"
            ),
            requestBody = null,
            responseHeaders = mapOf(
                "content-type" to "application/json; charset=utf-8",
                "content-encoding" to "gzip"
            ),
            responseBody = """
                {
                  "items": [
                    { "id": "tx_101", "merchant": "Coffee Roasters", "amount": -4.75, "currency": "USD" },
                    { "id": "tx_102", "merchant": "Cloud Hosting Inc", "amount": -45.00, "currency": "USD" },
                    { "id": "tx_103", "merchant": "Client Payout", "amount": 1250.00, "currency": "USD" }
                  ],
                  "totalCount": 3,
                  "hasMore": false
                }
            """.trimIndent(),
            requestSize = 0,
            responseSize = 312,
            protocol = "HTTP/2",
            remoteAddress = "104.21.72.19:443"
        ),
        ApiRequest(
            id = "req-7",
            method = HttpMethod.GET,
            url = "https://httpbin.org/redirect-to?url=https://httpbin.org/get",
            host = "httpbin.org",
            path = "/redirect-to",
            statusCode = 302,
            statusMessage = "Found",
            timestamp = now - 280_000,
            durationMs = 85,
            requestHeaders = mapOf(
                "Accept" to "*/*",
                "User-Agent" to "NetworkInspector/2.0"
            ),
            requestBody = null,
            responseHeaders = mapOf(
                "Location" to "https://httpbin.org/get",
                "server" to "gunicorn/19.9.0",
                "content-length" to "0"
            ),
            responseBody = null,
            requestSize = 0,
            responseSize = 0,
            protocol = "HTTP/1.1",
            remoteAddress = "54.161.222.85:443"
        ),
        ApiRequest(
            id = "req-8",
            method = HttpMethod.DELETE,
            url = "https://api.example.com/cart/items/492",
            host = "api.example.com",
            path = "/cart/items/492",
            statusCode = 204,
            statusMessage = "No Content",
            timestamp = now - 350_000,
            durationMs = 112,
            requestHeaders = mapOf(
                "Authorization" to "Bearer cart_session_key"
            ),
            requestBody = null,
            responseHeaders = mapOf(
                "server" to "envoy",
                "x-request-id" to "envoy-trace-991"
            ),
            responseBody = null,
            requestSize = 0,
            responseSize = 0,
            protocol = "HTTP/2",
            remoteAddress = "104.21.72.19:443"
        ),
        ApiRequest(
            id = "req-9",
            method = HttpMethod.PUT,
            url = "https://api.example.com/users/avatar",
            host = "api.example.com",
            path = "/users/avatar",
            statusCode = 200,
            statusMessage = "OK",
            timestamp = now - 420_000,
            durationMs = 520,
            requestHeaders = mapOf(
                "Content-Type" to "application/json",
                "Authorization" to "Bearer user_token_live"
            ),
            requestBody = """
                {
                  "avatarBase64": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJ...",
                  "crop": { "x": 0, "y": 0, "width": 256, "height": 256 }
                }
            """.trimIndent(),
            responseHeaders = mapOf(
                "content-type" to "application/json",
                "etag" to "\"av-99482\""
            ),
            responseBody = """
                {
                  "avatarUrl": "https://cdn.example.com/avatars/usr_991823_v2.jpg",
                  "updatedAt": "2026-09-26T15:15:00Z"
                }
            """.trimIndent(),
            requestSize = 840,
            responseSize = 110,
            protocol = "HTTP/2",
            remoteAddress = "172.67.140.22:443"
        ),
        ApiRequest(
            id = "req-10",
            method = HttpMethod.PATCH,
            url = "https://api.example.com/settings/notifications",
            host = "api.example.com",
            path = "/settings/notifications",
            statusCode = 200,
            statusMessage = "OK",
            timestamp = now - 510_000,
            durationMs = 95,
            requestHeaders = mapOf(
                "Content-Type" to "application/json",
                "Authorization" to "Bearer user_token_live"
            ),
            requestBody = """
                {
                  "marketingEmails": false,
                  "pushSecurityAlerts": true,
                  "pushOrderUpdates": true
                }
            """.trimIndent(),
            responseHeaders = mapOf(
                "content-type" to "application/json"
            ),
            responseBody = """
                {
                  "status": "updated",
                  "settings": {
                    "marketingEmails": false,
                    "pushSecurityAlerts": true,
                    "pushOrderUpdates": true
                  }
                }
            """.trimIndent(),
            requestSize = 98,
            responseSize = 124,
            protocol = "HTTP/2",
            remoteAddress = "104.21.72.19:443"
        ),
        ApiRequest(
            id = "req-11",
            method = HttpMethod.GET,
            url = "https://api.example.com/orders/non_existent_9999",
            host = "api.example.com",
            path = "/orders/non_existent_9999",
            statusCode = 404,
            statusMessage = "Not Found",
            timestamp = now - 600_000,
            durationMs = 62,
            requestHeaders = mapOf(
                "Accept" to "application/json",
                "Authorization" to "Bearer user_token_live"
            ),
            requestBody = null,
            responseHeaders = mapOf(
                "content-type" to "application/json",
                "x-error-code" to "ORDER_NOT_FOUND"
            ),
            responseBody = """
                {
                  "error": "Not Found",
                  "message": "Order with ID 'non_existent_9999' was not found in our database.",
                  "statusCode": 404
                }
            """.trimIndent(),
            requestSize = 0,
            responseSize = 132,
            protocol = "HTTP/2",
            remoteAddress = "104.21.72.19:443"
        ),
        ApiRequest(
            id = "req-12",
            method = HttpMethod.POST,
            url = "https://checkout.example.com/checkout/confirm",
            host = "checkout.example.com",
            path = "/checkout/confirm",
            statusCode = 500,
            statusMessage = "Internal Server Error",
            timestamp = now - 720_000,
            durationMs = 840,
            requestHeaders = mapOf(
                "Content-Type" to "application/json",
                "X-Cart-Id" to "cart_live_9921"
            ),
            requestBody = """
                {
                  "shippingAddressId": "addr_9210",
                  "paymentMethodId": "pm_visa_3010",
                  "applyPromoCode": "SUMMER50"
                }
            """.trimIndent(),
            responseHeaders = mapOf(
                "content-type" to "application/json",
                "server" to "nginx/1.24.0"
            ),
            responseBody = """
                {
                  "error": "InternalServerError",
                  "message": "Payment processing gateway connection timed out. Please retry later.",
                  "timestamp": "2026-09-26T15:10:20Z"
                }
            """.trimIndent(),
            requestSize = 112,
            responseSize = 158,
            protocol = "HTTP/1.1",
            remoteAddress = "52.84.162.11:443"
        ),
        ApiRequest(
            id = "req-13",
            method = HttpMethod.GET,
            url = "https://api.example.com/search?q=httpbin&type=endpoint",
            host = "api.example.com",
            path = "/search",
            statusCode = 200,
            statusMessage = "OK",
            timestamp = now - 850_000,
            durationMs = 115,
            requestHeaders = mapOf(
                "Accept" to "application/json"
            ),
            requestBody = null,
            responseHeaders = mapOf(
                "content-type" to "application/json"
            ),
            responseBody = """
                {
                  "query": "httpbin",
                  "totalHits": 4,
                  "results": [
                    { "name": "/post", "type": "endpoint" },
                    { "name": "/get", "type": "endpoint" },
                    { "name": "/status", "type": "endpoint" },
                    { "name": "/redirect-to", "type": "endpoint" }
                  ]
                }
            """.trimIndent(),
            requestSize = 0,
            responseSize = 210,
            protocol = "HTTP/2",
            remoteAddress = "104.21.72.19:443"
        ),
        ApiRequest(
            id = "req-14",
            method = HttpMethod.GET,
            url = "https://httpbin.org/get?inspector=active",
            host = "httpbin.org",
            path = "/get",
            statusCode = 200,
            statusMessage = "OK",
            timestamp = now - 990_000,
            durationMs = 138,
            requestHeaders = mapOf(
                "Accept" to "application/json",
                "User-Agent" to "NetworkInspector/2.0"
            ),
            requestBody = null,
            responseHeaders = mapOf(
                "content-type" to "application/json",
                "server" to "gunicorn/19.9.0"
            ),
            responseBody = """
                {
                  "args": { "inspector": "active" },
                  "headers": {
                    "Accept": "application/json",
                    "Host": "httpbin.org",
                    "User-Agent": "NetworkInspector/2.0"
                  },
                  "origin": "73.189.14.22",
                  "url": "https://httpbin.org/get?inspector=active"
                }
            """.trimIndent(),
            requestSize = 0,
            responseSize = 245,
            protocol = "HTTP/2",
            remoteAddress = "54.161.222.85:443"
        ),
        ApiRequest(
            id = "req-15",
            method = HttpMethod.POST,
            url = "https://analytics.example.com/events",
            host = "analytics.example.com",
            path = "/events",
            statusCode = 200,
            statusMessage = "OK",
            timestamp = now - 1_200_000,
            durationMs = 45,
            requestHeaders = mapOf(
                "Content-Type" to "application/json"
            ),
            requestBody = """
                {
                  "event": "screen_view",
                  "properties": {
                    "screen": "api_inspector_dashboard",
                    "platform": "Android",
                    "sessionDuration": 412
                  }
                }
            """.trimIndent(),
            responseHeaders = mapOf(
                "content-type" to "application/json"
            ),
            responseBody = """{ "status": "recorded", "batches": 1 }""",
            requestSize = 130,
            responseSize = 38,
            protocol = "HTTP/2",
            remoteAddress = "35.190.247.1:443"
        ),
        ApiRequest(
            id = "req-16",
            method = HttpMethod.GET,
            url = "http://insecure.example.com/legacy/status",
            host = "insecure.example.com",
            path = "/legacy/status",
            statusCode = 400,
            statusMessage = "Bad Request",
            timestamp = now - 1_450_000,
            durationMs = 82,
            requestHeaders = mapOf(
                "Accept" to "application/json"
            ),
            requestBody = null,
            responseHeaders = mapOf(
                "content-type" to "application/json"
            ),
            responseBody = """
                {
                  "error": "UpgradeRequired",
                  "message": "Plain HTTP is deprecated. Please upgrade connection to HTTPS."
                }
            """.trimIndent(),
            requestSize = 0,
            responseSize = 110,
            protocol = "HTTP/1.1",
            remoteAddress = "93.184.216.34:80"
        ),
        ApiRequest(
            id = "req-17",
            method = HttpMethod.GET,
            url = "https://api.example.com/server/health",
            host = "api.example.com",
            path = "/server/health",
            statusCode = 200,
            statusMessage = "OK",
            timestamp = now - 1_800_000,
            durationMs = 28,
            requestHeaders = mapOf(
                "Accept" to "application/json"
            ),
            requestBody = null,
            responseHeaders = mapOf(
                "content-type" to "application/json",
                "cache-control" to "no-cache"
            ),
            responseBody = """
                {
                  "status": "healthy",
                  "database": "connected",
                  "redisCluster": "operational",
                  "uptimeSeconds": 894320
                }
            """.trimIndent(),
            requestSize = 0,
            responseSize = 98,
            protocol = "HTTP/2",
            remoteAddress = "104.21.72.19:443"
        ),
        ApiRequest(
            id = "req-18",
            method = HttpMethod.POST,
            url = "https://feedback.example.com/submit",
            host = "feedback.example.com",
            path = "/submit",
            statusCode = 201,
            statusMessage = "Created",
            timestamp = now - 2_200_000,
            durationMs = 175,
            requestHeaders = mapOf(
                "Content-Type" to "application/json",
                "Authorization" to "Bearer user_token_live"
            ),
            requestBody = """
                {
                  "rating": 5,
                  "category": "ux_design",
                  "comment": "The bottom sheet inspector is exceptionally fast and clean!"
                }
            """.trimIndent(),
            responseHeaders = mapOf(
                "content-type" to "application/json"
            ),
            responseBody = """
                {
                  "feedbackId": "fb_89104",
                  "status": "received",
                  "ticketAssigned": "CS-4192"
                }
            """.trimIndent(),
            requestSize = 108,
            responseSize = 76,
            protocol = "HTTP/2",
            remoteAddress = "198.51.100.4:443"
        )
    )

    private val currentData = mutableListOf<ApiRequest>().apply {
        addAll(initialRequests)
    }

    override suspend fun getRequests(): List<ApiRequest> {
        // Simulate minor network/disk loading time for smooth skeleton animation
        delay(400)
        return synchronized(currentData) {
            currentData.toList()
        }
    }

    override suspend fun clearHistory() {
        delay(150)
        synchronized(currentData) {
            currentData.clear()
        }
    }

    /**
     * Helper to reload sample data if user wants to repopulate after clearing.
     */
    fun reloadInitialData() {
        synchronized(currentData) {
            currentData.clear()
            currentData.addAll(initialRequests)
        }
    }
}
