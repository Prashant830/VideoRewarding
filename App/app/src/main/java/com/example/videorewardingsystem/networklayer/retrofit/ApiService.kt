package com.example.videorewardingsystem.networklayer.retrofit

import com.example.videorewardingsystem.networklayer.retrofit.model.RewardModel
import com.example.videorewardingsystem.networklayer.retrofit.model.VideoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("videos")
    suspend fun getVideos(): Response<VideoResponse>

    @POST("sendReward")
    suspend fun sendReward(@Body request: RewardModel): Response<Any>
}