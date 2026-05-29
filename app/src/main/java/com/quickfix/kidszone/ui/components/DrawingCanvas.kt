package com.quickfix.kidszone.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput

data class DrawPath(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float,
    val brushType: BrushType = BrushType.NORMAL,
)

enum class BrushType { NORMAL, GLOW, RAINBOW, ERASER }

@Composable
fun DrawingCanvas(
    paths: List<DrawPath>,
    currentPath: DrawPath?,
    onPathStart: (Offset) -> Unit,
    onPathContinue: (Offset) -> Unit,
    onPathEnd: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .background(backgroundColor)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset -> onPathStart(offset) },
                    onDrag = { change, _ -> onPathContinue(change.position) },
                    onDragEnd = { onPathEnd() },
                )
            }
    ) {
        paths.forEach { path ->
            drawKiddoPath(path)
        }
        currentPath?.let { drawKiddoPath(it) }
    }
}

private fun DrawScope.drawKiddoPath(path: DrawPath) {
    if (path.points.size < 2) return

    val androidPath = Path()
    androidPath.moveTo(path.points.first().x, path.points.first().y)
    for (i in 1 until path.points.size) {
        val prev = path.points[i - 1]
        val curr = path.points[i]
        androidPath.quadraticBezierTo(
            prev.x, prev.y,
            (prev.x + curr.x) / 2f,
            (prev.y + curr.y) / 2f,
        )
    }

    when (path.brushType) {
        BrushType.ERASER -> {
            drawPath(
                path = androidPath,
                color = Color.White,
                style = Stroke(
                    width = path.strokeWidth * 2,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
            )
        }
        BrushType.GLOW -> {
            // Draw glow layers
            for (i in 3 downTo 1) {
                drawPath(
                    path = androidPath,
                    color = path.color.copy(alpha = 0.2f / i),
                    style = Stroke(
                        width = path.strokeWidth * (1 + i),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round,
                    ),
                )
            }
            drawPath(
                path = androidPath,
                color = path.color,
                style = Stroke(
                    width = path.strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
            )
        }
        BrushType.RAINBOW -> {
            val rainbowColors = listOf(
                Color.Red, Color(0xFFFF6B00), Color.Yellow,
                Color.Green, Color.Cyan, Color.Blue, Color.Magenta,
            )
            path.points.forEachIndexed { index, point ->
                if (index < path.points.size - 1) {
                    val color = rainbowColors[index % rainbowColors.size]
                    val nextPoint = path.points[index + 1]
                    val singlePath = Path().apply {
                        moveTo(point.x, point.y)
                        lineTo(nextPoint.x, nextPoint.y)
                    }
                    drawPath(
                        path = singlePath,
                        color = color,
                        style = Stroke(
                            width = path.strokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                        ),
                    )
                }
            }
        }
        BrushType.NORMAL -> {
            drawPath(
                path = androidPath,
                color = path.color,
                style = Stroke(
                    width = path.strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
            )
        }
    }
}
