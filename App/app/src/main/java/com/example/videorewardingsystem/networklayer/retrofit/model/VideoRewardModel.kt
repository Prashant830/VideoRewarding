package com.example.videorewardingsystem.networklayer.retrofit.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class VideoResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("videos")
    val videos: List<VideoModel>
)
@Parcelize
data class VideoModel(
    @SerializedName("videoId")
    val videoId: Int = 0,
    @SerializedName("videoUrl")
    val videoUrl: String = "",
    @SerializedName("currentWatched")
    val currentWatched : Long = 0L,
    @SerializedName("totalRuntime")
    val totalRuntime : Long = 0L,
): Parcelable

data class RewardModel(
    @SerializedName("code")
    val recipient: String,
)