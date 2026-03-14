package com.nitkkr.swastyacare.ui.screens.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary   = Color(0xFF1976D2),
    secondary = Color(0xFF42A5F5),
    background = Color(0xFFF5F7FF),
    surface   = Color.White
)

@Composable
fun SwastyaCareTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColors, content = content)
}