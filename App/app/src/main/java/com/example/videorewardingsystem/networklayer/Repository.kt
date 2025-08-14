package com.example.videorewardingsystem.networklayer

import com.example.videorewardingsystem.networklayer.retrofit.ApiService
import com.example.videorewardingsystem.networklayer.retrofit.model.RewardModel
import com.example.videorewardingsystem.networklayer.retrofit.model.VideoResponse
import okhttp3.ResponseBody
import retrofit2.Response


class Repository(private val apiService: ApiService) : ApiService {

  override suspend fun getVideos(): Response<VideoResponse> {
            return try {
                apiService.getVideos()
            } catch (e: Exception) {
                Response.error(500, ResponseBody.create(null, e.message.toString()))
            }
        }

        override suspend fun sendReward(request: RewardModel): Response<Any> {
            return try {
                apiService.sendReward(request)
            } catch (e: Exception) {
                Response.error(500, ResponseBody.create(null, e.message.toString()))
            }
        }
}
