package com.example.videorewardingsystem.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    fun updateUserName(name: String) {
        _uiState.value = _uiState.value.copy(userName = name)
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun updateWalletAddress(address: String) {
        _uiState.value = _uiState.value.copy(metamaskWallet = address)
    }
}

data class ProfileUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val email: String = "",
    val metamaskWallet: String = ""
)
