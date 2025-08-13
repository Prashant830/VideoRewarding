package com.example.videorewardingsystem.data

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREF_NAME, Context.MODE_PRIVATE
    )

    // ===== Existing user save/load =====
    fun saveUserData(userName: String, email: String, walletAddress: String) {
        sharedPreferences.edit().apply {
            putString(KEY_USERNAME, userName)
            putString(KEY_EMAIL, email)
            putString(KEY_WALLET, walletAddress)
            apply()
        }
    }

    fun getUserData(): Triple<String, String, String> {
        val userName = sharedPreferences.getString(KEY_USERNAME, "") ?: ""
        val email = sharedPreferences.getString(KEY_EMAIL, "") ?: ""
        val wallet = sharedPreferences.getString(KEY_WALLET, "") ?: ""
        return Triple(userName, email, wallet)
    }

    // ===== Video progress save/load =====
    fun saveOrUpdateVideoProgress(videoId: String, videoUrl: String, currentWatched: Long) {
        val currentData = sharedPreferences.getString(KEY_VIDEO_PROGRESS, "") ?: ""
        val entries = currentData.split(";").filter { it.isNotBlank() }.toMutableList()

        // Find if videoId exists
        val index = entries.indexOfFirst { it.startsWith("$videoId-") }

        if (index >= 0) {
            // Update existing
            entries[index] = "$videoId-$videoUrl-$currentWatched"
        } else {
            // Add new
            entries.add("$videoId-$videoUrl-$currentWatched")
        }

        sharedPreferences.edit()
            .putString(KEY_VIDEO_PROGRESS, entries.joinToString(";"))
            .apply()
    }

    private fun getVideoProgressList(): List<Triple<String, String, Long>> {
        val currentData = sharedPreferences.getString(KEY_VIDEO_PROGRESS, "") ?: ""
        return currentData.split(";")
            .filter { it.isNotBlank() }
            .map {
                val parts = it.split("-")
                Triple(parts[0], parts[1], parts[2].toLong())
            }
    }

    fun getVideoProgressById(videoId: String): Triple<String, String, Long>? {
        val entries = getVideoProgressList()
        return entries.find { it.first == videoId }
    }

    companion object {
        private const val PREF_NAME = "video_rewarding_prefs"
        private const val KEY_USERNAME = "user_name"
        private const val KEY_EMAIL = "email"
        private const val KEY_WALLET = "wallet_address"
        private const val KEY_VIDEO_PROGRESS = "video_progress_list"
    }
}
