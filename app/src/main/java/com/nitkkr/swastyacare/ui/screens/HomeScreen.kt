package com.nitkkr.swastyacare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onAddPatientClick: () -> Unit,
    onViewPatientsClick: () -> Unit,
    onHealthScanClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "SwastyaCare 🏥",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(30.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Text(
                    text = "Patient Management",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(15.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onAddPatientClick
                ) {
                    Text("Add Patient")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onViewPatientsClick
                ) {
                    Text("View Patients")
                }

            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(modifier = Modifier.padding(20.dp)) {

                Text(
                    text = "AI Health Tools",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(15.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onHealthScanClick
                ) {
                    Text("AI Health Scan")
                }

            }

        }

    }

}