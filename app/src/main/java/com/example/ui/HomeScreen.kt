package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.ui.theme.Amber500

@Composable
fun HomeScreen(viewModel: MainViewModel, navController: NavController, showAd: (() -> Unit) -> Unit) {
    val appState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoadingTasks by viewModel.isLoadingTasks.collectAsStateWithLifecycle()
    var message by remember { mutableStateOf<String?>(null) }

    if (isLoadingTasks) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                Text(
                    text = "WELCOME BACK,",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = com.example.ui.theme.Slate500
                )
                Text(
                    text = appState.username,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = com.example.ui.theme.Slate800
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(
                                com.example.ui.theme.Purple600,
                                com.example.ui.theme.Indigo600,
                                com.example.ui.theme.Blue700
                            )
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = "EARN COINS DAILY",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = Color.White
                    )
                    Text(
                        text = "Complete simple tasks, get real rewards.",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = com.example.ui.theme.Indigo100,
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )
                    Button(
                        onClick = {
                            viewModel.claimDailyLogin(
                                showAd = showAd,
                                onSuccess = { message = "Claimed $it coins!" },
                                onError = { message = it }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = com.example.ui.theme.Indigo700
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(50)
                    ) {
                        Text("DAILY LOGIN +10", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            if (message != null) {
                Text(
                    text = message!!,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Ad Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.LightGray, MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Text("Banner Ad Placeholder", color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quick Tasks",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = com.example.ui.theme.Slate800
                )
                Text(
                    text = "SEE ALL",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = com.example.ui.theme.Indigo600
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                QuickTaskCard(
                    title = "Watch & Earn", 
                    subtitle = "+20 COINS",
                    icon = Icons.Default.PlayArrow, 
                    containerColor = com.example.ui.theme.Rose50,
                    iconBgColor = com.example.ui.theme.Rose500,
                    subtitleColor = com.example.ui.theme.Rose600,
                    modifier = Modifier.weight(1f)
                ) {
                    navController.navigate("watch_ad")
                }
                QuickTaskCard(
                    title = "Spin Wheel", 
                    subtitle = "WIN UP TO 50",
                    icon = Icons.Default.Refresh, 
                    containerColor = com.example.ui.theme.AmberBg50,
                    iconBgColor = com.example.ui.theme.Amber500,
                    subtitleColor = com.example.ui.theme.Amber900,
                    modifier = Modifier.weight(1f)
                ) {
                    navController.navigate("spin_wheel")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                QuickTaskCard(
                    title = "Daily Quiz", 
                    subtitle = "+15 PER ANSWER",
                    icon = Icons.Default.CheckCircle, 
                    containerColor = com.example.ui.theme.Emerald50,
                    iconBgColor = com.example.ui.theme.Emerald500,
                    subtitleColor = com.example.ui.theme.Emerald700,
                    modifier = Modifier.weight(1f)
                ) {
                    navController.navigate("quiz")
                }
                QuickTaskCard(
                    title = "Scratch Card", 
                    subtitle = "6 LEFT DAILY",
                    icon = Icons.Default.Star, 
                    containerColor = com.example.ui.theme.Blue50,
                    iconBgColor = com.example.ui.theme.Blue500,
                    subtitleColor = com.example.ui.theme.Blue700,
                    modifier = Modifier.weight(1f)
                ) {
                    navController.navigate("scratch_card")
                }
            }
        }
    }
}

@Composable
fun QuickTaskCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color,
    iconBgColor: Color,
    subtitleColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(130.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconBgColor, androidx.compose.foundation.shape.RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = Color.White)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.Bold, color = com.example.ui.theme.Slate800, style = MaterialTheme.typography.bodyMedium)
            Text(subtitle, fontWeight = FontWeight.Bold, color = subtitleColor, style = MaterialTheme.typography.labelSmall)
        }
    }
}
