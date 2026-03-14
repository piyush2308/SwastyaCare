package com.nitkkr.swastyacare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nitkkr.swastyacare.data.AppDatabase
import com.nitkkr.swastyacare.data.PatientEntity
import kotlinx.coroutines.launch

@Composable
fun ViewPatientsScreen() {

    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val scope = rememberCoroutineScope()

    var patients by remember { mutableStateOf(listOf<PatientEntity>()) }
    var searchQuery by remember { mutableStateOf("") }

    var selectedPatient by remember { mutableStateOf<PatientEntity?>(null) }
    var editMode by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var disease by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        patients = db.patientDao().getAllPatients()
    }

    val filteredPatients = patients.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    Column(modifier = Modifier.padding(16.dp)) {

        Text("Patients", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Patient") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {

            items(filteredPatients) { patient ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {

                    Column(modifier = Modifier.padding(16.dp)) {

                        Text("Name: ${patient.name}")
                        Text("Age: ${patient.age}")
                        Text("Disease: ${patient.disease}")

                        Spacer(modifier = Modifier.height(8.dp))

                        Row {

                            Button(onClick = {

                                selectedPatient = patient
                                name = patient.name
                                age = patient.age.toString()
                                disease = patient.disease
                                editMode = false

                            }) {
                                Text("View")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(onClick = {

                                selectedPatient = patient
                                name = patient.name
                                age = patient.age.toString()
                                disease = patient.disease
                                editMode = true

                            }) {
                                Text("Update")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(onClick = {

                                scope.launch {

                                    db.patientDao().deletePatient(patient)
                                    patients = db.patientDao().getAllPatients()

                                }

                            }) {
                                Text("Delete")
                            }

                        }

                    }

                }

            }

        }

    }

    selectedPatient?.let {

        AlertDialog(

            onDismissRequest = { selectedPatient = null },

            confirmButton = {

                if (editMode) {

                    Button(onClick = {

                        val ageInt = age.toIntOrNull() ?: return@Button

                        scope.launch {

                            val updated = selectedPatient!!.copy(
                                name = name,
                                age = ageInt,
                                disease = disease
                            )

                            db.patientDao().updatePatient(updated)

                            patients = db.patientDao().getAllPatients()

                            selectedPatient = null

                        }

                    }) {
                        Text("Save")
                    }

                }

            },

            dismissButton = {

                Button(onClick = { selectedPatient = null }) {
                    Text("Close")
                }

            },

            title = {
                Text(if (editMode) "Update Patient" else "Patient Details")
            },

            text = {

                Column {

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        enabled = editMode,
                        label = { Text("Name") }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = age,
                        onValueChange = { age = it },
                        enabled = editMode,
                        label = { Text("Age") }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = disease,
                        onValueChange = { disease = it },
                        enabled = editMode,
                        label = { Text("Disease") }
                    )

                }

            }

        )

    }

}