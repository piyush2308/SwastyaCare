package com.nitkkr.swastyacare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nitkkr.swastyacare.data.AppDatabase
import com.nitkkr.swastyacare.data.PatientEntity
import com.nitkkr.swastyacare.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun AddPatientScreen(navController: NavController) {
    val context = LocalContext.current
    val db      = AppDatabase.getDatabase(context)
    val scope   = rememberCoroutineScope()

    var name    by remember { mutableStateOf("") }
    var age     by remember { mutableStateOf("") }
    var disease by remember { mutableStateOf("") }
    var success by remember { mutableStateOf(false) }
    var error   by remember { mutableStateOf("") }

    Scaffold(bottomBar = { BottomNavBar(navController, Screen.AddPatient.route) }) { pad ->
        Column(
            Modifier.fillMaxSize().padding(pad).background(Color(0xFFF5F7FF))
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Box(
                Modifier.fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(Color(0xFF1565C0), Color(0xFF42A5F5))))
                    .statusBarsPadding()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(44.dp).clip(CircleShape).background(Color.White.copy(0.25f)),
                        contentAlignment = Alignment.Center
                    ) { Text("👤", fontSize = 20.sp) }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Add New Patient", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Fill in patient details below", fontSize = 13.sp, color = Color.White.copy(.80f))
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Form card
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Patient Information", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1976D2))
                    Text("All fields are required", fontSize = 12.sp, color = Color(0xFF9E9E9E))
                    Spacer(Modifier.height(20.dp))

                    FormField(
                        value = name, onValueChange = { name = it; error = "" },
                        label = "Patient Name", icon = "👤",
                        keyboardType = KeyboardType.Text
                    )
                    Spacer(Modifier.height(14.dp))
                    FormField(
                        value = age, onValueChange = { age = it; error = "" },
                        label = "Age", icon = "📅",
                        keyboardType = KeyboardType.Number
                    )
                    Spacer(Modifier.height(14.dp))
                    FormField(
                        value = disease, onValueChange = { disease = it; error = "" },
                        label = "Disease / Condition", icon = "❤️",
                        keyboardType = KeyboardType.Text
                    )

                    if (error.isNotEmpty()) {
                        Spacer(Modifier.height(10.dp))
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            shape = RoundedCornerShape(8.dp)) {
                            Text(error, Modifier.padding(10.dp), color = Color(0xFFC62828), fontSize = 13.sp)
                        }
                    }
                    if (success) {
                        Spacer(Modifier.height(10.dp))
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                            shape = RoundedCornerShape(8.dp)) {
                            Text("✅ Patient saved successfully!", Modifier.padding(10.dp),
                                color = Color(0xFF2E7D32), fontSize = 13.sp)
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = {
                            if (name.isBlank() || age.isBlank() || disease.isBlank()) {
                                error = "Please fill all fields"; return@Button
                            }
                            val ageInt = age.toIntOrNull()
                            if (ageInt == null || ageInt <= 0) {
                                error = "Please enter a valid age"; return@Button
                            }
                            scope.launch {
                                db.patientDao().insertPatient(PatientEntity(name = name.trim(), age = ageInt, disease = disease.trim()))
                                success = true
                                name = ""; age = ""; disease = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                    ) {
                        Text("💾  Save Patient", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.height(10.dp))
                    TextButton(
                        onClick = { name = ""; age = ""; disease = ""; error = ""; success = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Clear Form", fontSize = 14.sp, color = Color(0xFF9E9E9E))
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FormField(value: String, onValueChange: (String) -> Unit, label: String, icon: String, keyboardType: KeyboardType) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Text(icon, fontSize = 18.sp) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF1976D2),
            unfocusedBorderColor = Color(0xFFE0E0E0)
        )
    )
}