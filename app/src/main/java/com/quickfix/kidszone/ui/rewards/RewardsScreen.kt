package com.quickfix.kidszone.ui.rewards

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.quickfix.kidszone.domain.model.Reward
import com.quickfix.kidszone.ui.components.AnimatedButton
import com.quickfix.kidszone.ui.components.ConfettiAnimation
import com.quickfix.kidszone.ui.components.SuccessBanner
import com.quickfix.kidszone.ui.theme.TextDark

@Composable
fun RewardsScreen(
    onBack: () -> Unit,
    viewModel: RewardsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF1A1A2E), Color(0xFF16213E)))),
    ) {
        if (uiState.showDailyClaimedMessage) {
            ConfettiAnimation(modifier = Modifier.fillMaxSize())
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("⬅️", fontSize = 28.sp, modifier = Modifier.clickable(onClick = onBack))
                Spacer(Modifier.width(12.dp))
                Text("🏆 Rewards", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                Spacer(Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⭐ ${uiState.totalStars}", color = Color(0xFFFFD700), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                    Spacer(Modifier.width(12.dp))
                    Text("🪙 ${uiState.totalCoins}", color = Color(0xFFFFBF00), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                }
            }

            // Daily reward
            DailyRewardCard(
                canClaim = uiState.canClaimDailyReward,
                onClaim = viewModel::claimDailyReward,
                showMessage = uiState.showDailyClaimedMessage,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )

            Text(
                "Your Badges & Unlocks",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(uiState.allRewards, key = { it.id }) { reward ->
                    RewardBadge(reward = reward)
                }
            }
        }
    }
}

@Composable
private fun DailyRewardCard(
    canClaim: Boolean,
    onClaim: () -> Unit,
    showMessage: Boolean,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "daily")
    val glow by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800, easing = EaseInOut), RepeatMode.Reverse),
        label = "daily_glow",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(if (canClaim) 12.dp else 4.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (canClaim)
                    Brush.horizontalGradient(listOf(Color(0xFFFF9671), Color(0xFFFF6B35)))
                else
                    Brush.horizontalGradient(listOf(Color(0xFF555566), Color(0xFF333344)))
            )
            .alpha(if (canClaim) glow else 0.7f)
            .padding(16.dp),
    ) {
        if (showMessage) {
            SuccessBanner("🎁 +5 Stars, +20 Coins claimed!")
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🎁", fontSize = 40.sp)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Daily Reward!", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                    Text(
                        if (canClaim) "Claim your ⭐ +5 & 🪙 +20!" else "Come back tomorrow!",
                        color = Color.White.copy(0.85f),
                        fontSize = 14.sp,
                    )
                }
                if (canClaim) {
                    AnimatedButton(
                        text = "Claim!",
                        onClick = onClaim,
                        startColor = Color.White.copy(0.3f),
                        endColor = Color.White.copy(0.15f),
                        textColor = Color.White,
                        height = 44.dp,
                    )
                }
            }
        }
    }
}

@Composable
private fun RewardBadge(reward: Reward) {
    val scale by animateFloatAsState(
        targetValue = if (reward.isUnlocked) 1f else 0.9f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "badge_scale",
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .shadow(if (reward.isUnlocked) 8.dp else 2.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (reward.isUnlocked)
                    Brush.verticalGradient(listOf(Color(0xFF845EC2), Color(0xFFFF6B9D)))
                else
                    Brush.verticalGradient(listOf(Color(0xFF2D2D44), Color(0xFF1A1A2E)))
            )
            .padding(12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (reward.isUnlocked) reward.emoji else "🔒",
                fontSize = 36.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                reward.title,
                color = if (reward.isUnlocked) Color.White else Color.White.copy(0.5f),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
            )
            if (!reward.isUnlocked) {
                Text("⭐ ${reward.stars}", color = Color(0xFFFFD700).copy(0.7f), fontSize = 11.sp)
            }
        }
    }
}
