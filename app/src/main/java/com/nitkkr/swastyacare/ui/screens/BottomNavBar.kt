package com.nitkkr.swastyacare.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nitkkr.swastyacare.navigation.Screen

data class NavItem(val label: String, val icon: String, val route: String)

@Composable
fun BottomNavBar(navController: NavController, currentRoute: String) {
    val items = listOf(
        NavItem("Home",     "🏠", Screen.Home.route),
        NavItem("Patients", "👥", Screen.Patients.route),
        NavItem("Add",      "➕", Screen.AddPatient.route),
        NavItem("Reminder", "🔔", Screen.Reminder.route),
        NavItem("AI Scan",  "🔬", Screen.HealthScan.route),
    )
    NavigationBar(containerColor = Color.White, tonalElevation = 4.dp) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Always pop everything back to Home so back stack stays clean
                            popUpTo(Screen.Home.route) {
                                inclusive = item.route == Screen.Home.route
                                saveState = false
                            }
                            launchSingleTop = true
                            restoreState = false
                        }
                    }
                },
                icon = { Text(item.icon, fontSize = 20.sp) },
                label = { Text(item.label, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = Color(0xFF1976D2),
                    selectedTextColor   = Color(0xFF1976D2),
                    unselectedIconColor = Color(0xFF9E9E9E),
                    unselectedTextColor = Color(0xFF9E9E9E),
                    indicatorColor      = Color(0xFFE3F2FD)
                )
            )
        }
    }
}