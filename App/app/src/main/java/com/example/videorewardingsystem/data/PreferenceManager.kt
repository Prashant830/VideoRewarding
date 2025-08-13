package com.example.videorewardingsystem.data

import android.content.Context
import android.content.SharedPreferences
import com.example.videorewardingsystem.networklayer.retrofit.model.VideoModel

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

    fun saveOrUpdateVideoProgress(videoId: Int, videoUrl: String, currentWatched: Long) {
        val currentData = sharedPreferences.getString(KEY_VIDEO_PROGRESS, "") ?: ""
        val entries = currentData.split(";")
            .filter { it.isNotBlank() }
            .mapNotNull { entry ->
                val parts = entry.split("-")
                    VideoModel(parts[0].toInt(), parts[1], parts[2].toLongOrNull() ?: 0L)
            }.toMutableList()

        val index = entries.indexOfFirst { it.videoId == videoId }

        if (index >= 0) {
            // Update existing
            entries[index] = VideoModel(videoId, videoUrl, currentWatched)
        } else {
            // Add new
            entries.add(VideoModel(videoId, videoUrl, currentWatched))
        }

        val serialized = entries.joinToString(";") { "${it.videoId}-${it.videoUrl}-${it.currentWatched}" }
        sharedPreferences.edit()
            .putString(KEY_VIDEO_PROGRESS, serialized)
            .apply()
    }

    fun getVideoProgressList(): List<VideoModel> {
        val currentData = sharedPreferences.getString(KEY_VIDEO_PROGRESS, "") ?: ""
        return currentData.split(";")
            .filter { it.isNotBlank() }
            .mapNotNull { entry ->
                val parts = entry.split("-")
                if (parts.size == 3) {
                    VideoModel(parts[0].toInt(), parts[1], parts[2].toLongOrNull() ?: 0L)
                } else null
            }
    }

    fun getVideoProgressById(videoId: Int): VideoModel? {
        return getVideoProgressList().find { it.videoId == videoId }
    }


    companion object {
        private const val PREF_NAME = "video_rewarding_prefs"
        private const val KEY_USERNAME = "user_name"
        private const val KEY_EMAIL = "email"
        private const val KEY_WALLET = "wallet_address"
        private const val KEY_VIDEO_PROGRESS = "video_progress_list"
    }
}
