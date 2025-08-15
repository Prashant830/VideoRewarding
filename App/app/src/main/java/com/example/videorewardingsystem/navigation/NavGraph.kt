package com.example.videorewardingsystem.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.videorewardingsystem.networklayer.retrofit.model.VideoModel
import com.example.videorewardingsystem.ui.screens.HomeScreen
import com.example.videorewardingsystem.ui.screens.PlayerScreen
import com.example.videorewardingsystem.ui.screens.ProfileScreen
import com.google.gson.Gson

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Player : Screen("player")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        homeScreen(navController)
        profileScreen(navController)
        playerScreen(navController)
    }
}

// Extension functions to navigate

fun NavHostController.navigateToPlayer(video: VideoModel?) {
    val videoJson = video?.let { Uri.encode(Gson().toJson(it)) }
    val route = if (videoJson != null) {
        "${Screen.Player.route}?video=$videoJson"
    } else {
        Screen.Player.route
    }
    navigate(route)
}


fun NavHostController.navigateToProfile() {
    navigate(Screen.Profile.route)
}

fun NavHostController.navigateToHome() {
    navigate(Screen.Home.route)
}

// Screens

private fun NavGraphBuilder.homeScreen(navController: NavHostController) {
    composable(Screen.Home.route) {
        HomeScreen(
            onNavigateToProfile = { navController.navigateToProfile() },
            onNavigateToPlayer = { video -> navController.navigateToPlayer(video) }
        )
    }
}

private fun NavGraphBuilder.profileScreen(navController: NavHostController) {
    composable(Screen.Profile.route) {
        ProfileScreen(onBackClick = { navController.navigateUp() })
    }
}

private fun NavGraphBuilder.playerScreen(navController: NavHostController) {
    composable(
        route = "${Screen.Player.route}?video={video}",
        arguments = listOf(
            navArgument("video") {
                type = NavType.StringType
                defaultValue = null
                nullable = true
            }
        )
    ) { backStackEntry ->
        val videoJson = backStackEntry.arguments?.getString("video")
        val video: VideoModel? = videoJson?.let {
            try {
                Gson().fromJson(Uri.decode(it), VideoModel::class.java)
            } catch (e: Exception) {
                null // fallback if parsing fails
            }
        }

        PlayerScreen(videoModel = video, onBackClick = { navController.navigateUp() })
    }
}
