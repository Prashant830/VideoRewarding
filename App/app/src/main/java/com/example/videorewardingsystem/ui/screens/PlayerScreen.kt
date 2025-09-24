package com.example.videorewardingsystem.ui.screens

import android.Manifest
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.videorewardingsystem.networklayer.retrofit.model.VideoModel
import com.example.videorewardingsystem.ui.screens.components.CommonTopBar
import com.example.videorewardingsystem.ui.viewmodels.HomeViewModel
import com.example.videorewardingsystem.ui.viewmodels.PlayerViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.MaterialFactory
import com.google.ar.sceneform.rendering.ShapeFactory
import com.google.ar.sceneform.rendering.Texture
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// ------------------- AI Coach -------------------
class AiCoach(
    val predictions: Map<Int, Float>,
    val blockPositions: Map<Int, Pose>
) {
    val targetIndex = predictions.maxByOrNull { it.value }?.key ?: 0

    fun getResponse(selectedIndex: Int, userPose: Pose): String {
        return if (selectedIndex == targetIndex) {
            "🎉 Congrats! Claim your reward!"
        } else {
            val pred = predictions[selectedIndex] ?: 0f
            val hint = when (pred) {
                in 90f..95f -> "🔥Looks right but not Eth."
                in 75f..90f -> "😊 Very promising!"
                in 50f..75f -> "🤔 Maybe…"
                in 25f..50f -> "❄️ Cold. Probably not."
                else -> "🙃 Definitely not."
            }

            val targetPose = blockPositions[targetIndex]
            val suggestion = targetPose?.let {
                val dx = it.tx() - userPose.tx()
                val dz = it.tz() - userPose.tz()
                val distance = sqrt(dx * dx + dz * dz)

                val direction = when {
                    dx > 0.3 -> "to your Right"
                    dx < -0.3 -> "to your Left"
                    dz > 0.3 -> "in Front"
                    dz < -0.3 -> "Behind you"
                    else -> "Nearby"
                }

                if (distance <= 1f) {
                    "👉 Target Block (#$targetIndex) is very close! (${String.format("%.2f", distance)}m, $direction)"
                } else {
                    "👉 Target Block (#$targetIndex) is ${String.format("%.2f", distance)}m $direction."
                }
            } ?: "🔍 Target block not found!"

            "❌ Wrong block. $hint\n$suggestion"
        }
    }
}

