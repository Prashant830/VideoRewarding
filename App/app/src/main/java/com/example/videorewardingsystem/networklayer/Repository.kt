package com.example.videorewardingsystem.networklayer

import com.example.videorewardingsystem.networklayer.retrofit.ApiService
import com.example.videorewardingsystem.networklayer.retrofit.model.RewardModel
import com.example.videorewardingsystem.networklayer.retrofit.model.VideoResponse
import retrofit2.Response

class Repository(private val apiService: ApiService) : ApiService {

    override suspend fun getVideos(): Response<VideoResponse> {
        return apiService.getVideos()
    }

    override suspend fun sendReward(request: RewardModel): Response<Any> {
        return apiService.sendReward(request)
    }
}
