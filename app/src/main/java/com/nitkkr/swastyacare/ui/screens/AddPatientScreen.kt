package com.nitkkr.swastyacare.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nitkkr.swastyacare.data.AppDatabase
import com.nitkkr.swastyacare.data.PatientEntity
import kotlinx.coroutines.launch

@Composable
//Add Patient
fun AddPatientScreen() {

    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var disease by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(
                    "Add Patient",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Patient Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = disease,
                    onValueChange = { disease = it },
                    label = { Text("Disease") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {

                        val ageInt = age.toIntOrNull()

                        if (name.isBlank() || ageInt == null || disease.isBlank()) {
                            Toast.makeText(
                                context,
                                "Enter valid patient details",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        scope.launch {

                            db.patientDao().insertPatient(
                                PatientEntity(
                                    name = name,
                                    age = ageInt,
                                    disease = disease
                                )
                            )

                            Toast.makeText(
                                context,
                                "Patient Saved",
                                Toast.LENGTH_SHORT
                            ).show()

                            name = ""
                            age = ""
                            disease = ""

                        }
                    }
                ) {
                    Text("Save Patient")
                }

            }

        }

    }
}