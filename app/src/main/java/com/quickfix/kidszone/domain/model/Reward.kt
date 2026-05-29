package com.quickfix.kidszone.domain.model

data class Reward(
    val id: Int,
    val title: String,
    val description: String,
    val emoji: String,
    val stars: Int,
    val isUnlocked: Boolean = false,
    val type: RewardType,
)

enum class RewardType {
    BADGE, STICKER, THEME, ANIMATION
}
