package com.nitkkr.swastyacare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

data class Patient(
    val name: String,
    val age: String,
    val phone: String
)

@Composable
fun AddPatientScreen(
    existingPatient: Patient? = null,
    onSavePatient: (Patient) -> Unit
) {

    var name by remember { mutableStateOf(existingPatient?.name ?: "") }
    var age by remember { mutableStateOf(existingPatient?.age ?: "") }
    var phone by remember { mutableStateOf(existingPatient?.phone ?: "") }

    var nameError by remember { mutableStateOf(false) }
    var ageError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = if (existingPatient == null) "Add Patient" else "Edit Patient",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                nameError = false
            },
            label = { Text("Patient Name") },
            isError = nameError,
            modifier = Modifier.fillMaxWidth()
        )

        if (nameError) {
            Text("Name cannot be empty", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = age,
            onValueChange = {
                age = it
                ageError = false
            },
            label = { Text("Age") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = ageError,
            modifier = Modifier.fillMaxWidth()
        )

        if (ageError) {
            Text("Enter valid age", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = {
                phone = it
                phoneError = false
            },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = phoneError,
            modifier = Modifier.fillMaxWidth()
        )

        if (phoneError) {
            Text("Enter 10 digit phone number", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Button(
                onClick = {

                    nameError = name.isBlank()
                    ageError = age.toIntOrNull() == null
                    phoneError = phone.length != 10

                    if (!nameError && !ageError && !phoneError) {

                        onSavePatient(Patient(name, age, phone))

                        if (existingPatient == null) {
                            name = ""
                            age = ""
                            phone = ""
                        }
                    }
                }
            ) {
                Text(if (existingPatient == null) "Save" else "Update")
            }

            Button(
                onClick = {
                    name = ""
                    age = ""
                    phone = ""
                }
            ) {
                Text("Reset")
            }
        }
    }
}