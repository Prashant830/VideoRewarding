package com.example.videorewardingsystem.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videorewardingsystem.data.PreferenceManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ProfileViewModel(
    private val preferenceManager: PreferenceManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadSavedData()
    }

    private fun loadSavedData() {
        val (userName, email, wallet) = preferenceManager.getUserData()
        _uiState.value = _uiState.value.copy(
            userName = userName,
            email = email,
            metamaskWallet = wallet
        )
    }

    fun updateUserName(name: String) {
        _uiState.value = _uiState.value.copy(userName = name)
        saveData()
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
        saveData()
    }

    fun updateWalletAddress(address: String) {
        _uiState.value = _uiState.value.copy(metamaskWallet = address)
        saveData()
    }

    private fun saveData() {
        with(_uiState.value) {
            preferenceManager.saveUserData(userName, email, metamaskWallet)
        }
    }

    fun updateUserInfo() {
        saveData()
        _uiState.value = _uiState.value.copy(isUpdateSuccess = true)
        // Reset update success after a delay
        viewModelScope.launch {
            delay(2000)
            _uiState.value = _uiState.value.copy(isUpdateSuccess = false)
        }
    }

    fun getWalletBalanceUrl(): String {
        return if (_uiState.value.metamaskWallet.isNotEmpty()) {
            "https://sepolia.etherscan.io/address/${_uiState.value.metamaskWallet}"
        } else ""
    }
}

data class ProfileUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val email: String = "",
    val metamaskWallet: String = "",
    val isUpdateSuccess: Boolean = false
)
