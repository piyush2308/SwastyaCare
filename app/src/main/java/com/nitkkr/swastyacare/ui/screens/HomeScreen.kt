package com.nitkkr.swastyacare.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.nitkkr.swastyacare.data.AppDatabase
import com.nitkkr.swastyacare.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(navController: NavController) {
    val auth        = FirebaseAuth.getInstance()
    val user        = auth.currentUser
    val displayName = user?.displayName?.takeIf { it.isNotBlank() }
        ?: user?.email?.substringBefore("@") ?: "Doctor"

    val today = SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date())
    val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 0..11  -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        else      -> "Good Evening"
    }

    val context = LocalContext.current
    val db      = AppDatabase.getDatabase(context)
    val scope   = rememberCoroutineScope()

    var patientCount  by remember { mutableStateOf(0) }
    var reminderCount by remember { mutableStateOf(0) }
    var bpm           by remember { mutableStateOf<Int?>(null) }
    var measuring     by remember { mutableStateOf(false) }
    var showMenu      by remember { mutableStateOf(false) }

    // Load lastBpm from SharedPreferences — persists across app restarts
    // but clears on fresh install/rebuild from Android Studio
    val prefs = remember { context.getSharedPreferences("swastyacare_prefs", Context.MODE_PRIVATE) }
    var lastBpm by remember {
        mutableStateOf(run {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val currentInstallTime = packageInfo.firstInstallTime
            val savedInstallTime = prefs.getLong("install_time", -1L)
            if (savedInstallTime != currentInstallTime) {
                // Fresh install or rebuild — wipe saved BPM
                prefs.edit().clear().putLong("install_time", currentInstallTime).apply()
                null
            } else {
                prefs.getInt("last_bpm", -1).takeIf { it != -1 }
            }
        })
    }

    LaunchedEffect(Unit) {
        patientCount  = db.patientDao().getAllPatients().size
        reminderCount = db.reminderDao().getAllReminders().size
    }

    // Pulse animation — only while measuring
    val pulse = rememberInfiniteTransition(label = "pulse")
    val heartScale by pulse.animateFloat(
        initialValue = 1f, targetValue = 1.20f,
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
        label = "heartScale"
    )

    // BPM status label
    val bpmStatus = when {
        bpm == null -> null
        bpm!! < 60  -> Pair("Low",    Color(0xFF1976D2))
        bpm!! < 100 -> Pair("Normal", Color(0xFF388E3C))
        else        -> Pair("High",   Color(0xFFD32F2F))
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController, Screen.Home.route) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF5F7FF))
        ) {
            // ── Header ───────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(Color(0xFF1565C0), Color(0xFF42A5F5))))
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Column {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("SwastyaCare", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Box {
                            Box(
                                modifier = Modifier
                                    .size(42.dp).clip(CircleShape)
                                    .background(Color.White.copy(0.25f))
                                    .clickable { showMenu = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(displayName.first().uppercaseChar().toString(),
                                    fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                                Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                                    Text(displayName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                                    Text(user?.email ?: "", fontSize = 12.sp, color = Color(0xFF9E9E9E))
                                }
                                Divider()
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Text("🚪", fontSize = 16.sp)
                                            Text("Logout", fontSize = 14.sp, color = Color(0xFFD32F2F))
                                        }
                                    },
                                    onClick = {
                                        showMenu = false
                                        auth.signOut()
                                        navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                                    }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("$greeting, Dr. $displayName", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    Text(today, fontSize = 13.sp, color = Color.White.copy(0.75f))
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Stats ─────────────────────────────────────────────────────────
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("👥", "Patients",  patientCount.toString(),  Color(0xFFE3F2FD), Color(0xFF1976D2), Modifier.weight(1f))
                StatCard("🔔", "Reminders", reminderCount.toString(), Color(0xFFE8F5E9), Color(0xFF388E3C), Modifier.weight(1f))
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("❤️", fontSize = 22.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(if (lastBpm != null) "$lastBpm" else "--",
                            fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                        Text("Last BPM", fontSize = 10.sp, color = Color(0xFFD32F2F).copy(0.80f))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── BPM card ──────────────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {

                    // Top row — heart icon + title
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .scale(if (measuring) heartScale else 1f)
                                .size(38.dp).clip(CircleShape)
                                .background(Color(0xFFFFEBEE)),
                            contentAlignment = Alignment.Center
                        ) { Text("❤️", fontSize = 18.sp) }
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                if (measuring) "Measuring..." else "Heart Rate Monitor",
                                fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF9E9E9E)
                            )
                            Text("Simulate BPM check", fontSize = 12.sp, color = Color(0xFFBDBDBD))
                        }
                        // Reset button — only visible after BPM is measured
                        if (bpm != null && !measuring) {
                            TextButton(onClick = { bpm = null; }) {
                                Text("Reset", fontSize = 13.sp, color = Color(0xFFD32F2F))
                            }
                        }
                    }

                    // BPM display — shown after measuring
                    if (bpm != null || measuring) {
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                if (measuring) "..." else "$bpm",
                                fontSize = 48.sp, fontWeight = FontWeight.Bold,
                                color = Color(0xFFD32F2F)
                            )
                            if (!measuring) {
                                Spacer(Modifier.width(6.dp))
                                Text("BPM", fontSize = 20.sp, fontWeight = FontWeight.Medium,
                                    color = Color(0xFFD32F2F), modifier = Modifier.padding(bottom = 8.dp))
                            }
                        }
                        // Status badge
                        bpmStatus?.let { (label, color) ->
                            Spacer(Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(color.copy(0.12f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = color)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    } else {
                        Spacer(Modifier.height(16.dp))
                    }

                    // Start button — full width, hidden while measuring
                    if (!measuring) {
                        Button(
                            onClick = {
                                measuring = true
                                bpm = null
                                scope.launch {
                                    // Simulate ramp-up with random fluctuation over 3 seconds
                                    repeat(12) {
                                        delay(250)
                                    }
                                    bpm = (62..98).random()
                                    lastBpm = bpm
                                    // Persist to SharedPreferences
                                    bpm?.let { prefs.edit().putInt("last_bpm", it).apply() }
                                    measuring = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                        ) {
                            Text("❤  Start Checking", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                    } else {
                        // Progress indicator while measuring
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFFD32F2F),
                            trackColor = Color(0xFFFFEBEE)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Quick actions ─────────────────────────────────────────────────
            Text("Quick Actions", fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A), modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickCard("👤", "Add Patient",   Color(0xFFE3F2FD), Color(0xFF1976D2), Modifier.weight(1f)) { navController.navigate(Screen.AddPatient.route) }
                QuickCard("👥", "View Patients", Color(0xFFE8F5E9), Color(0xFF388E3C), Modifier.weight(1f)) { navController.navigate(Screen.Patients.route) }
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickCard("🔔", "Reminders",      Color(0xFFFFF3E0), Color(0xFFF57C00), Modifier.weight(1f)) { navController.navigate(Screen.Reminder.route) }
                QuickCard("🔬", "AI Health Scan", Color(0xFFF3E5F5), Color(0xFF7B1FA2), Modifier.weight(1f)) { navController.navigate(Screen.HealthScan.route) }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatCard(icon: String, label: String, value: String, bg: Color, textColor: Color, modifier: Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bg), elevation = CardDefaults.cardElevation(0.dp)) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 22.sp)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
            Text(label, fontSize = 10.sp, color = textColor.copy(0.80f))
        }
    }
}

@Composable
private fun QuickCard(icon: String, label: String, bg: Color, iconColor: Color, modifier: Modifier, onClick: () -> Unit) {
    Card(modifier = modifier.clickable(onClick = onClick), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(bg),
                contentAlignment = Alignment.Center) { Text(icon, fontSize = 22.sp) }
            Spacer(Modifier.height(8.dp))
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
        }
    }
}