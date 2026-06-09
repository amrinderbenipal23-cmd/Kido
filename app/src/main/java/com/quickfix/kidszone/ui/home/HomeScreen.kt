package com.quickfix.kidszone.ui.home

import android.app.Activity
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quickfix.kidszone.utils.AdManager
import com.quickfix.kidszone.ui.LocalAdsEnabled
import com.quickfix.kidszone.ui.components.AnimatedMascot
import com.quickfix.kidszone.ui.components.BannerAdView
import com.quickfix.kidszone.ui.components.ModuleCard
import com.quickfix.kidszone.ui.theme.*

data class HomeModule(
    val title: String,
    val emoji: String,
    val description: String,
    val startColor: Color,
    val endColor: Color,
    val onClick: () -> Unit,
)

@Composable
fun HomeScreen(
    onNavigateToAbc: () -> Unit,
    onNavigateToNumbers: () -> Unit,
    onNavigateToAnimals: () -> Unit,
    onNavigateToDrawing: () -> Unit,
    onNavigateToGames: () -> Unit,
    onNavigateToRewards: () -> Unit,
    onNavigateToParent: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToTables: () -> Unit,
    onNavigateToWords: () -> Unit,
    onNavigateToStories: () -> Unit,
    onNavigateToPoems: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val activity = LocalContext.current as Activity
    val childName by viewModel.childName.collectAsStateWithLifecycle()
    val totalStars by viewModel.totalStars.collectAsStateWithLifecycle()
    val totalCoins by viewModel.totalCoins.collectAsStateWithLifecycle()
    val streak by viewModel.currentStreak.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()
    val adsEnabled = LocalAdsEnabled.current
    val isHindi = language == "hi"

    // Shows an interstitial on every 2nd tap (only when ads are enabled), then
    // proceeds with navigation. Falls through immediately otherwise.
    fun navigateWithAd(destination: () -> Unit) {
        if (adsEnabled && viewModel.recordTap()) {
            AdManager.showInterstitial(activity = activity, onDismissed = destination)
        } else {
            destination()
        }
    }

    val modules = listOf(
        HomeModule("ABC Learn", "🔤", "A for Apple...", AbcCardColor, Color(0xFFFF8E8E)) { navigateWithAd(onNavigateToAbc) },
        HomeModule("Numbers", "🔢", "Count to 100!", NumberCardColor, Color(0xFF7EDBD6)) { navigateWithAd(onNavigateToNumbers) },
        HomeModule("Animals", "🐾", "Meet the animals!", AnimalCardColor, Color(0xFFFFD166)) { navigateWithAd(onNavigateToAnimals) },
        HomeModule("Drawing", "🎨", "Create art!", DrawingCardColor, Color(0xFFFF95C0)) { navigateWithAd(onNavigateToDrawing) },
        HomeModule("Games", "🎮", "Play & Learn!", GamesCardColor, Color(0xFFA57ED9)) { navigateWithAd(onNavigateToGames) },
        HomeModule("Rewards", "🏆", "Your trophies!", RewardsCardColor, Color(0xFFFFB399)) { navigateWithAd(onNavigateToRewards) },
        HomeModule("Tables", "✖️", "2 to 20 tables!", Color(0xFF3D5AF1), Color(0xFF0A97B0)) { navigateWithAd(onNavigateToTables) },
        HomeModule("Words", "📖", "Learn & speak!", Color(0xFF11998E), Color(0xFF38EF7D)) { navigateWithAd(onNavigateToWords) },
        HomeModule("Stories", "📚", "Moral stories!", Color(0xFFB24592), Color(0xFFF15F79)) { navigateWithAd(onNavigateToStories) },
        HomeModule("Poems", "🎵", "Sing & learn!", Color(0xFFFA709A), Color(0xFFFEE140)) { navigateWithAd(onNavigateToPoems) },
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFFF0E6FF), Color(0xFFFFF0F6)))
            ),
    ) {
        // Top Bar
        HomeTopBar(
            childName = childName,
            totalStars = totalStars,
            totalCoins = totalCoins,
            onParentClick = onNavigateToParent,
            onSettingsClick = onNavigateToSettings,
        )

        Spacer(Modifier.height(8.dp))

        // Mascot greeting
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 20.dp),
        ) {
            AnimatedMascot(
                emoji = "🦉",
                size = 70.dp,
                message = if (isHindi) "नमस्ते, $childName! 👋" else "Hello, $childName! 👋",
                isExcited = true,
            )
            Spacer(Modifier.weight(1f))
            DailyStreakBadge(streak = streak)
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = if (isHindi) "आज आप क्या सीखना चाहते हैं?" else "What do you want to learn today?",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp,
            color = TextDark,
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Spacer(Modifier.height(12.dp))

        // Module grid — equal-height rows filling all space above the ad
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            modules.chunked(2).forEach { rowModules ->
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    rowModules.forEach { module ->
                        ModuleCard(
                            title = module.title,
                            emoji = module.emoji,
                            description = module.description,
                            startColor = module.startColor,
                            endColor = module.endColor,
                            onClick = module.onClick,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                        )
                    }
                    if (rowModules.size < 2) Spacer(Modifier.weight(1f))
                }
            }
        }

        // Banner ad pinned to bottom
        BannerAdView()
    }
}

@Composable
private fun HomeTopBar(
    childName: String,
    totalStars: Int,
    totalCoins: Int,
    onParentClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp)
            .background(
                Brush.horizontalGradient(listOf(Color(0xFF845EC2), Color(0xFFFF6B9D)))
            )
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Kiddo Learn", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 26.sp)
                Text("Hi, $childName! 🌟", color = Color.White.copy(0.9f), fontSize = 16.sp)
            }

            // Stars
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(0.2f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text("⭐", fontSize = 18.sp)
                Spacer(Modifier.width(4.dp))
                Text(totalStars.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.width(12.dp))
                Text("🪙", fontSize = 18.sp)
                Spacer(Modifier.width(4.dp))
                Text(totalCoins.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            Spacer(Modifier.width(8.dp))

            Text(
                "👨‍👩‍👧",
                fontSize = 28.sp,
                modifier = Modifier.clickable(onClick = onParentClick),
            )

            Spacer(Modifier.width(8.dp))

            Text(
                "⚙️",
                fontSize = 26.sp,
                modifier = Modifier.clickable(onClick = onSettingsClick),
            )
        }
    }
}

@Composable
private fun DailyStreakBadge(streak: Int) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFFFF9671), Color(0xFFFF6B35))))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🔥", fontSize = 22.sp)
            Text(
                text = if (streak > 0) "$streak day${if (streak == 1) "" else "s"}" else "Start!",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
            )
        }
    }
}
