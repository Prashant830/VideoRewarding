package com.example.videorewardingsystem.ui.screens
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.example.videorewardingsystem.networklayer.retrofit.model.VideoModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import com.example.videorewardingsystem.ui.screens.components.CommonTopBar
import com.example.videorewardingsystem.ui.viewmodels.HomeViewModel
import com.example.videorewardingsystem.ui.viewmodels.PlayerViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlayerScreen(
    videoModel: VideoModel?,
    onBackClick: () -> Unit,
    viewModel: PlayerViewModel = koinViewModel(),
    homeViewModel: HomeViewModel = koinViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Video Player / Claim Rewards",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (videoModel == null) {
                // Show placeholder UI if null
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    androidx.compose.material3.Text(
                        text = "Welcome to the Claim Reward System",
                    )
                }
            } else {
                // Extract YouTube ID
                val videoId = videoModel.videoUrl.let { url ->
                    when {
                        url.contains("shorts/") -> url.substringAfter("shorts/").substringBefore("?")
                        url.contains("watch?v=") -> url.substringAfter("watch?v=").substringBefore("&")
                        else -> url
                    }
                }

                AndroidView(
                    factory = { context ->
                        YouTubePlayerView(context).apply {
                            lifecycleOwner.lifecycle.addObserver(this)

                            addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                                override fun onReady(youTubePlayer: YouTubePlayer) {
                                    youTubePlayer.loadVideo(videoId, 0f)
                                }

                                override fun onCurrentSecond(
                                    youTubePlayer: YouTubePlayer,
                                    second: Float
                                ) {
                                    val totalRuntimeSeconds = videoModel.totalRuntime
                                    homeViewModel.saveOrUpdateVideoProgress(
                                        videoModel.videoId,
                                        videoModel.videoUrl,
                                        if (videoModel.currentWatched >= totalRuntimeSeconds) totalRuntimeSeconds else second.toLong(),
                                        videoModel.totalRuntime
                                    )
                                }
                            })

                            post {
                                toggleFullScreen()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

