package com.example.videorewardingsystem.ui.viewmodels

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videorewardingsystem.data.PreferenceManager
import com.example.videorewardingsystem.networklayer.Repository
import com.example.videorewardingsystem.networklayer.retrofit.model.PredictionResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream


class PlayerViewModel(
    private val repository: Repository,
    private val sharedPreferences: PreferenceManager
) : ViewModel() {


    private val _predictions = MutableStateFlow<Map<Int, Float>>(emptyMap())
    val predictions: StateFlow<Map<Int, Float>> = _predictions

    fun runPrediction(cubeIndex: Int, bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                val response = repository.runPrediction(bitmap)
                if (response != null) {
                    val result: PredictionResponse = response
                    val confidence = result.predictions.firstOrNull()?.confidence?.times(100) ?: 0f
                    _predictions.value = _predictions.value.toMutableMap().apply {
                        put(cubeIndex, confidence)
                    }
                } else {
                    Log.e("PredictionViewModel", "API Error: ${response?.toString()}")
                }
            } catch (e: Exception) {
                Log.e("PredictionViewModel", "Exception: ${e.message}")
            }
        }
    }
}


