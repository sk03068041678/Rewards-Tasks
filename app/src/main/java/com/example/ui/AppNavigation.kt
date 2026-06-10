package com.example.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.theme.Amber500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(viewModel: MainViewModel, showAd: (() -> Unit) -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val appState by viewModel.uiState.collectAsStateWithLifecycle()

    val bottomNavRoutes = listOf("home", "tasks", "wallet", "profile")
    val showBottomNav = currentRoute in bottomNavRoutes

    Scaffold(
        topBar = {
            val showTopBar = currentRoute != "login" && currentRoute != "signup"
            if (showTopBar) {
                val titleString = when (currentRoute) {
                    "home" -> "Reward Tasks"
                    "tasks" -> "All Tasks"
                    "wallet" -> "Wallet"
                    "profile" -> "Profile"
                    "watch_ad" -> "Watch Ad"
                    "spin_wheel" -> "Spin & Win"
                    "scratch_card" -> "Scratch Card"
                    "quiz" -> "Quiz"
                    "referral" -> "Referral"
                    else -> "Reward Tasks"
                }

                TopAppBar(
                    title = { Text(titleString, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        if (!showBottomNav) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    },
                    actions = {
                        Chip(coins = appState.coins)
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = com.example.ui.theme.Slate800,
                        actionIconContentColor = com.example.ui.theme.Slate800,
                        navigationIconContentColor = com.example.ui.theme.Slate800
                    )
                )
            }
        },
        bottomBar = {
            if (showBottomNav) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                ) {
                    NavigationBarItem(
                        selected = currentRoute == "home",
                        onClick = { navController.navigate("home") { launchSingleTop = true; restoreState = true; popUpTo("home") { saveState = true } } },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "tasks",
                        onClick = { navController.navigate("tasks") { launchSingleTop = true; restoreState = true; popUpTo("home") { saveState = true } } },
                        icon = { Icon(Icons.Default.List, contentDescription = "Tasks") },
                        label = { Text("Tasks") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "wallet",
                        onClick = { navController.navigate("wallet") { launchSingleTop = true; restoreState = true; popUpTo("home") { saveState = true } } },
                        icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Wallet") },
                        label = { Text("Wallet") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "profile",
                        onClick = { navController.navigate("profile") { launchSingleTop = true; restoreState = true; popUpTo("home") { saveState = true } } },
                        icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile") },
                        label = { Text("Profile") }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = if (com.google.firebase.auth.FirebaseAuth.getInstance().currentUser != null) "home" else "login",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("login") {
                LoginScreen(navController) {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
            composable("signup") {
                SignupScreen(navController) {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
            composable("home") { HomeScreen(viewModel, navController, showAd) }
            composable("tasks") { TasksScreen(viewModel, navController, showAd) }
            composable("wallet") { WalletScreen(viewModel, showAd) }
            composable("profile") { ProfileScreen(viewModel, navController) }
            
            // Nested Task Screens
            composable("watch_ad") { WatchAdScreen(viewModel, navController, showAd) }
            composable("spin_wheel") { SpinWheelScreen(viewModel, navController, showAd) }
            composable("scratch_card") { ScratchCardScreen(viewModel, navController, showAd) }
            composable("quiz") { QuizScreen(viewModel, navController, showAd) }
            composable("referral") { ReferralScreen(viewModel, navController, showAd) }
        }
    }
}

@Composable
fun Chip(coins: Int) {
    Surface(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.Slate100),
        shadowElevation = 2.dp,
        modifier = Modifier.padding(end = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(com.example.ui.theme.Amber400, androidx.compose.foundation.shape.CircleShape),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("$", fontWeight = FontWeight.Black, color = com.example.ui.theme.Amber900, style = MaterialTheme.typography.labelSmall)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = coins.toString(),
                fontWeight = FontWeight.Black,
                color = com.example.ui.theme.Slate800,
            )
        }
    }
}
