package com.quickfix.kidszone.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Compact speaking-practice row: a mic button plus a prompt and live feedback.
 * Shared by the ABC and Words modules.
 */
@Composable
fun VoicePracticeRow(
    isListening: Boolean,
    feedback: String?,
    prompt: String,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = Color(0xFF845EC2),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.6f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VoiceButton(
            isListening = isListening,
            onToggle = onToggle,
            size = 56.dp,
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = feedback ?: prompt,
                color = accent,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
            )
            if (feedback == null) {
                Text(
                    text = "Practice speaking! 🌟",
                    color = accent.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                )
            }
        }
    }
}
