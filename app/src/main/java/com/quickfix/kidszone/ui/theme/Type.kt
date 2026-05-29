package com.quickfix.kidszone.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val KiddoFont = FontFamily.SansSerif

val Typography = Typography(
    displayLarge = TextStyle(fontFamily = KiddoFont, fontWeight = FontWeight.ExtraBold, fontSize = 96.sp, lineHeight = 96.sp),
    displayMedium = TextStyle(fontFamily = KiddoFont, fontWeight = FontWeight.Bold, fontSize = 60.sp, lineHeight = 64.sp),
    displaySmall = TextStyle(fontFamily = KiddoFont, fontWeight = FontWeight.Bold, fontSize = 48.sp, lineHeight = 52.sp),
    headlineLarge = TextStyle(fontFamily = KiddoFont, fontWeight = FontWeight.ExtraBold, fontSize = 36.sp, lineHeight = 40.sp),
    headlineMedium = TextStyle(fontFamily = KiddoFont, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 34.sp),
    headlineSmall = TextStyle(fontFamily = KiddoFont, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 30.sp),
    titleLarge = TextStyle(fontFamily = KiddoFont, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontFamily = KiddoFont, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 24.sp),
    titleSmall = TextStyle(fontFamily = KiddoFont, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontFamily = KiddoFont, fontWeight = FontWeight.Normal, fontSize = 20.sp, lineHeight = 28.sp),
    bodyMedium = TextStyle(fontFamily = KiddoFont, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp),
    bodySmall = TextStyle(fontFamily = KiddoFont, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontFamily = KiddoFont, fontWeight = FontWeight.Bold, fontSize = 18.sp, lineHeight = 24.sp),
    labelMedium = TextStyle(fontFamily = KiddoFont, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    labelSmall = TextStyle(fontFamily = KiddoFont, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp),
)
