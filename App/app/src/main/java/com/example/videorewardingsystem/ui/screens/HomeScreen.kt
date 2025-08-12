package com.example.videorewardingsystem.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun HomeScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToPlayer: () -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.Black,
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToProfile,
                    icon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White
                        )
                    },
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
                    icon = {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Player",
                            tint = Color.White
                        )
                    },
                    label = { Text("Player", color = Color.White) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Color.White,
                        unselectedTextColor = Color.White,
                        indicatorColor = Color.DarkGray
                    )
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("Welcome to Video Rewarding System")
        }
    }
}
