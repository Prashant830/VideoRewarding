package com.example.videorewardingsystem.utils

object Utils {

    /**
     * Formats seconds into mm:ss format
     */
    fun formatTime(seconds: Long): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return String.format("%d:%02d", minutes, secs)
    }
}
