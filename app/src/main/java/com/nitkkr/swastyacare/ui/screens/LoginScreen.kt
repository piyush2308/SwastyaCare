package com.nitkkr.swastyacare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.nitkkr.swastyacare.navigation.Screen

@Composable
fun LoginScreen(navController: NavController) {
    val auth     = FirebaseAuth.getInstance()
    var tab      by remember { mutableStateOf(0) }   // 0=Login  1=Register
    var name     by remember { mutableStateOf("") }
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }
    var error    by remember { mutableStateOf("") }
    var loading  by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF1565C0), Color(0xFF1976D2), Color(0xFF2196F3))))
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))
            Text("SwastyaCare", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Healthcare Management System", fontSize = 13.sp, color = Color.White.copy(0.80f))
            Spacer(Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(10.dp)
            ) {
                Column(Modifier.padding(24.dp)) {

                    // ── Tab switcher ─────────────────────────────────────────
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFEEF2FF))
                    ) {
                        listOf("Login", "Register").forEachIndexed { i, label ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (tab == i) Color(0xFF1976D2) else Color.Transparent),
                                contentAlignment = Alignment.Center
                            ) {
                                TextButton(
                                    onClick = { tab = i; error = "" },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        label,
                                        color = if (tab == i) Color.White else Color(0xFF888888),
                                        fontWeight = if (tab == i) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(22.dp))
                    Text(
                        if (tab == 0) "Welcome Back!" else "Create Account",
                        fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A)
                    )
                    Text(
                        if (tab == 0) "Sign in to continue" else "Fill in your details",
                        fontSize = 13.sp, color = Color(0xFF9E9E9E)
                    )
                    Spacer(Modifier.height(20.dp))

                    // ── Name field — register only ────────────────────────────
                    if (tab == 1) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it; error = "" },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1976D2),
                                unfocusedBorderColor = Color(0xFFE0E0E0)
                            )
                        )
                        Spacer(Modifier.height(12.dp))
                    }

                    // ── Email ─────────────────────────────────────────────────
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; error = "" },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1976D2),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )
                    Spacer(Modifier.height(12.dp))

                    // ── Password ──────────────────────────────────────────────
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; error = "" },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            TextButton(onClick = { showPass = !showPass }) {
                                Text(if (showPass) "Hide" else "Show", fontSize = 12.sp, color = Color(0xFF1976D2))
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1976D2),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )

                    // ── Error ─────────────────────────────────────────────────
                    if (error.isNotEmpty()) {
                        Spacer(Modifier.height(10.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(error, modifier = Modifier.padding(10.dp),
                                color = Color(0xFFC62828), fontSize = 13.sp)
                        }
                    }

                    Spacer(Modifier.height(22.dp))

                    // ── Action button ─────────────────────────────────────────
                    Button(
                        onClick = {
                            error = ""
                            if (tab == 1 && name.isBlank()) { error = "Please enter your name"; return@Button }
                            if (email.isBlank()) { error = "Please enter your email"; return@Button }
                            if (password.isBlank()) { error = "Please enter your password"; return@Button }
                            if (password.length < 6) { error = "Password must be at least 6 characters"; return@Button }

                            loading = true
                            if (tab == 0) {
                                // ── Login ────────────────────────────────────
                                auth.signInWithEmailAndPassword(email.trim(), password)
                                    .addOnSuccessListener {
                                        loading = false
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo(Screen.Login.route) { inclusive = true }
                                        }
                                    }
                                    .addOnFailureListener {
                                        loading = false
                                        error = it.message ?: "Login failed"
                                    }
                            } else {
                                // ── Register — save displayName ───────────────
                                auth.createUserWithEmailAndPassword(email.trim(), password)
                                    .addOnSuccessListener { result ->
                                        val update = userProfileChangeRequest { displayName = name.trim() }
                                        result.user?.updateProfile(update)?.addOnCompleteListener {
                                            loading = false
                                            navController.navigate(Screen.Home.route) {
                                                popUpTo(Screen.Login.route) { inclusive = true }
                                            }
                                        }
                                    }
                                    .addOnFailureListener {
                                        loading = false
                                        error = it.message ?: "Registration failed"
                                    }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                        enabled = !loading
                    ) {
                        if (loading)
                            CircularProgressIndicator(Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                        else
                            Text(
                                if (tab == 0) "Login" else "Create Account",
                                fontSize = 16.sp, fontWeight = FontWeight.SemiBold
                            )
                    }
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}