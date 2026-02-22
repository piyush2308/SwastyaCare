package com.nitkkr.swastyacare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ViewPatientsScreen(
    patients: List<Patient>,
    onDeletePatient: (Patient) -> Unit,
    onEditPatient: (Patient) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Patient List", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        if (patients.isEmpty()) {
            Text("No patients added yet.")
        }

        LazyColumn {
            items(patients) { patient ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {

                    Column(modifier = Modifier.padding(16.dp)) {

                        Text("Name: ${patient.name}")
                        Text("Age: ${patient.age}")
                        Text("Phone: ${patient.phone}")

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Button(onClick = { onEditPatient(patient) }) {
                                Text("Edit")
                            }

                            Button(
                                onClick = { onDeletePatient(patient) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    }
}


