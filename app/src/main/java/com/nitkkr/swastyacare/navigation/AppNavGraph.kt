package com.nitkkr.swastyacare.navigation



import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.nitkkr.swastyacare.ui.screens.HomeScreen
import com.nitkkr.swastyacare.ui.screens.AddPatientScreen

@Composable
fun AppNavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("home") {
            HomeScreen(
                onAddPatientClick = { navController.navigate("add_patient") },
                onViewPatientsClick = { },
                onHealthScanClick = { }
            )
        }

        composable("add_patient") {
            AddPatientScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}