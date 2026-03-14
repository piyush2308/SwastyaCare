package com.nitkkr.swastyacare

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.nitkkr.swastyacare.navigation.AppNavGraph
import com.nitkkr.swastyacare.navigation.Screen
import com.nitkkr.swastyacare.ui.screens.theme.SwastyaCareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Allow content to draw behind status bar
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Set status bar color to our app blue
        window.statusBarColor = android.graphics.Color.parseColor("#1565C0")

        // Make status bar icons white (battery, signal, time)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false   // false = white icons
        }

        setContent {
            SwastyaCareTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val start = if (FirebaseAuth.getInstance().currentUser != null)
                        Screen.Home.route else Screen.Login.route
                    AppNavGraph(navController = navController, startDestination = start)
                }
            }
        }
    }
}