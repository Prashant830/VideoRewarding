package com.example.videorewardingsystem.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.videorewardingsystem.ui.viewmodels.SplashViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit,
    viewModel: SplashViewModel = koinViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(isLoading) {
        if (!isLoading) {
            onSplashComplete()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
