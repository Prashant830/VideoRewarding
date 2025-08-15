package com.example.videorewardingsystem.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.videorewardingsystem.data.PreferenceManager
import com.example.videorewardingsystem.networklayer.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayerViewModel(
    private val repository: Repository,
    private val sharedPreferences: PreferenceManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState = _uiState.asStateFlow()
}


data class PlayerUiState(
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L
)
