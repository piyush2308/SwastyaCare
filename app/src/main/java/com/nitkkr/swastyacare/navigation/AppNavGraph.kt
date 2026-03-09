package com.nitkkr.swastyacare.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*

import com.nitkkr.swastyacare.ui.screens.AddPatientScreen
import com.nitkkr.swastyacare.ui.screens.HomeScreen
import com.nitkkr.swastyacare.ui.screens.ViewPatientsScreen
import com.nitkkr.swastyacare.ui.screens.ReminderScreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Text
import com.nitkkr.swastyacare.ui.screens.HealthScanScreen
@Composable
fun AppNavGraph() {

    val navController = rememberNavController()

    Scaffold(

        bottomBar = {

            NavigationBar {

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("home") },
                    label = { Text("Home") },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("add") },
                    label = { Text("Add") },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("view") },
                    label = { Text("Patients") },
                    icon = { Icon(Icons.Default.List, contentDescription = null) }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("reminder") },
                    label = { Text("Reminder") },
                    icon = { Icon(Icons.Default.Notifications, contentDescription = null) }
                )

            }

        }

    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {

            composable(route = "home") {
                HomeScreen(
                    onAddPatientClick = { navController.navigate("add") },
                    onViewPatientsClick = { navController.navigate("view") },
                    onHealthScanClick = { navController.navigate("scan") }
                )
            }

            composable("add") {
                AddPatientScreen()
            }

            composable("view") {
                ViewPatientsScreen()
            }

            composable("reminder") {
                ReminderScreen()
            }

            composable("scan") {
                HealthScanScreen()
            }
        }

    }

}