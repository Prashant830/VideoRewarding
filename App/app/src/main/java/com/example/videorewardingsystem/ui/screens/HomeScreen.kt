package com.example.videorewardingsystem.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
    onNavigateToPlayer: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state = viewModel.uiState.collectAsState().value

    Scaffold(
        topBar = {
            if (state.errorMessage == null) {
                HomeTopBar()
            }
        },
        bottomBar = { BottomNavigationBar(onNavigateToProfile, onNavigateToPlayer) }
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
                else -> VideoList(state.videos)
            }
        }
    }
}

@Composable
private fun VideoList(videos: List<VideoModel>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(videos.size) { index ->
            val video = videos[index]
            VideoItem(
                videoId = "Video ID: ${video.videoId}",
                progress = formatTime(video.currentWatched),
                bannerRes = R.drawable.crypto_banner
            )
        }
    }
}

@Composable
fun VideoItem(videoId: String, progress: String, bannerRes: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = bannerRes),
            contentDescription = "Video Thumbnail",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = videoId, style = MaterialTheme.typography.bodyLarge)
            Text(text = progress, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
    }
}
