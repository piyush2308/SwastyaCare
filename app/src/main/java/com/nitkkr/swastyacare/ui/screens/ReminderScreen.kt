package com.nitkkr.swastyacare.ui.screens

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nitkkr.swastyacare.data.AppDatabase
import com.nitkkr.swastyacare.data.ReminderEntity
import com.nitkkr.swastyacare.navigation.Screen
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun ReminderScreen(navController: NavController) {
    val context = LocalContext.current
    val db      = AppDatabase.getDatabase(context)
    val scope   = rememberCoroutineScope()

    var medicineName by remember { mutableStateOf("") }
    var timeInput    by remember { mutableStateOf("") }
    var reminders    by remember { mutableStateOf<List<ReminderEntity>>(emptyList()) }
    var error        by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { reminders = db.reminderDao().getAllReminders() }

    Scaffold(bottomBar = { BottomNavBar(navController, Screen.Reminder.route) }) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).background(Color(0xFFF5F7FF))) {

            // Header
            Box(
                Modifier.fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(Color(0xFFE65100), Color(0xFFFF9800))))
                    .statusBarsPadding()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(44.dp).clip(CircleShape).background(Color.White.copy(0.25f)),
                        contentAlignment = Alignment.Center
                    ) { Text("🕐", fontSize = 22.sp) }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Medicine Reminders", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Never miss your medication", fontSize = 13.sp, color = Color.White.copy(.80f))
                    }
                }
            }

            LazyColumn(contentPadding = PaddingValues(16.dp)) {

                // ── Form card ─────────────────────────────────────────────────
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(Modifier.padding(18.dp)) {
                            Text("Set New Reminder", fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                            Spacer(Modifier.height(14.dp))
                            OutlinedTextField(
                                value = medicineName, onValueChange = { medicineName = it; error = "" },
                                label = { Text("Medicine Name") },
                                leadingIcon = { Text("💊", fontSize = 18.sp) },
                                modifier = Modifier.fillMaxWidth(), singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFF57C00),
                                    unfocusedBorderColor = Color(0xFFE0E0E0)
                                )
                            )
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(
                                value = timeInput, onValueChange = { timeInput = it; error = "" },
                                label = { Text("Reminder Time (HH:MM)") },
                                leadingIcon = { Text("🕐", fontSize = 18.sp) },
                                modifier = Modifier.fillMaxWidth(), singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                placeholder = { Text("e.g. 08:00", color = Color(0xFF9E9E9E)) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFF57C00),
                                    unfocusedBorderColor = Color(0xFFE0E0E0)
                                )
                            )
                            if (error.isNotEmpty()) {
                                Spacer(Modifier.height(8.dp))
                                Text(error, color = Color(0xFFC62828), fontSize = 12.sp)
                            }
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    if (medicineName.isBlank() || timeInput.isBlank()) {
                                        error = "Please fill all fields"; return@Button
                                    }
                                    if (!timeInput.matches(Regex("\\d{2}:\\d{2}"))) {
                                        error = "Time format must be HH:MM (e.g. 08:00)"; return@Button
                                    }
                                    scope.launch {
                                        val reminder = ReminderEntity(medicineName = medicineName.trim(), time = timeInput.trim())
                                        db.reminderDao().insertReminder(reminder)
                                        scheduleAlarm(context, medicineName.trim(), timeInput.trim())
                                        reminders = db.reminderDao().getAllReminders()
                                        medicineName = ""; timeInput = ""
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57C00))
                            ) {
                                Text("🕐  Add Reminder", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }

                // ── Active reminders header ───────────────────────────────────
                if (reminders.isNotEmpty()) {
                    item {
                        Text("Active Reminders", fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                        Spacer(Modifier.height(10.dp))
                    }
                }

                items(reminders) { reminder ->
                    ReminderItem(reminder) {
                        scope.launch {
                            db.reminderDao().deleteReminder(reminder)
                            reminders = db.reminderDao().getAllReminders()
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun ReminderItem(reminder: ReminderEntity, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFFFF3E0)),
                contentAlignment = Alignment.Center
            ) { Text("💊", fontSize = 20.sp) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(reminder.medicineName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                Text(reminder.time, fontSize = 12.sp, color = Color(0xFF666666))
            }
            IconButton(onClick = onDelete) {
                Box(
                    Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFFFEBEE)),
                    contentAlignment = Alignment.Center
                ) { Text("🗑️", fontSize = 16.sp) }
            }
        }
    }
}

fun scheduleAlarm(context: Context, medicine: String, time: String) {
    try {
        val parts = time.split(":")
        if (parts.size != 2) return
        val hour   = parts[0].toInt()
        val minute = parts[1].toInt()
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DATE, 1)
        }
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("medicine", medicine)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            medicine.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}