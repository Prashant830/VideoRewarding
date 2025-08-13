package com.example.videorewardingsystem.ui.screens.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.videorewardingsystem.ui.theme.ThemeColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonTopBar(
    title: String,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    )
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