package com.nitkkr.swastyacare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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

val avatarColors = listOf(
    Color(0xFF1976D2), Color(0xFF388E3C), Color(0xFFF57C00),
    Color(0xFF7B1FA2), Color(0xFFD32F2F), Color(0xFF0097A7)
)

@Composable
fun PatientsScreen(navController: NavController) {
    val context = LocalContext.current
    val db      = AppDatabase.getDatabase(context)
    val scope   = rememberCoroutineScope()

    var patients by remember { mutableStateOf<List<PatientEntity>>(emptyList()) }
    var search   by remember { mutableStateOf("") }

    // Dialog states
    var viewPatient by remember { mutableStateOf<PatientEntity?>(null) }
    var editPatient by remember { mutableStateOf<PatientEntity?>(null) }

    LaunchedEffect(Unit) { patients = db.patientDao().getAllPatients() }

    val filtered = patients.filter {
        search.isBlank() || it.name.contains(search, true) || it.disease.contains(search, true)
    }

    // ── View dialog ───────────────────────────────────────────────────────────
    viewPatient?.let { p ->
        AlertDialog(
            onDismissRequest = { viewPatient = null },
            shape = RoundedCornerShape(20.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        Modifier.size(44.dp).clip(CircleShape)
                            .background(avatarColors[p.id % avatarColors.size]),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(p.name.first().uppercaseChar().toString(),
                            fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Text(p.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Divider()
                    ViewRow("👤", "Name",      p.name)
                    ViewRow("📅", "Age",       "${p.age} years")
                    ViewRow("🩺", "Condition", p.disease)
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewPatient = null },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                ) { Text("Close") }
            }
        )
    }

    // ── Edit dialog ───────────────────────────────────────────────────────────
    editPatient?.let { p ->
        var editName    by remember { mutableStateOf(p.name) }
        var editAge     by remember { mutableStateOf(p.age.toString()) }
        var editDisease by remember { mutableStateOf(p.disease) }
        var editError   by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { editPatient = null },
            shape = RoundedCornerShape(20.dp),
            title = {
                Text("Edit Patient", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editName, onValueChange = { editName = it; editError = "" },
                        label = { Text("Patient Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1976D2),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )
                    OutlinedTextField(
                        value = editAge, onValueChange = { editAge = it; editError = "" },
                        label = { Text("Age") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1976D2),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )
                    OutlinedTextField(
                        value = editDisease, onValueChange = { editDisease = it; editError = "" },
                        label = { Text("Disease / Condition") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1976D2),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )
                    if (editError.isNotEmpty()) {
                        Text(editError, color = Color(0xFFC62828), fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isBlank() || editAge.isBlank() || editDisease.isBlank()) {
                            editError = "All fields are required"; return@Button
                        }
                        val ageInt = editAge.toIntOrNull()
                        if (ageInt == null || ageInt <= 0) {
                            editError = "Enter a valid age"; return@Button
                        }
                        scope.launch {
                            db.patientDao().updatePatient(
                                p.copy(name = editName.trim(), age = ageInt, disease = editDisease.trim())
                            )
                            patients = db.patientDao().getAllPatients()
                            editPatient = null
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                ) { Text("Save") }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { editPatient = null },
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Cancel") }
            }
        )
    }

    Scaffold(bottomBar = { BottomNavBar(navController, Screen.Patients.route) }) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).background(Color(0xFFF5F7FF))) {

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
                    ) { Text("👥", fontSize = 20.sp) }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Patients", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("${filtered.size} records found", fontSize = 13.sp, color = Color.White.copy(.80f))
                    }
                }
            }

            // Search
            OutlinedTextField(
                value = search, onValueChange = { search = it },
                placeholder = { Text("Search by name or condition...", fontSize = 13.sp, color = Color(0xFF9E9E9E)) },
                leadingIcon = { Text("🔍", fontSize = 16.sp) },
                trailingIcon = {
                    if (search.isNotEmpty()) {
                        IconButton(onClick = { search = "" }) {
                            Text("✕", fontSize = 14.sp, color = Color(0xFF9E9E9E))
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1976D2),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)) {
                items(filtered) { patient ->
                    PatientCard(
                        patient     = patient,
                        avatarColor = avatarColors[patient.id % avatarColors.size],
                        onView      = { viewPatient = patient },
                        onEdit      = { editPatient = patient },
                        onDelete    = {
                            scope.launch {
                                db.patientDao().deletePatient(patient)
                                patients = db.patientDao().getAllPatients()
                            }
                        }
                    )
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

// ── Small helper row for view dialog ─────────────────────────────────────────
@Composable
private fun ViewRow(icon: String, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(icon, fontSize = 18.sp)
        Column {
            Text(label, fontSize = 11.sp, color = Color(0xFF9E9E9E))
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
        }
    }
}

@Composable
fun PatientCard(
    patient: PatientEntity,
    avatarColor: Color,
    onView: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            shape = RoundedCornerShape(16.dp),
            title = { Text("Delete Patient") },
            text  = { Text("Remove ${patient.name} from records?") },
            confirmButton = {
                TextButton(onClick = { showDeleteDialog = false; onDelete() }) {
                    Text("Delete", color = Color(0xFFD32F2F))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(46.dp).clip(CircleShape).background(avatarColor),
                contentAlignment = Alignment.Center
            ) {
                Text(patient.name.first().uppercaseChar().toString(),
                    fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(patient.name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                Text("Age: ${patient.age} · ${patient.disease}", fontSize = 12.sp, color = Color(0xFF666666))
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActionChip("View",   Color(0xFFE3F2FD), Color(0xFF1976D2), onClick = onView)
                    ActionChip("Edit",   Color(0xFFE8F5E9), Color(0xFF388E3C), onClick = onEdit)
                    ActionChip("Delete", Color(0xFFFFEBEE), Color(0xFFD32F2F), onClick = { showDeleteDialog = true })
                }
            }
        }
    }
}

@Composable
private fun ActionChip(label: String, bg: Color, textColor: Color, onClick: () -> Unit) {
    Surface(onClick = onClick, color = bg, shape = RoundedCornerShape(8.dp)) {
        Text(label, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            fontSize = 12.sp, fontWeight = FontWeight.Medium, color = textColor)
    }
}