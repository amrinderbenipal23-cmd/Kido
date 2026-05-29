package com.quickfix.kidszone.ui.tables

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quickfix.kidszone.data.models.TableRow
import com.quickfix.kidszone.ui.components.AnimatedButton
import com.quickfix.kidszone.ui.components.RewardDialog
import com.quickfix.kidszone.ui.theme.TextDark

@Composable
fun TableDetailScreen(
    tableNum: Int,
    onStartQuiz: (Int) -> Unit,
    onBack: () -> Unit,
    viewModel: TablesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(tableNum) { viewModel.selectTable(tableNum) }

    val table = uiState.selectedTable
    val cardColor = table?.let { Color(it.color) } ?: Color(0xFF3D5AF1)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFE8F4FF), Color(0xFFF0E6FF)))),
    ) {
        if (table != null) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(listOf(cardColor, cardColor.copy(alpha = 0.7f))))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⬅️", fontSize = 26.sp, modifier = Modifier.clickable(onClick = onBack))
                        Spacer(Modifier.width(10.dp))
                        Text(table.emoji, fontSize = 30.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Table of ${table.number}",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                        )
                        Spacer(Modifier.weight(1f))
                        // Language toggle
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.25f))
                                .clickable { viewModel.toggleLanguage() }
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        ) {
                            Text(
                                text = if (uiState.isHindi) "हिंदी" else "EN",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        // Auto play toggle
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (uiState.isAutoPlaying) Color(0xFFFF6B6B).copy(0.8f) else Color.White.copy(alpha = 0.25f))
                                .clickable { viewModel.toggleAutoPlay() }
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        ) {
                            Text(
                                text = if (uiState.isAutoPlaying) "⏹" else "▶",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                            )
                        }
                    }
                }

                // Table rows
                val listState = rememberLazyListState()
                LaunchedEffect(uiState.currentRowIndex) {
                    listState.animateScrollToItem(uiState.currentRowIndex)
                }

                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    itemsIndexed(table.rows) { index, row ->
                        TableRowCard(
                            row = row,
                            isHighlighted = index == uiState.currentRowIndex,
                            isHindi = uiState.isHindi,
                            cardColor = cardColor,
                            onClick = {
                                viewModel.selectTable(table.number)
                                viewModel.speakCurrentRow()
                            },
                        )
                    }
                }

                // Navigation + quiz button
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AnimatedButton(
                            text = "◀",
                            onClick = viewModel::previousRow,
                            startColor = cardColor.copy(alpha = 0.7f),
                            endColor = cardColor,
                            modifier = Modifier.weight(1f),
                            height = 52.dp,
                            enabled = uiState.currentRowIndex > 0,
                        )
                        AnimatedButton(
                            text = "🔊",
                            onClick = viewModel::speakCurrentRow,
                            startColor = Color(0xFFFFBE0B),
                            endColor = Color(0xFFFF9671),
                            modifier = Modifier.weight(1f),
                            height = 52.dp,
                        )
                        AnimatedButton(
                            text = "▶",
                            onClick = viewModel::nextRow,
                            startColor = cardColor,
                            endColor = cardColor.copy(alpha = 0.7f),
                            modifier = Modifier.weight(1f),
                            height = 52.dp,
                            enabled = uiState.currentRowIndex < table.rows.size - 1,
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    AnimatedButton(
                        text = "🎯 Start Quiz",
                        onClick = { onStartQuiz(table.number) },
                        startColor = Color(0xFF845EC2),
                        endColor = Color(0xFFFF6B9D),
                        modifier = Modifier.fillMaxWidth(),
                        height = 56.dp,
                    )
                }
            }
        }

        if (uiState.showReward) {
            RewardDialog(
                title = "Table Mastered! 🎉",
                message = "You completed the table of ${table?.number}! Amazing!",
                emoji = table?.emoji ?: "⭐",
                starsEarned = uiState.starsEarned,
                onDismiss = viewModel::dismissReward,
                onContinue = viewModel::dismissReward,
            )
        }
    }
}

@Composable
private fun TableRowCard(
    row: TableRow,
    isHighlighted: Boolean,
    isHindi: Boolean,
    cardColor: Color,
    onClick: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (isHighlighted) 1.04f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "row_scale",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(if (isHighlighted) 12.dp else 4.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isHighlighted)
                    Brush.horizontalGradient(listOf(cardColor, cardColor.copy(alpha = 0.75f)))
                else
                    Brush.horizontalGradient(listOf(Color.White, Color(0xFFF8F8FF)))
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "${row.multiplier} × ${row.multiplicand}",
                color = if (isHighlighted) Color.White else TextDark,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
            )
            Text(
                text = "=",
                color = if (isHighlighted) Color.White.copy(0.8f) else Color(0xFF888899),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "${row.product}",
                color = if (isHighlighted) Color.White else cardColor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 30.sp,
            )
            if (isHighlighted) {
                Text("🔊", fontSize = 22.sp)
            }
        }
        if (isHighlighted && isHindi) {
            Text(
                text = row.narrationHi,
                color = Color.White.copy(0.85f),
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 46.dp),
            )
        }
    }
}
