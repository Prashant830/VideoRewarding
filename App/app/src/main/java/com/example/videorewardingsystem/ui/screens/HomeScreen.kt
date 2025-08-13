package com.example.videorewardingsystem.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.videorewardingsystem.ui.screens.components.BottomNavigationBar
import com.example.videorewardingsystem.ui.viewmodels.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToPlayer: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state = viewModel.uiState.collectAsState().value

    Scaffold(
        bottomBar = { BottomNavigationBar(onNavigateToProfile, onNavigateToPlayer) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.isLoading -> CircularProgressIndicator()
                state.errorMessage != null -> Text("Error: ${state.errorMessage}", color = Color.Red)
                else -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Videos Loaded: ${state.videos.size}")
                    state.videos.forEach { video ->
                        Text(video.videoUrl)
                    }
                }
            }
        }
    }
}


