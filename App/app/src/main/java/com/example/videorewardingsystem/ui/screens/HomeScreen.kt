package com.example.videorewardingsystem.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.videorewardingsystem.R
import com.example.videorewardingsystem.networklayer.retrofit.model.VideoModel
import com.example.videorewardingsystem.ui.screens.components.BottomNavigationBar
import com.example.videorewardingsystem.ui.screens.components.HomeTopBar
import com.example.videorewardingsystem.ui.screens.components.ErrorMessage
import com.example.videorewardingsystem.ui.viewmodels.HomeViewModel
import com.example.videorewardingsystem.utils.Utils.formatTime
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToPlayer: (VideoModel?) -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state = viewModel.uiState.collectAsState().value

    // Refresh whenever the composable enters the screen
    LaunchedEffect(Unit) {
        viewModel.fetchVideos()
    }

    Scaffold(
        topBar = { if (state.errorMessage == null) HomeTopBar() },
        bottomBar = { BottomNavigationBar(
            onNavigateToProfile = onNavigateToProfile,
            onNavigateToPlayer = { onNavigateToPlayer(null)} // Pass null if no video
        ) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            when {
                state.isLoading -> CircularProgressIndicator()
                state.errorMessage != null -> ErrorMessage(state.errorMessage)
                else -> VideoList(videos = state.videos, onClick = onNavigateToPlayer, onNavigateToPlayer = onNavigateToPlayer)
            }
        }
    }
}

@Composable
private fun VideoList(
    videos: List<VideoModel>,
    onClick: (VideoModel?) -> Unit,
    onNavigateToPlayer: (VideoModel?) -> Unit
    ) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(videos) { video ->
            VideoItem(video = video, bannerRes = R.drawable.crypto_banner, onClick = onClick, onNavigateToPlayer = onNavigateToPlayer)
        }
    }
}

@Composable
fun VideoItem(
    video: VideoModel,
    bannerRes: Int,
    onClick: (VideoModel?) -> Unit,
    onNavigateToPlayer: (VideoModel?) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Banner image clickable (only this triggers video player)
        Image(
            painter = painterResource(id = bannerRes),
            contentDescription = "Video Thumbnail",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clickable { onClick(video) } // Click to watch video
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Video ID: ${video.videoId}", style = MaterialTheme.typography.bodyLarge)

            if (video.currentWatched >= video.totalRuntime) {
                Text(
                    text = "✅ Successfully Watched – Claim Reward",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    modifier = Modifier
                        .background(
                            color = Color(0xFF4CAF50), // success green
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {onNavigateToPlayer(null)}
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                )
            } else {
                Text(
                    text = "${formatTime(video.currentWatched)}/${formatTime(video.totalRuntime)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}
