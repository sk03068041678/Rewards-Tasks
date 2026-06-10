package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.ui.theme.Amber500
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpinWheelScreen(viewModel: MainViewModel, navController: NavController, showAd: (() -> Unit) -> Unit) {
    val appState by viewModel.uiState.collectAsStateWithLifecycle()
    var isSpinning by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    
    val rewards = listOf(5, 10, 15, 20, 25, 30, 40, 50)
    var rotationAngle by remember { mutableStateOf(0f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Spin Wheel") },
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
            
            // Simple visual placeholder for the wheel
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(250.dp)) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    rotate(rotationAngle) {
                        drawCircle(color = Amber500, radius = size.minDimension / 2)
                        // This is a placeholder visual; actual wheel requires more drawing logic
                        drawCircle(color = Color.White, radius = size.minDimension / 2.5f)
                    }
                }
                Text("SPIN", style = MaterialTheme.typography.titleLarge)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            if (resultMessage != null) {
                Text(resultMessage!!, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            Button(
                onClick = {
                    isSpinning = true
                    resultMessage = null
                    viewModel.spinWheel(
                        showAd = showAd,
                        onResult = { won ->
                            rotationAngle += 1080f // fake spin visual
                            isSpinning = false
                            resultMessage = "You won $won coins!"
                        },
                        onError = { err ->
                            isSpinning = false
                            resultMessage = err
                        }
                    )
                },
                enabled = !isSpinning,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(if (isSpinning) "Spinning..." else "Spin Now")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text("Extra Spins available: ${appState.extraSpinsAvailable}")
            
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = {
                    isSpinning = true
                    viewModel.watchAdForExtraSpin(
                        showAd = showAd,
                        onComplete = {
                            isSpinning = false
                            resultMessage = "Extra spin earned!"
                        }
                    )
                },
                enabled = !isSpinning
            ) {
                Text("Watch Ad for Extra Spin")
            }
        }
    }
}
