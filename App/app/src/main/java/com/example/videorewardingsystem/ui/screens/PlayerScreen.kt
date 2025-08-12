package com.example.videorewardingsystem.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.videorewardingsystem.ui.screens.components.CommonTopBar
import com.example.videorewardingsystem.ui.viewmodels.PlayerViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlayerScreen(
    onBackClick: () -> Unit,
    viewModel: PlayerViewModel = koinViewModel()
) {
    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Player",
                onBackClick = onBackClick
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("Player Screen")
        }
    }
}
