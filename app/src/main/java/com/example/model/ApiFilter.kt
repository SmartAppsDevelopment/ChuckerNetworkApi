package com.example.model

data class ApiFilter(
    val methods: Set<HttpMethod> = emptySet(),
    val statuses: Set<StatusCategory> = emptySet(),
    val httpsOnly: Boolean = false,
    val errorsOnly: Boolean = false
) {
    val isActive: Boolean
        get() = methods.isNotEmpty() || statuses.isNotEmpty() || httpsOnly || errorsOnly

    val activeCount: Int
        get() = methods.size + statuses.size + (if (httpsOnly) 1 else 0) + (if (errorsOnly) 1 else 0)
}
