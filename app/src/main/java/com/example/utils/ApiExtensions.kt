package com.example.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.R
import com.example.model.HttpMethod
import com.example.model.StatusCategory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ApiExtensions {

    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val fullDateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())

    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }

    fun formatFullDateTime(timestamp: Long): String {
        return fullDateTimeFormat.format(Date(timestamp))
    }

    fun formatDuration(durationMs: Long): String {
        return if (durationMs >= 1000) {
            String.format(Locale.getDefault(), "%.2f s", durationMs / 1000.0)
        } else {
            "$durationMs ms"
        }
    }

    fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> String.format(Locale.getDefault(), "%.1f KB", bytes / 1024.0)
            else -> String.format(Locale.getDefault(), "%.2f MB", bytes / (1024.0 * 1024.0))
        }
    }

    fun getMethodTextColor(context: Context, method: HttpMethod): Int {
        val colorRes = when (method) {
            HttpMethod.GET -> R.color.method_get_text
            HttpMethod.POST -> R.color.method_post_text
            HttpMethod.PUT -> R.color.method_put_text
            HttpMethod.PATCH -> R.color.method_patch_text
            HttpMethod.DELETE -> R.color.method_delete_text
        }
        return ContextCompat.getColor(context, colorRes)
    }

    fun getMethodBgColor(context: Context, method: HttpMethod): Int {
        val colorRes = when (method) {
            HttpMethod.GET -> R.color.method_get_bg
            HttpMethod.POST -> R.color.method_post_bg
            HttpMethod.PUT -> R.color.method_put_bg
            HttpMethod.PATCH -> R.color.method_patch_bg
            HttpMethod.DELETE -> R.color.method_delete_bg
        }
        return ContextCompat.getColor(context, colorRes)
    }

    fun getStatusTextColor(context: Context, statusCode: Int): Int {
        val colorRes = when {
            statusCode in 200..299 -> R.color.status_success_text
            statusCode in 300..399 -> R.color.status_redirect_text
            else -> R.color.status_error_text
        }
        return ContextCompat.getColor(context, colorRes)
    }

    fun getStatusBgColor(context: Context, statusCode: Int): Int {
        val colorRes = when {
            statusCode in 200..299 -> R.color.status_success_bg
            statusCode in 300..399 -> R.color.status_redirect_bg
            else -> R.color.status_error_bg
        }
        return ContextCompat.getColor(context, colorRes)
    }
}
