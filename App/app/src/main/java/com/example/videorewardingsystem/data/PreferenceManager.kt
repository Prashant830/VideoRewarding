package com.example.videorewardingsystem.data

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREF_NAME, Context.MODE_PRIVATE
    )

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

    companion object {
        private const val PREF_NAME = "video_rewarding_prefs"
        private const val KEY_USERNAME = "user_name"
        private const val KEY_EMAIL = "email"
        private const val KEY_WALLET = "wallet_address"
    }
}
