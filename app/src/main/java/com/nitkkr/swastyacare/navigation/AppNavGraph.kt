package com.nitkkr.swastyacare.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.nitkkr.swastyacare.ui.screens.*

@Composable
fun AppNavGraph() {

    val navController = rememberNavController()
    val patientList = remember { mutableStateListOf<Patient>() }

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("home") {
            HomeScreen(
                onAddPatientClick = { navController.navigate("addPatient") },
                onViewPatientsClick = { navController.navigate("viewPatients") },
                onHealthScanClick = {}
            )
        }

        composable("addPatient") {
            AddPatientScreen(
                onSavePatient = { patient ->
                    patientList.add(patient)
                    navController.popBackStack()
                }
            )
        }

        composable("viewPatients") {
            ViewPatientsScreen(
                patients = patientList,
                onDeletePatient = { patient ->
                    patientList.remove(patient)
                },
                onEditPatient = { patient ->
                    navController.navigate(
                        "edit/${patient.name}/${patient.age}/${patient.phone}"
                    )
                }
            )
        }

        composable(
            route = "edit/{name}/{age}/{phone}"
        ) { backStackEntry ->

            val name = backStackEntry.arguments?.getString("name") ?: ""
            val age = backStackEntry.arguments?.getString("age") ?: ""
            val phone = backStackEntry.arguments?.getString("phone") ?: ""

            AddPatientScreen(
                existingPatient = Patient(name, age, phone),
                onSavePatient = { updatedPatient ->
                    patientList.removeIf {
                        it.name == name && it.phone == phone
                    }
                    patientList.add(updatedPatient)
                    navController.popBackStack()
                }
            )
        }
    }
}