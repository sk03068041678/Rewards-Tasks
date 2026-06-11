package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(viewModel: MainViewModel, showAd: (() -> Unit) -> Unit) {
    val appState by viewModel.uiState.collectAsStateWithLifecycle()
    var withdrawAmount by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    
    val usdValue = appState.coins / 1000.0

    var selectedPaymentMethod by remember { mutableStateOf("PayPal") }
    var expanded by remember { mutableStateOf(false) }
    val paymentMethods = listOf("PayPal", "Stripe", "Binance Pay", "Payoneer", "Bank Transfer")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Wallet Balance", style = MaterialTheme.typography.titleLarge)
        Text("${appState.coins} Coins", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold)
        Text("≈ $${String.format("%.2f", usdValue)} USD", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        BalanceLineChart(currentCoins = appState.coins)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Withdraw Funds", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Conversion: 1000 Coins = $1 USD\nMin Withdrawal: 1000 Coins", style = MaterialTheme.typography.bodySmall)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedPaymentMethod,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Payment Method") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        paymentMethods.forEach { method ->
                            DropdownMenuItem(
                                text = { Text(method) },
                                onClick = {
                                    selectedPaymentMethod = method
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = withdrawAmount,
                    onValueChange = { withdrawAmount = it },
                    label = { Text("Amount (Coins)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Account Details / Email (for $selectedPaymentMethod)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        val amount = withdrawAmount.toIntOrNull() ?: 0
                        viewModel.processWithdrawal(
                            amount = amount,
                            showAd = showAd,
                            onSuccess = { 
                                val payoutUsd = String.format("%.2f", amount / 1000.0)
                                message = "Payout of $$payoutUsd requested via $selectedPaymentMethod." 
                            },
                            onError = { message = it }
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Submit Request")
                }
                
                if (message != null) {
                    Text(message!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
    }
}

@Composable
fun BalanceLineChart(currentCoins: Int) {
    val history = listOf(
        maxOf(0, currentCoins - 300),
        maxOf(0, currentCoins - 150),
        maxOf(0, currentCoins - 200),
        maxOf(0, currentCoins - 80),
        maxOf(0, currentCoins - 50),
        maxOf(0, currentCoins - 10),
        currentCoins
    )
    val maxVal = (history.maxOrNull() ?: 1000).toFloat()
    val minVal = (history.minOrNull() ?: 0).toFloat()
    val range = if (maxVal == minVal) 1f else (maxVal - minVal)
    
    val lineColor = MaterialTheme.colorScheme.primary

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("7-Day Trend", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
        
        Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val spacing = size.width / (history.size - 1)
                val heightAvailable = size.height - 16.dp.toPx()
                val topOffset = 8.dp.toPx()
                
                val points = history.mapIndexed { index, value ->
                    val ratio = (value.toFloat() - minVal) / range
                    val x = index * spacing
                    val y = topOffset + heightAvailable - (ratio * heightAvailable)
                    androidx.compose.ui.geometry.Offset(x, y)
                }
                
                val path = androidx.compose.ui.graphics.Path().apply {
                    points.forEachIndexed { index, point ->
                        if (index == 0) moveTo(point.x, point.y)
                        else lineTo(point.x, point.y)
                    }
                }
                
                drawPath(
                    path = path,
                    color = lineColor,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round)
                )
                
                points.forEach { point ->
                    drawCircle(
                        color = lineColor,
                        radius = 5.dp.toPx(),
                        center = point
                    )
                    drawCircle(
                        color = androidx.compose.ui.graphics.Color.White,
                        radius = 2.dp.toPx(),
                        center = point
                    )
                }
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            days.forEach { day ->
                Text(text = day, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

