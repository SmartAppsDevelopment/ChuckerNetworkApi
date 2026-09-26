package com.example.model

enum class StatusCategory(val label: String) {
    SUCCESS("Success"),
    REDIRECT("Redirect"),
    CLIENT_ERROR("Client Error"),
    SERVER_ERROR("Server Error")
}
