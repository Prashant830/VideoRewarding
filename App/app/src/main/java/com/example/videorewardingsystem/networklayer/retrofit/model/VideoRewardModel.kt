package com.example.videorewardingsystem.networklayer.retrofit.model

import com.google.gson.annotations.SerializedName


data class VideoResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("videos")
    val videos: List<VideoModel>
)

data class VideoModel(
    @SerializedName("videoId")
    val videoId: Int = 0,
    @SerializedName("videoUrl")
    val videoUrl: String = "",
    @SerializedName("currentWatched")
    val currentWatched : Long = 0L
)

data class RewardModel(
    @SerializedName("code")
    val recipient: String,
)