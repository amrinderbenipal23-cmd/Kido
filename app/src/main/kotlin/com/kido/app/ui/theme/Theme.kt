package com.kido.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = KidoColors.Berry,
    onPrimary = Color.White,
    primaryContainer = KidoColors.SoftPurple,
    onPrimaryContainer = KidoColors.DeepPurple,
    secondary = KidoColors.Sky,
    onSecondary = Color.White,
    tertiary = KidoColors.Sunshine,
    onTertiary = KidoColors.DeepPurple,
    background = KidoColors.Cream,
    onBackground = Color(0xFF2A1A4A),
    surface = Color.White,
    onSurface = Color(0xFF2A1A4A),
    error = KidoColors.Cherry,
    onError = Color.White,
)

@Composable
fun KidoTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content,
    )
}
