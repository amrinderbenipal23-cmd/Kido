package com.quickfix.kidszone.ui.drawing

import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quickfix.kidszone.ui.components.BrushType
import com.quickfix.kidszone.ui.components.DrawingCanvas
import com.quickfix.kidszone.ui.components.SuccessBanner
import com.quickfix.kidszone.ui.theme.TextDark

@Composable
fun DrawingScreen(
    onBack: () -> Unit,
    viewModel: DrawingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    Column(modifier = Modifier.fillMaxSize()) {
        // Top bar — same in both orientations
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A1A2E))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("⬅️", fontSize = 26.sp, modifier = Modifier.clickable(onClick = onBack))
            Spacer(Modifier.width(8.dp))
            Text("🎨 Drawing", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            Spacer(Modifier.weight(1f))
            ToolButton("↩️", onClick = viewModel::undo)
            ToolButton("🗑️", onClick = viewModel::clearCanvas)
            ToolButton("💾", onClick = { viewModel.saveDrawing(context) })
        }

        if (isPortrait) {
            // Portrait: canvas takes full width, tools at bottom
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                DrawingCanvas(
                    paths = uiState.paths,
                    currentPath = uiState.currentPath,
                    onPathStart = viewModel::onPathStart,
                    onPathContinue = viewModel::onPathContinue,
                    onPathEnd = viewModel::onPathEnd,
                    modifier = Modifier.fillMaxSize(),
                )

                if (uiState.showSavedMessage) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .align(Alignment.TopCenter),
                        contentAlignment = Alignment.Center,
                    ) {
                        SuccessBanner("Drawing saved! 💾✅")
                    }
                }
            }

            // Brush selector row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2D2D44))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BrushSelectorRow(
                    selected = uiState.brushType,
                    onSelect = viewModel::selectBrush,
                )
            }

            // Size slider row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2D2D44))
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Size", color = Color.White.copy(0.7f), fontSize = 11.sp)
                Spacer(Modifier.width(8.dp))
                Slider(
                    value = uiState.strokeWidth,
                    onValueChange = viewModel::setStrokeWidth,
                    valueRange = 4f..40f,
                    modifier = Modifier.weight(1f),
                )
            }

            // Color palette
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A1A2E))
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ColorPaletteItems(uiState, viewModel)
            }
        } else {
            // Landscape: tools on left, canvas on right
            Row(modifier = Modifier.weight(1f)) {
                Column(
                    modifier = Modifier
                        .width(70.dp)
                        .fillMaxHeight()
                        .background(Color(0xFF2D2D44))
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    BrushSelectorVertical(
                        selected = uiState.brushType,
                        onSelect = viewModel::selectBrush,
                    )

                    Spacer(Modifier.height(8.dp))

                    Text("Size", color = Color.White.copy(0.7f), fontSize = 11.sp)
                    Slider(
                        value = uiState.strokeWidth,
                        onValueChange = viewModel::setStrokeWidth,
                        valueRange = 4f..40f,
                        modifier = Modifier
                            .height(100.dp)
                            .padding(horizontal = 8.dp),
                    )
                }

                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    DrawingCanvas(
                        paths = uiState.paths,
                        currentPath = uiState.currentPath,
                        onPathStart = viewModel::onPathStart,
                        onPathContinue = viewModel::onPathContinue,
                        onPathEnd = viewModel::onPathEnd,
                        modifier = Modifier.fillMaxSize(),
                    )

                    if (uiState.showSavedMessage) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                                .align(Alignment.TopCenter),
                            contentAlignment = Alignment.Center,
                        ) {
                            SuccessBanner("Drawing saved! 💾✅")
                        }
                    }
                }
            }

            // Color palette
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A1A2E))
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ColorPaletteItems(uiState, viewModel)
            }
        }
    }
}

@Composable
private fun ColorPaletteItems(uiState: DrawingUiState, viewModel: DrawingViewModel) {
    brushColors.forEach { color ->
        val isSelected = uiState.selectedColor == color && uiState.brushType == BrushType.NORMAL
        Box(
            modifier = Modifier
                .size(if (isSelected) 42.dp else 36.dp)
                .shadow(if (isSelected) 8.dp else 2.dp, CircleShape)
                .clip(CircleShape)
                .background(color)
                .border(
                    width = if (isSelected) 3.dp else 0.dp,
                    color = Color.White,
                    shape = CircleShape,
                )
                .clickable { viewModel.selectColor(color) },
        )
    }
}

@Composable
private fun BrushSelectorVertical(selected: BrushType, onSelect: (BrushType) -> Unit) {
    val brushes = listOf(
        BrushType.NORMAL to "✏️",
        BrushType.GLOW to "✨",
        BrushType.RAINBOW to "🌈",
        BrushType.ERASER to "🗒",
    )
    brushes.forEach { (type, emoji) ->
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (selected == type) Color(0xFF845EC2) else Color.Transparent)
                .clickable { onSelect(type) },
            contentAlignment = Alignment.Center,
        ) {
            Text(emoji, fontSize = 24.sp)
        }
    }
}

@Composable
private fun RowScope.BrushSelectorRow(selected: BrushType, onSelect: (BrushType) -> Unit) {
    val brushes = listOf(
        BrushType.NORMAL to "✏️",
        BrushType.GLOW to "✨",
        BrushType.RAINBOW to "🌈",
        BrushType.ERASER to "🗒",
    )
    brushes.forEach { (type, emoji) ->
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (selected == type) Color(0xFF845EC2) else Color.Transparent)
                .clickable { onSelect(type) },
            contentAlignment = Alignment.Center,
        ) {
            Text(emoji, fontSize = 24.sp)
        }
    }
}

@Composable
private fun ToolButton(emoji: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(0.1f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(emoji, fontSize = 22.sp)
    }
}
