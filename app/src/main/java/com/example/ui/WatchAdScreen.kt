package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchAdScreen(viewModel: MainViewModel, navController: NavController, showAd: (() -> Unit) -> Unit) {
    var isWatching by remember { mutableStateOf(false) }
    var rewardEarned by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Watch Ad & Earn") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (rewardEarned) {
                Text("Congratulations!", style = MaterialTheme.typography.headlineMedium)
                Text("You earned 20 Coins", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text("Go Back")
                }
            } else if (isWatching) {
                CircularProgressIndicator(modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Watching Ad... Please wait")
            } else {
                Text("Watch a short ad to earn 20 coins.", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        isWatching = true
                        viewModel.watchAdForCoins(
                            showAd = showAd,
                            onComplete = { 
                                isWatching = false
                                rewardEarned = true
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Watch Ad Now")
                }
            }
        }
    }
}
