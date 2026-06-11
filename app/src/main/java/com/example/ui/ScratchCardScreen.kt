package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScratchCardScreen(viewModel: MainViewModel, navController: NavController, showAd: (() -> Unit) -> Unit) {
    val appState by viewModel.uiState.collectAsStateWithLifecycle()
    var resultMessage by remember { mutableStateOf<String?>(null) }
    
    // Determine active mask
    val now = System.currentTimeMillis()
    val isNewDay = !isSameDay(appState.scratchedCardsDateMillis, now)
    val activeMask = if (isNewDay) 0 else appState.scratchedCardsMask

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scratch Cards") },
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Scratch 6 cards daily to win coins!", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            if (resultMessage != null) {
                Text(resultMessage!!, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(6) { index ->
                    val isScratched = (activeMask and (1 shl index)) != 0
                    
                    Card(
                        modifier = Modifier
                            .height(120.dp)
                            .clickable(enabled = !isScratched) {
                                viewModel.scratchCard(
                                    cardIndex = index,
                                    showAd = showAd,
                                    onResult = { won -> resultMessage = "Scratched $won coins!" },
                                    onError = { err -> resultMessage = err }
                                )
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isScratched) Color.LightGray else MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            if (isScratched) {
                                Text("Scratched", color = Color.DarkGray)
                            } else {
                                Text("Tap to Scratch", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun isSameDay(time1: Long, time2: Long): Boolean {
    if (time1 == 0L) return false
    val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
           cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
