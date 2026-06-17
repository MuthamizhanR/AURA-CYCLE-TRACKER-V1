package com.example.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.engine.AuraViewModel
import com.example.ui.screens.AuraHomeScreen
import com.example.ui.screens.ClinicalExportScreen
import com.example.ui.screens.LoggerScreen
import com.example.ui.screens.PartnerDashboardScreen
import com.example.ui.screens.Cycle101Screen
import com.example.ui.screens.SettingsScreen

@Composable
fun AppNavigation(viewModel: AuraViewModel) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            if (uiState.baseline != null) {
                BottomNavigationBar(navController = navController, appMode = uiState.baseline!!.appMode)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (uiState.baseline == null) "onboarding" else "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("onboarding") {
                OnboardingScreen(
                    onComplete = { age, h, w, waist, partner, mode ->
                        viewModel.saveBaseline(age, h, w, waist, partner, mode)
                        navController.navigate("home") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                )
            }
            composable("home") {
                if (uiState.baseline?.appMode == "PARTNER") {
                    PartnerDashboardScreen(uiState = uiState, onSettingsClick = { navController.navigate("settings") })
                } else {
                    AuraHomeScreen(
                        uiState = uiState, 
                        onSettingsClick = { navController.navigate("settings") },
                        onLogClick = { navController.navigate("log") }
                    )
                }
            }
            composable("log") {
                LoggerScreen(
                    uiState = uiState,
                    onLogComplete = { e, c, s, m, bbt, lh ->
                        viewModel.logDaily(e, c, s, m, bbt, lh)
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }
            composable("partner") {
                PartnerDashboardScreen(uiState = uiState, onSettingsClick = { navController.navigate("settings") })
            }
            composable("cycle101") {
                Cycle101Screen()
            }
            composable("export") {
                ClinicalExportScreen(uiState = uiState)
            }
            composable("settings") {
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, appMode: String) {
    val items = mutableListOf<BottomNavItem>()
    
    if (appMode == "PARTNER") {
        items.add(BottomNavItem("Hubby", "home", Icons.Rounded.Favorite))
        items.add(BottomNavItem("Cycle 101", "cycle101", Icons.Rounded.Info))
    } else {
        items.add(BottomNavItem("Home", "home", Icons.Rounded.Home))
        items.add(BottomNavItem("Log", "log", Icons.Rounded.Create))
        if (appMode == "COUPLED") {
            items.add(BottomNavItem("Partner", "partner", Icons.Rounded.Favorite))
        }
        items.add(BottomNavItem("Report", "export", Icons.Rounded.Info))
    }

    NavigationBar(
        containerColor = com.example.ui.theme.PorcelainBg,
        contentColor = com.example.ui.theme.PlumText
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = com.example.ui.theme.PlumText,
                    selectedTextColor = com.example.ui.theme.PlumText,
                    unselectedIconColor = com.example.ui.theme.PlumSoft,
                    unselectedTextColor = com.example.ui.theme.PlumSoft,
                    indicatorColor = com.example.ui.theme.CleanWhite
                )
            )
        }
    }
}

data class BottomNavItem(val title: String, val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
