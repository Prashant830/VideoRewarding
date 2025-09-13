package com.example.videorewardingsystem.ui.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.videorewardingsystem.R
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
//        NavigationBarItem(
//            selected = false,
//            onClick = onNavigateToPlayer,
//            icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Player", tint = Color.White) },
//            label = { Text("Player", color = Color.White) },
//            colors = NavigationBarItemDefaults.colors(
//                unselectedIconColor = Color.White,
//                unselectedTextColor = Color.White,
//                indicatorColor = Color.DarkGray
//            )
//        )
    }
}

@Composable
fun HomeTopBar() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.eth),
            contentDescription = "Ethereum Logo",
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "VIDEO REWARDING SYSTEM",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF00E676)
        )
    }
}

@Composable
fun ErrorMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Error: $message",
            color = Color.Red,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