// ------------------- Player Screen -------------------
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

    val blockPositions = remember { mutableStateMapOf<Int, Pose>() }
    val nearestBlockInfo = remember { mutableStateOf("Scanning for nearest block...") }

    val showAiPopup = remember { mutableStateOf(false) }
    val aiMessage = remember { mutableStateOf("") }
    val userPoseState = remember { mutableStateOf<Pose?>(null) }
    val targetIndexState = remember { mutableStateOf(0) }

    Scaffold(
        topBar = { CommonTopBar(title = "AR Claim Rewards", onBackClick = onBackClick) }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {

            if (videoModel == null) {
                val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

                when {
                    cameraPermissionState.status.isGranted -> {
                        AndroidView(factory = { ctx ->
                            val arView = ArSceneView(ctx)
                            val session = try { Session(ctx) } catch (_: Exception) { null }
                            session?.let {
                                val config = Config(it)
                                config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
                                config.lightEstimationMode = Config.LightEstimationMode.DISABLED
                                it.configure(config)
                                arView.session = it
                                // Keep 3D blocks visible at very close distances by reducing the near clip plane
                                arView.scene.camera.nearClipPlane = 0.01f
                                arView.scene.camera.farClipPlane = 100f
                            }

                            lifecycleOwner.lifecycle.addObserver(object :
                                DefaultLifecycleObserver {
                                override fun onResume(owner: LifecycleOwner) { arView.resume() }
                                override fun onPause(owner: LifecycleOwner) { arView.pause() }
                                override fun onDestroy(owner: LifecycleOwner) { arView.destroy() }
                            })

                            val cubeTextures = listOf(
                                com.example.videorewardingsystem.R.drawable.uv_cube1,
                                com.example.videorewardingsystem.R.drawable.uv_cube2,
                                com.example.videorewardingsystem.R.drawable.uv_cube3,
                                com.example.videorewardingsystem.R.drawable.uv_cube4,
                                com.example.videorewardingsystem.R.drawable.uv_cube5
                            ).shuffled()

                            val blockShapes = listOf("CUBE", "SPHERE", "CYLINDER", "CUBE", "SPHERE")
                            val blockColors = listOf(
                                com.google.ar.sceneform.rendering.Color(1f, 0f, 0f),
                                com.google.ar.sceneform.rendering.Color(0f, 1f, 0f),
                                com.google.ar.sceneform.rendering.Color(0f, 0f, 1f),
                                com.google.ar.sceneform.rendering.Color(1f, 1f, 0f),
                                com.google.ar.sceneform.rendering.Color(1f, 0f, 1f)
                            )

                            var blocksPlaced by mutableStateOf(false)
                            var startPose: Pose? = null

                            arView.scene.addOnUpdateListener {
                                val frame = arView.arFrame ?: return@addOnUpdateListener
                                if (frame.camera.trackingState != TrackingState.TRACKING) return@addOnUpdateListener
                                if (startPose == null) startPose = frame.camera.pose

                                val userPose = frame.camera.pose
                                userPoseState.value = userPose

                                // Update nearest block info
                                if (blockPositions.isNotEmpty()) {
                                    var nearestIndex = 0
                                    var minDistance = Float.MAX_VALUE
                                    var direction = ""
                                    blockPositions.forEach { (index, pose) ->
                                        val dx = pose.tx() - userPose.tx()
                                        val dz = pose.tz() - userPose.tz()
                                        val distance = sqrt(dx * dx + dz * dz)
                                        if (distance < minDistance) {
                                            minDistance = distance
                                            nearestIndex = index
                                            direction = when {
                                                dx > 2 -> "Right"
                                                dx < -2 -> "Left"
                                                dz > 2 -> "Front"
                                                dz < -2 -> "Back"
                                                else -> "Nearby"
                                            }
                                        }
                                    }
                                    nearestBlockInfo.value = "Nearest Block: #$nearestIndex | ${"%.1f".format(minDistance)}m | $direction"
                                }

                                // Place blocks once
                                if (!blocksPlaced && startPose != null) {
                                    val hits = frame.hitTest(0f, 0f)
                                    if (hits.isNotEmpty()) {
                                        val boardPose = hits[0].createAnchor().pose
                                        repeat(5) { index ->
                                            val randomRadius = (2..5).random().toFloat()
                                            val randomAngle = Math.toRadians((0..360).random().toDouble())
                                            val x = boardPose.tx() + randomRadius * cos(randomAngle).toFloat()
                                            val z = boardPose.tz() + randomRadius * sin(randomAngle).toFloat()
                                            val cubePose = Pose(floatArrayOf(x, boardPose.ty(), z), floatArrayOf(0f, 0f, 0f, 1f))
                                            blockPositions[index] = cubePose

                                            val anchor = session?.createAnchor(cubePose) ?: return@repeat
                                            val anchorNode = AnchorNode(anchor).apply { setParent(arView.scene) }
                                            val node = Node().apply { setParent(anchorNode) }
                                            val randomShape = blockShapes.random()

                                            // Run AI prediction
                                            viewModel.runPrediction(
                                                index,
                                                BitmapFactory.decodeResource(ctx.resources, cubeTextures[index])
                                            )

                                            lifecycleOwner.lifecycleScope.launch {
                                                viewModel.predictions.collect { map ->
                                                    map[index]?.let { _ ->
                                                        (ctx as? ComponentActivity)?.runOnUiThread {
                                                            Texture.builder().setSource(ctx, cubeTextures[index])
                                                                .build().thenAccept { texture ->
                                                                    MaterialFactory.makeOpaqueWithColor(ctx, blockColors[index])
                                                                        .thenAccept { material ->
                                                                            material.setTexture("baseColor", texture)
                                                                            node.renderable = when (randomShape) {
                                                                                "CUBE" -> ShapeFactory.makeCube(Vector3(0.5f, 0.5f, 0.5f), Vector3.zero(), material)
                                                                                "SPHERE" -> ShapeFactory.makeSphere(0.25f, Vector3.zero(), material)
                                                                                "CYLINDER" -> ShapeFactory.makeCylinder(0.25f, 0.5f, Vector3.zero(), material)
                                                                                else -> ShapeFactory.makeCube(Vector3(0.5f, 0.5f, 0.5f), Vector3.zero(), material)
                                                                            }

                                                                            node.setOnTapListener { _, _ ->
                                                                                val aiCoach = AiCoach(map, blockPositions)
                                                                                targetIndexState.value = aiCoach.targetIndex

                                                                                val currentFrame = arView.arFrame
                                                                                val userPose = currentFrame?.camera?.pose ?: return@setOnTapListener
                                                                                val blockPose = anchor.pose
                                                                                val dx = userPose.tx() - blockPose.tx()
                                                                                val dy = userPose.ty() - blockPose.ty()
                                                                                val dz = userPose.tz() - blockPose.tz()
                                                                                val distance = sqrt(dx * dx + dy * dy + dz * dz)

                                                                                val response: String = if (distance <= 1.3f) {
                                                                                    aiCoach.getResponse(index, userPose)
                                                                                } else {
                                                                                    "⛔ Too far! 📏 You are ${"%.2f".format(distance)}m away.\nMove with in 1.3 meter to claim rewards."
                                                                                }

                                                                                (ctx as? ComponentActivity)?.runOnUiThread {
                                                                                    aiMessage.value = response
                                                                                    showAiPopup.value = true
                                                                                }
                                                                            }
                                                                        }
                                                                }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        blocksPlaced = true
                                    }
                                }
                            }

                            arView
                        }, modifier = Modifier.fillMaxSize()
                        )

                        Card(
                            modifier = Modifier.align(Alignment.TopCenter)  // now it works correctly
                                .padding(12.dp),
                        ) {
                            // Overlay Mini-map radar
                            userPoseState.value?.let { pose ->
                                MiniMapRadar(
                                    userPose = pose,
                                    blockPositions = blockPositions,
                                    targetIndex = targetIndexState.value,
                                    nearestBlockInfo = nearestBlockInfo.value
                                )
                            }
                        }
                    }

                    cameraPermissionState.status.shouldShowRationale -> Text("Camera permission is needed for AR view.")
                    else -> {
                        LaunchedEffect(Unit) { cameraPermissionState.launchPermissionRequest() }
                        Text("Requesting camera permission...")
                    }
                }
            } else {
                // YouTube Video Player
                val videoId = videoModel.videoUrl.let { url ->
                    when {
                        url.contains("shorts/") -> url.substringAfter("shorts/").substringBefore("?")
                        url.contains("watch?v=") -> url.substringAfter("watch?v=").substringBefore("&")
                        else -> url
                    }
                }

                AndroidView(factory = { context ->
                    YouTubePlayerView(context).apply {
                        lifecycleOwner.lifecycle.addObserver(this)
                        addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                            override fun onReady(youTubePlayer: YouTubePlayer) {
                                youTubePlayer.loadVideo(videoId, 0f)
                            }

                            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                                val totalRuntimeSeconds = videoModel.totalRuntime
                                homeViewModel.saveOrUpdateVideoProgress(
                                    videoModel.videoId,
                                    videoModel.videoUrl,
                                    if (videoModel.currentWatched >= totalRuntimeSeconds) totalRuntimeSeconds else second.toLong(),
                                    videoModel.totalRuntime
                                )
                            }
                        })
                        post { toggleFullScreen() }
                    }
                }, modifier = Modifier.fillMaxSize())
            }

            // AI Popup
            if (showAiPopup.value) {
                AiPopup(aiMessage.value)
                LaunchedEffect(aiMessage.value) {
                    kotlinx.coroutines.delay(1500)
                    showAiPopup.value = false
                }
            }
        }
    }
}

// ------------------- AI Popup -------------------
@Composable
fun AiPopup(message: String) {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.9f)),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "🤖 AI Assistant", style = MaterialTheme.typography.titleMedium, color = androidx.compose.ui.graphics.Color.Cyan)
                Spacer(Modifier.height(8.dp))
                Text(text = message, style = MaterialTheme.typography.bodyLarge, color = androidx.compose.ui.graphics.Color.White)
            }
        }
    }
}

