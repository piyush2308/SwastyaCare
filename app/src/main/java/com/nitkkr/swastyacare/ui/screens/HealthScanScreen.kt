package com.nitkkr.swastyacare.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nitkkr.swastyacare.BuildConfig
import com.nitkkr.swastyacare.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.TimeUnit

@Composable
fun HealthScanScreen(navController: NavController) {
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()

    var imageUri  by remember { mutableStateOf<Uri?>(null) }
    var result    by remember { mutableStateOf("") }
    var analyzing by remember { mutableStateOf(false) }
    var error     by remember { mutableStateOf("") }

    // Camera
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            val file = File(context.cacheDir, "scan_${System.currentTimeMillis()}.jpg")
            file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
            imageUri = Uri.fromFile(file)
            result = ""; error = ""
        }
    }

    // Gallery
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { imageUri = it; result = ""; error = "" }
    }

    Scaffold(bottomBar = { BottomNavBar(navController, Screen.HealthScan.route) }) { pad ->
        Column(
            Modifier.fillMaxSize().padding(pad).background(Color(0xFFF5F7FF))
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Box(
                Modifier.fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(Color(0xFF4A148C), Color(0xFF9C27B0))))
                    .statusBarsPadding()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(44.dp).clip(CircleShape).background(Color.White.copy(0.25f)),
                        contentAlignment = Alignment.Center
                    ) { Text("🔬", fontSize = 20.sp) }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Health Scan", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("AI-Powered Analysis", fontSize = 13.sp, color = Color.White.copy(.80f))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Info card
            Card(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                    Text("🔬", fontSize = 26.sp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("AI Health Analysis", fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold, color = Color(0xFF4A148C))
                        Text("Upload or capture a medical image for instant AI analysis",
                            fontSize = 12.sp, color = Color(0xFF6A1B9A))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Image preview
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(model = imageUri, contentDescription = "Selected",
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)))
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📷", fontSize = 40.sp)
                        Text("No image selected", fontSize = 14.sp, color = Color(0xFF9E9E9E))
                        Text("Use buttons below", fontSize = 12.sp, color = Color(0xFFBDBDBD))
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Camera / Gallery buttons
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { cameraLauncher.launch(null) },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF7B1FA2)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF7B1FA2))
                ) { Text("📷  Camera", fontSize = 14.sp) }

                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF7B1FA2)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF7B1FA2))
                ) { Text("🖼️  Gallery", fontSize = 14.sp) }
            }

            Spacer(Modifier.height(12.dp))

            // Analyze button
            Button(
                onClick = {
                    val uri = imageUri ?: run { error = "Please select an image first"; return@Button }
                    analyzing = true; result = ""; error = ""
                    scope.launch {
                        try {
                            val analysisResult = analyzeWithClaude(context, uri)
                            result = analysisResult
                        } catch (e: Exception) {
                            error = "Analysis failed: ${e.message}"
                        } finally { analyzing = false }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (imageUri != null) Color(0xFF7B1FA2) else Color(0xFFBDBDBD)
                ),
                enabled = !analyzing
            ) {
                if (analyzing) {
                    CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(Modifier.width(10.dp))
                    Text("Analyzing...", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                } else {
                    Text("🔬  Analyze with AI", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // Error
            if (error.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Card(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(12.dp)
                ) { Text(error, Modifier.padding(14.dp), color = Color(0xFFC62828), fontSize = 13.sp) }
            }

            // Result
            if (result.isNotEmpty()) {
                Spacer(Modifier.height(14.dp))
                Card(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("🤖 AI Analysis Result", fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold, color = Color(0xFF4A148C))
                        Spacer(Modifier.height(10.dp))
                        Text(result, fontSize = 13.sp, color = Color(0xFF333333), lineHeight = 20.sp)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

private suspend fun analyzeWithClaude(context: android.content.Context, uri: Uri): String = withContext(Dispatchers.IO) {
    val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
    val bytes = context.contentResolver.openInputStream(uri)!!.use { it.readBytes() }
    val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)

    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    val requestBody = JSONObject().apply {
        put("model", "claude-opus-4-5")
        put("max_tokens", 1024)
        put("messages", JSONArray().put(JSONObject().apply {
            put("role", "user")
            put("content", JSONArray().apply {
                put(JSONObject().apply {
                    put("type", "image")
                    put("source", JSONObject().apply {
                        put("type", "base64")
                        put("media_type", mimeType)
                        put("data", base64)
                    })
                })
                put(JSONObject().apply {
                    put("type", "text")
                    put("text", "You are a medical AI assistant. Analyze this image and provide a brief health observation. Note any visible symptoms, conditions, or relevant medical observations. Keep the response concise and in plain language.")
                })
            })
        }))
    }.toString()

    val request = Request.Builder()
        .url("https://api.anthropic.com/v1/messages")
        .post(requestBody.toRequestBody("application/json".toMediaType()))
        .addHeader("x-api-key", BuildConfig.CLAUDE_API_KEY)
        .addHeader("anthropic-version", "2023-06-01")
        .addHeader("content-type", "application/json")
        .build()

    val response = client.newCall(request).execute()
    val responseBody = response.body?.string() ?: throw Exception("Empty response")
    if (!response.isSuccessful) throw Exception("API error ${response.code}: $responseBody")

    val json = JSONObject(responseBody)
    json.getJSONArray("content").getJSONObject(0).getString("text")
}