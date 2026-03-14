package com.nitkkr.swastyacare.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nitkkr.swastyacare.ui.screens.*

sealed class Screen(val route: String) {
    object Login      : Screen("login")
    object Home       : Screen("home")
    object Patients   : Screen("patients")
    object AddPatient : Screen("add_patient")
    object Reminder   : Screen("reminder")
    object HealthScan : Screen("health_scan")
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route)      { LoginScreen(navController) }
        composable(Screen.Home.route)       { HomeScreen(navController) }
        composable(Screen.Patients.route)   { PatientsScreen(navController) }
        composable(Screen.AddPatient.route) { AddPatientScreen(navController) }
        composable(Screen.Reminder.route)   { ReminderScreen(navController) }
        composable(Screen.HealthScan.route) { HealthScanScreen(navController) }
    }
}