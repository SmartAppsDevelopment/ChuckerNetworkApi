package com.example.utils

import org.json.JSONArray
import org.json.JSONObject

object JsonFormatter {

    /**
     * Formats a raw JSON string into a pretty-printed 2-space indented string.
     * Falls back to raw string if the payload is not valid JSON.
     */
    fun formatJson(rawJson: String?): String {
        if (rawJson.isNullOrBlank()) return ""
        val trimmed = rawJson.trim()
        return try {
            when {
                trimmed.startsWith("{") -> {
                    JSONObject(trimmed).toString(2)
                }
                trimmed.startsWith("[") -> {
                    JSONArray(trimmed).toString(2)
                }
                else -> trimmed
            }
        } catch (e: Exception) {
            trimmed
        }
    }
}
