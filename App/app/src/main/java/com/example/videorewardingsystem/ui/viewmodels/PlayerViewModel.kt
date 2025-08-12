package com.example.videorewardingsystem.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState = _uiState.asStateFlow()
}

data class PlayerUiState(
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L
)
