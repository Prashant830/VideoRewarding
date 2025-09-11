package com.example.videorewardingsystem.ui.screens

import android.Manifest
import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.example.videorewardingsystem.networklayer.retrofit.model.VideoModel
import com.example.videorewardingsystem.ui.screens.components.CommonTopBar
import com.example.videorewardingsystem.ui.viewmodels.HomeViewModel
import com.example.videorewardingsystem.ui.viewmodels.PlayerViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Color as SColor
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ShapeFactory
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import org.koin.androidx.compose.koinViewModel

private const val TAG = "PlayerScreenAR"

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PlayerScreen(
    videoModel: VideoModel?,
    onBackClick: () -> Unit,
    viewModel: PlayerViewModel = koinViewModel(),
    homeViewModel: HomeViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isTracking by remember { mutableStateOf(false) }

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
                val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
                val blocksPlaced = remember { mutableStateOf(false) }
                val blockDistances = listOf(5f, 10f, 10f, 15f, 15f) // approximate distance
                val blocksAnchors = remember { mutableStateListOf<AnchorNode>() }


                when {
                    cameraPermissionState.status.isGranted -> {
                        AndroidView(
                            factory = { ctx ->
                                val arView = ArSceneView(ctx)

                                // Far clip plane to view distant blocks
                                arView.scene.camera.farClipPlane = 50f

                                // ARCore session
                                val session = try { Session(ctx) } catch (e: Exception) { null }

                                session?.let {
                                    val config = Config(it)
                                    config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
                                    config.lightEstimationMode = Config.LightEstimationMode.DISABLED
                                    it.configure(config)
                                    arView.session = it
                                    Log.d(TAG, "AR Session configured")
                                }

                                // Lifecycle handling
                                lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                                    override fun onResume(owner: LifecycleOwner) { arView.resume() }
                                    override fun onPause(owner: LifecycleOwner) { arView.pause() }
                                    override fun onDestroy(owner: LifecycleOwner) { arView.destroy() }
                                })

                                // Place blocks once at random positions relative to starting camera pose
                                var startPose: Pose? = null

                                arView.scene.addOnUpdateListener {
                                    val frame = arView.arFrame ?: return@addOnUpdateListener
                                    if (frame.camera.trackingState != TrackingState.TRACKING) return@addOnUpdateListener

                                    if (startPose == null) startPose = frame.camera.pose

                                    if (!blocksPlaced.value && startPose != null) {
                                        val ctx = arView.context
                                        blockDistances.forEachIndexed { index, distance ->
                                            // Random angle to spread blocks in all directions
                                            val angle = (0..360).random() * Math.PI.toFloat() / 180f
                                            val x = distance * kotlin.math.cos(angle)
                                            val y = 0f
                                            val z = distance * kotlin.math.sin(angle)

                                            val blockPose = startPose!!.compose(Pose(floatArrayOf(x, y, z), floatArrayOf(0f, 0f, 0f, 1f)))
                                            val anchor = session?.createAnchor(blockPose)
                                            if (anchor != null) {
                                                val anchorNode = AnchorNode(anchor).apply { setParent(arView.scene) }
                                                val node = Node().apply {
                                                    setParent(anchorNode)
                                                    localPosition = Vector3.zero()
                                                }

                                                MaterialFactory.makeOpaqueWithColor(ctx, SColor(android.graphics.Color.RED))
                                                    .thenAccept { material ->
                                                        node.renderable = ShapeFactory.makeCube(
                                                            Vector3(2.0f, 2.0f, 2.0f),
                                                            Vector3.zero(),
                                                            material
                                                        )
                                                        Log.d(TAG, "Block #$index placed at X:${blockPose.tx()} Y:${blockPose.ty()} Z:${blockPose.tz()}")
                                                    }
                                                blocksAnchors.add(anchorNode)
                                            }
                                        }
                                        blocksPlaced.value = true
                                    }
                                }

                                arView
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        if (!isTracking) {
                            Text(
                                text = "Move device slowly to start AR tracking",
                                modifier = Modifier.align(Alignment.TopCenter),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    cameraPermissionState.status.shouldShowRationale -> {
                        Text("Camera permission is needed to display AR view.")
                    }

                    else -> {
                        LaunchedEffect(Unit) { cameraPermissionState.launchPermissionRequest() }
                        Text("Requesting camera permission...")
                    }
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

