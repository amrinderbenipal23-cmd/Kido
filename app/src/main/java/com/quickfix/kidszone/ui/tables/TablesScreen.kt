package com.quickfix.kidszone.ui.tables

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.quickfix.kidszone.data.models.MultiplicationTable
import com.quickfix.kidszone.ui.components.BannerAdView
import com.quickfix.kidszone.ui.theme.TextDark

@Composable
fun TablesScreen(
    onTableSelected: (Int) -> Unit,
    onBack: () -> Unit,
    viewModel: TablesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFE8F4FF), Color(0xFFF0E6FF)))),
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.95f))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("⬅️", fontSize = 28.sp, modifier = Modifier.clickable(onClick = onBack))
            Spacer(Modifier.width(12.dp))
            Text("✖️ Tables Learning", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = TextDark)
            Spacer(Modifier.weight(1f))
            Text("2 – 20", color = Color(0xFF3D5AF1), fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Text(
            text = "Choose a table to learn! 🎯",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = TextDark,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f),
        ) {
            items(uiState.tables) { table ->
                TableChip(
                    table = table,
                    isCompleted = table.number in uiState.completedTables,
                    onClick = { onTableSelected(table.number) },
                )
            }
        }

        BannerAdView()
    }
}

@Composable
private fun TableChip(
    table: MultiplicationTable,
    isCompleted: Boolean,
    onClick: () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "chip")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(900, easing = EaseInOut), RepeatMode.Reverse),
        label = "chip_scale",
    )
    val cardColor = Color(table.color)

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .scale(scale)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.verticalGradient(listOf(cardColor, cardColor.copy(alpha = 0.7f))))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(table.emoji, fontSize = 28.sp, textAlign = TextAlign.Center)
            Text(
                text = "× ${table.number}",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
            )
            if (isCompleted) Text("✅", fontSize = 14.sp)
        }
    }
}
