package com.quickfix.kidszone.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progress")
data class ProgressEntity(
    @PrimaryKey val moduleId: String,
    val moduleName: String,
    val completedItems: Int = 0,
    val totalItems: Int,
    val stars: Int = 0,
    val lastPlayedTimestamp: Long = System.currentTimeMillis(),
)
