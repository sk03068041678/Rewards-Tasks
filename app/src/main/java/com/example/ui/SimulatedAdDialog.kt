package com.example.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay

@Composable
fun SimulatedAdDialog(onComplete: () -> Unit) {
    // 30 seconds for typical rewarded ad, 15 seconds to force interaction
    var timeLeft by remember { mutableStateOf(15) } 
    var playStoreOpened by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    LaunchedEffect(timeLeft, playStoreOpened) {
        if (timeLeft > 0) {
            // Pause at 10 seconds if play store wasn't opened
            if (timeLeft > 10 || playStoreOpened) {
                delay(1000)
                timeLeft--
            }
        }
    }

    Dialog(
        onDismissRequest = { /* Cannot dismiss rewarded ad */ },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Ad content
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.VideogameAsset,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("SPONSORED AD", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                Text("Epic Fantasy Game", color = Color.White, style = MaterialTheme.typography.headlineMedium)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                if (timeLeft <= 10 && !playStoreOpened) {
                    Card(
                        modifier = Modifier.padding(32.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("ACTION REQUIRED", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("You must open the Play Store to continue the ad and receive your reward.", textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    playStoreOpened = true
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.android.chrome"))
                                    try {
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.android.chrome"))
                                        try {
                                            context.startActivity(webIntent)
                                        } catch(ex: Exception) {
                                            // Fallback if no browser
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Open Play Store")
                            }
                        }
                    }
                } else {
                    // Show a fake install button
                    Button(onClick = { /* fake */ }) {
                        Text("Install Now")
                    }
                }
            }
            
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (timeLeft > 0) {
                    Surface(
                        color = Color.DarkGray,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            "Reward in $timeLeft s",
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Button(onClick = onComplete, colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) {
                        Text("Close Ad", color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
