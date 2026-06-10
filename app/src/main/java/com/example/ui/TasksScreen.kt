package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun TasksScreen(viewModel: MainViewModel, navController: NavController, showAd: (() -> Unit) -> Unit) {
    val appState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoadingTasks by viewModel.isLoadingTasks.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.checkDailyTasksReset()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = androidx.compose.ui.graphics.Color.Transparent
    ) { paddingValues ->
        if (isLoadingTasks) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("All Tasks", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
                
                item {
                    TaskItem(title = "Watch Ad & Earn", subtitle = "Earn 20 coins per ad", icon = Icons.Default.PlayArrow) {
                        navController.navigate("watch_ad")
                    }
                }
                item {
                    TaskItem(title = "Spin Wheel", subtitle = "Win up to 50 coins", icon = Icons.Default.Refresh) {
                        navController.navigate("spin_wheel")
                    }
                }
                item {
                    TaskItem(title = "Scratch Cards", subtitle = "Scratch and win 5-30 coins", icon = Icons.Default.Star) {
                        navController.navigate("scratch_card")
                    }
                }
                item {
                    TaskItem(title = "Daily Quiz", subtitle = "Answer simple math to win", icon = Icons.Default.CheckCircle) {
                        navController.navigate("quiz")
                    }
                }
                item {
                    TaskItem(title = "Refer a Friend", subtitle = "Earn 50 coins per referral", icon = Icons.Default.Share) {
                        navController.navigate("referral")
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Extra Rewards", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                val currentMask = appState.completedSimpleTasksMask

                // Task 0: Follow Twitter
                if ((currentMask and (1 shl 0)) == 0) {
                    item {
                        TaskItem(title = "Follow us on Twitter", subtitle = "Reward: 10 coins", icon = Icons.Default.ThumbUp) {
                            viewModel.completeSimpleTask(0, 10, showAd) { msg ->
                                scope.launch { snackbarHostState.showSnackbar(msg) }
                            }
                        }
                    }
                }

                // Task 1: Join Telegram
                if ((currentMask and (1 shl 1)) == 0) {
                    item {
                        TaskItem(title = "Join Telegram Channel", subtitle = "Reward: 15 coins", icon = Icons.Default.Group) {
                            viewModel.completeSimpleTask(1, 15, showAd) { msg ->
                                scope.launch { snackbarHostState.showSnackbar(msg) }
                            }
                        }
                    }
                }

                // Task 2: Rate App
                if ((currentMask and (1 shl 2)) == 0) {
                    item {
                        TaskItem(title = "Rate us on Play Store", subtitle = "Reward: 20 coins", icon = Icons.Default.Star) {
                            viewModel.completeSimpleTask(2, 20, showAd) { msg ->
                                scope.launch { snackbarHostState.showSnackbar(msg) }
                            }
                        }
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun TaskItem(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Go", modifier = Modifier.size(16.dp))
        }
    }
}
