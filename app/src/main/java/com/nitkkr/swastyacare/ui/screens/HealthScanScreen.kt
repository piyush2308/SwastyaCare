package com.nitkkr.swastyacare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HealthScanScreen() {

    var result by remember { mutableStateOf("No scan yet") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "AI Health Scan",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(onClick = {

            // Fake AI prediction for demo
            val predictions = listOf(
                "Healthy",
                "Possible Fever",
                "Possible Skin Infection",
                "Possible Allergy"
            )

            result = predictions.random()

        }) {

            Text("Start Scan")

        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Result:",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = result,
            style = MaterialTheme.typography.bodyLarge
        )

    }

}