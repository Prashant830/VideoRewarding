package com.example.videorewardingsystem.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.videorewardingsystem.ui.screens.components.CommonTopBar
import com.example.videorewardingsystem.ui.viewmodels.ProfileViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Profile",
                onBackClick = onBackClick
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.userName,
                onValueChange = { viewModel.updateUserName(it) },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.updateEmail(it) },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.metamaskWallet,
                onValueChange = { viewModel.updateWalletAddress(it) },
                label = { Text("Metamask Wallet Address") },
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.metamaskWallet.isNotEmpty()) {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(viewModel.getWalletBalanceUrl())
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Check Wallet Balance")
                }
            }

            Button(
                onClick = { viewModel.updateUserInfo() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update Information")
            }

            if (uiState.isUpdateSuccess) {
                Text(
                    text = "Information updated successfully!",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
