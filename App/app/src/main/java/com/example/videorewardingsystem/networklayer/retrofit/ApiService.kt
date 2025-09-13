package com.example.videorewardingsystem.networklayer.retrofit

import com.example.videorewardingsystem.networklayer.retrofit.model.PredictionResponse
import com.example.videorewardingsystem.networklayer.retrofit.model.RewardModel
import com.example.videorewardingsystem.networklayer.retrofit.model.VideoResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {
    @GET("videos")
    suspend fun getVideos(): Response<VideoResponse>

    @POST("sendReward")
    suspend fun sendReward(@Body request: RewardModel): Response<Any>

}