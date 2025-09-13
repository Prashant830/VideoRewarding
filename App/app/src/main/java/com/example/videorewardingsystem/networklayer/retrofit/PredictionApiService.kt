// networklayer/retrofit/PredictionApiService.kt
package com.example.videorewardingsystem.networklayer.retrofit

import com.example.videorewardingsystem.networklayer.retrofit.model.PredictionResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Url

interface PredictionApiService {
    @POST
    suspend fun runPrediction(
        @Url url: String, // dynamic URL (modelId + params)
        @retrofit2.http.Body body: RequestBody
    ): Response<PredictionResponse>
}
