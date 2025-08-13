package com.example.videorewardingsystem.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.videorewardingsystem.ui.theme.ThemeColor
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

@Composable
fun BottomNavigationBar(
    onNavigateToProfile: () -> Unit,
    onNavigateToPlayer: () -> Unit
) {
    NavigationBar(containerColor = ThemeColor) {
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToProfile,
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White) },
            label = { Text("Profile", color = Color.White) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Color.White,
                unselectedTextColor = Color.White,
                indicatorColor = Color.DarkGray
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToPlayer,
            icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Player", tint = Color.White) },
            label = { Text("Player", color = Color.White) },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Color.White,
                unselectedTextColor = Color.White,
                indicatorColor = Color.DarkGray
            )
        )
    }
}