// ------------------- Mini-map Radar -------------------
@Composable
fun MiniMapRadar(
    userPose: Pose,
    blockPositions: Map<Int, Pose>,
    targetIndex: Int,
    nearestBlockInfo: String
) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // 🔹 Top Label Box
            Card(
                modifier = Modifier.padding(10.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "🎯 Eth Treasure Hunt",
                    style = MaterialTheme.typography.titleSmall,
                    color = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))

                // 🔹 Nearest block info
                Text(
                    text = nearestBlockInfo,
                    style = MaterialTheme.typography.bodySmall,
                    color = androidx.compose.ui.graphics.Color.White
                )
            }

            // 🔹 Radar Canvas
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val centerX = size.width / 2
                    val centerY = size.height / 2
                    val radarRadius = size.minDimension / 2 * 0.9f

                    drawCircle(
                        color = androidx.compose.ui.graphics.Color.Gray.copy(alpha = 1f),
                        radius = radarRadius,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                    )

                    drawCircle(color = androidx.compose.ui.graphics.Color.Cyan, radius = 10f)

                    blockPositions.forEach { (index, pose) ->
                        val dx = pose.tx() - userPose.tx()
                        val dz = pose.tz() - userPose.tz()
                        val maxDisplayDistance = 5f

                        val distance = sqrt(dx * dx + dz * dz)
                        val angle = kotlin.math.atan2(-dz, dx)
                        val clampedDistance = distance.coerceAtMost(maxDisplayDistance)
                        val scaledDistance = (clampedDistance / maxDisplayDistance) * radarRadius
                        val scaledX = cos(angle) * scaledDistance
                        val scaledY = sin(angle) * scaledDistance

                        val color = androidx.compose.ui.graphics.Color.Red

                        drawCircle(
                            color = color,
                            radius = 11f,
                            center = androidx.compose.ui.geometry.Offset(centerX + scaledX, centerY + scaledY)
                        )
                    }
                }
            }
        }
}



