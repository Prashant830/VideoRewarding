package com.example.videorewardingsystem.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videorewardingsystem.data.PreferenceManager
import com.example.videorewardingsystem.networklayer.Repository
import com.example.videorewardingsystem.networklayer.retrofit.model.VideoModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: Repository,
    private val sharedPreferences: PreferenceManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchVideos()
    }

    fun fetchVideos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val cachedVideos = getVideoProgressList()
            if (cachedVideos != null && cachedVideos.isNotEmpty()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    videos = cachedVideos
                )
            } else {
                // Fetch from network
                val response = repository.getVideos()
                if (response.isSuccessful) {
                    val videos = response.body()?.videos ?: emptyList()
                    videos.forEach { video ->
                        saveOrUpdateVideoProgress(
                            video.videoId,
                            video.videoUrl,
                            video.currentWatched,
                            video.totalRuntime
                        )
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        videos = videos
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun saveOrUpdateVideoProgress(videoId: Int, videoUrl: String, currentWatched: Long, totalRuntime: Long){
        sharedPreferences.saveOrUpdateVideoProgress(videoId, videoUrl, currentWatched, totalRuntime)
        val updatedVideos = _uiState.value.videos.map { video ->
            if (video.videoId == videoId) video.copy(currentWatched = currentWatched)
            else video
        }
        _uiState.value = _uiState.value.copy(videos = updatedVideos)
    }


    private fun getVideoProgressList(): List<VideoModel> {
        return sharedPreferences.getVideoProgressList() ?: emptyList()
    }

}

data class HomeUiState(
    val isLoading: Boolean = false,
    val videos: List<VideoModel> = emptyList(),
    val errorMessage: String? = null
)
