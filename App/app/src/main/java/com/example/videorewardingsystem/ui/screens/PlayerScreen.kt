package com.example.videorewardingsystem.ui.screens

import android.Manifest
import android.graphics.BitmapFactory
import android.graphics.Color
import android.widget.TextView
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
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
import com.google.ar.sceneform.rendering.ViewRenderable
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

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

    Scaffold(
        topBar = {
            CommonTopBar(title = "AR Claim Rewards", onBackClick = onBackClick)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (videoModel == null) {


                val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

            when {
                cameraPermissionState.status.isGranted -> {
                    AndroidView(
                        factory = { ctx ->
                            val arView = ArSceneView(ctx)
                            val session = try { Session(ctx) } catch (_: Exception) { null }
                            session?.let {
                                val config = Config(it)
                                config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
                                config.lightEstimationMode = Config.LightEstimationMode.DISABLED
                                it.configure(config)
                                arView.session = it
                            }

                            lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                                override fun onResume(owner: LifecycleOwner) { arView.resume() }
                                override fun onPause(owner: LifecycleOwner) { arView.pause() }
                                override fun onDestroy(owner: LifecycleOwner) { arView.destroy() }
                            })

                            val cubeTextures = listOf(
                                com.example.videorewardingsystem.R.drawable.uv_cube1,
                                com.example.videorewardingsystem.R.drawable.uv_cube2,
                                com.example.videorewardingsystem.R.drawable.uv_cube3,
                                com.example.videorewardingsystem.R.drawable.uv_cube4, // target
                                com.example.videorewardingsystem.R.drawable.uv_cube5
                            ).shuffled()

                            val targetCubeRes = com.example.videorewardingsystem.R.drawable.uv_cube4

                            val blockShapes = listOf("CUBE", "SPHERE", "CYLINDER", "CUBE", "SPHERE")
                            val blockColors = listOf(
                                com.google.ar.sceneform.rendering.Color(1f, 0f, 0f),
                                com.google.ar.sceneform.rendering.Color(0f, 1f, 0f),
                                com.google.ar.sceneform.rendering.Color(0f, 0f, 1f),
                                com.google.ar.sceneform.rendering.Color(1f, 1f, 0f),
                                com.google.ar.sceneform.rendering.Color(1f, 0f, 1f)
                            )


                            val blockDistances = listOf(1f, 1.5f, 2f, 2.5f, 3f)
                            var blocksPlaced by mutableStateOf(false)
                            var startPose: Pose? = null

                            arView.scene.addOnUpdateListener {
                                val frame = arView.arFrame ?: return@addOnUpdateListener
                                if (frame.camera.trackingState != TrackingState.TRACKING) return@addOnUpdateListener
                                if (startPose == null) startPose = frame.camera.pose

                                if (!blocksPlaced && startPose != null) {
                                    val hits = frame.hitTest(0f, 0f)
                                    if (hits.isNotEmpty()) {
                                        val boardPose = hits[0].createAnchor().pose

                                        blockDistances.forEachIndexed { index, distance ->
                                            val angle = (index * 72f) * Math.PI.toFloat() / 180f
                                            val x = boardPose.tx() + distance * kotlin.math.cos(angle)
                                            val z = boardPose.tz() + distance * kotlin.math.sin(angle)

                                            val cubePose = Pose(
                                                floatArrayOf(x, boardPose.ty(), z),
                                                floatArrayOf(0f, 0f, 0f, 1f)
                                            )

                                            val anchor = session?.createAnchor(cubePose) ?: return@forEachIndexed
                                            val anchorNode = AnchorNode(anchor).apply { setParent(arView.scene) }
                                            val node = Node().apply { setParent(anchorNode) }
                                            val randomShape = blockShapes.random()

                                            // 🔹 API call for prediction
                                            viewModel.runPrediction(index, BitmapFactory.decodeResource(ctx.resources, cubeTextures[index]))

                                            lifecycleOwner.lifecycleScope.launch {
                                                viewModel.predictions.collect { map ->
                                                    map[index]?.let { predictionPercent ->
                                                        (ctx as? android.app.Activity)?.runOnUiThread {

                                                            Texture.builder().setSource(ctx, cubeTextures[index]).build().thenAccept { texture ->
                                                                MaterialFactory.makeOpaqueWithColor(ctx, blockColors[index]).thenAccept { coloredMaterial ->
                                                                    coloredMaterial.setTexture("baseColor", texture)

                                                                    // Apply random shape
                                                                    node.renderable = when (randomShape) {
                                                                        "CUBE" -> ShapeFactory.makeCube(Vector3(0.5f,0.5f,0.5f), Vector3.zero(), coloredMaterial)
                                                                        "SPHERE" -> ShapeFactory.makeSphere(0.25f, Vector3.zero(), coloredMaterial)
                                                                        "CYLINDER" -> ShapeFactory.makeCylinder(0.25f,0.5f, Vector3.zero(), coloredMaterial)
                                                                        else -> ShapeFactory.makeCube(Vector3(0.5f,0.5f,0.5f), Vector3.zero(), coloredMaterial)
                                                                    }

                                                                    // 🔹 Target cube click handling
                                                                    if (cubeTextures[index] == targetCubeRes) {
                                                                        node.setOnTapListener { _, _ ->
                                                                            Toast.makeText(ctx, "🎉 Congratulations! Claimed 🎉", Toast.LENGTH_LONG).show()
                                                                            onBackClick()
                                                                        }
                                                                    }else{
                                                                        node.setOnTapListener { _, _ ->
                                                                            Toast.makeText(ctx, "🎉 it is not correct ", Toast.LENGTH_LONG).show()
                                                                        }
                                                                    }

                                                                    // 🔹 Add prediction text
                                                                    ViewRenderable.builder().setView(ctx, TextView(ctx).apply {
                                                                        text = String.format("%.0f%%", predictionPercent)
                                                                        setTextColor(Color.WHITE)
                                                                        textSize = 20f
                                                                        setPadding(20,10,10,10)
                                                                        setBackgroundColor(Color.BLACK)
                                                                    }).build().thenAccept { textRenderable ->
                                                                        val textNode = Node().apply {
                                                                            setParent(node)
                                                                            localPosition = Vector3(-0.75f, 0f, 0f)
                                                                            renderable = textRenderable
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
                        },
                        modifier = Modifier.fillMaxSize()
                    )
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
