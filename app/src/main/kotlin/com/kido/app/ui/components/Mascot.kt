package com.kido.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.kido.app.ui.theme.KidoColors

enum class MascotMood { Happy, Cheering, Thinking }

/**
 * Placeholder Kido mascot — a purple blob with eyes. Designed for easy
 * replacement: the same callsites work once a real character asset arrives.
 * Swap point: replace this Canvas drawing with an Image of the final asset.
 */
@Composable
fun Mascot(
    modifier: Modifier = Modifier,
    mood: MascotMood = MascotMood.Happy,
) {
    Canvas(modifier = modifier.aspectRatio(1f)) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        // Body
        drawOval(
            color = KidoColors.MascotBody,
            topLeft = Offset(w * 0.08f, h * 0.12f),
            size = Size(w * 0.84f, h * 0.78f),
        )

        // Eye whites
        val eyeR = w * 0.10f
        val eyeY = cy - h * 0.06f
        val leftEye = Offset(cx - w * 0.15f, eyeY)
        val rightEye = Offset(cx + w * 0.15f, eyeY)
        drawCircle(Color.White, eyeR, leftEye)
        drawCircle(Color.White, eyeR, rightEye)

        // Pupils — look up-and-right when thinking
        val pupilR = eyeR * 0.5f
        val pupilOffsetX = if (mood == MascotMood.Thinking) w * 0.02f else 0f
        val pupilOffsetY = if (mood == MascotMood.Thinking) -h * 0.015f else 0f
        drawCircle(KidoColors.MascotEye, pupilR, leftEye + Offset(pupilOffsetX, pupilOffsetY))
        drawCircle(KidoColors.MascotEye, pupilR, rightEye + Offset(pupilOffsetX, pupilOffsetY))

        // Mouth
        when (mood) {
            MascotMood.Cheering -> {
                drawCircle(
                    color = KidoColors.MascotEye,
                    radius = w * 0.09f,
                    center = Offset(cx, cy + h * 0.18f),
                )
            }
            MascotMood.Thinking -> {
                drawLine(
                    color = KidoColors.MascotEye,
                    start = Offset(cx - w * 0.10f, cy + h * 0.20f),
                    end = Offset(cx + w * 0.10f, cy + h * 0.20f),
                    strokeWidth = w * 0.035f,
                    cap = StrokeCap.Round,
                )
            }
            MascotMood.Happy -> {
                drawArc(
                    color = KidoColors.MascotEye,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(cx - w * 0.15f, cy + h * 0.08f),
                    size = Size(w * 0.30f, h * 0.15f),
                    style = Stroke(width = w * 0.04f, cap = StrokeCap.Round),
                )
            }
        }
    }
}
