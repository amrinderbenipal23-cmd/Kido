package com.quickfix.kidszone.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KiddoColorScheme = lightColorScheme(
    primary = KiddoPurple,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEADDFF),
    onPrimaryContainer = KiddoPurple,

    secondary = KiddoOrange,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDDCC),
    onSecondaryContainer = KiddoOrange,

    tertiary = KiddoCyan,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFCCF5F0),
    onTertiaryContainer = Color(0xFF006B60),

    background = Color(0xFFF8F4FF),
    onBackground = TextDark,

    surface = CardSurface,
    onSurface = TextDark,
    surfaceVariant = Color(0xFFEDE7F6),
    onSurfaceVariant = TextMedium,

    error = KiddoRed,
    onError = Color.White,

    outline = Color(0xFFCCCCDD),
)

@Composable
fun KidsZoneTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KiddoColorScheme,
        typography = Typography,
        content = content,
    )
}
