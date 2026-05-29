package com.quickfix.kidszone.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rewards")
data class RewardEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val emoji: String,
    val starsRequired: Int,
    val isUnlocked: Boolean = false,
    val type: String,
    val unlockedTimestamp: Long = 0L,
)
