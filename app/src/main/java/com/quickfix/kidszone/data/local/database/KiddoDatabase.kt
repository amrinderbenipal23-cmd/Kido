package com.quickfix.kidszone.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.quickfix.kidszone.data.local.database.dao.ProgressDao
import com.quickfix.kidszone.data.local.database.dao.RewardDao
import com.quickfix.kidszone.data.local.database.entities.ProgressEntity
import com.quickfix.kidszone.data.local.database.entities.RewardEntity

@Database(
    entities = [ProgressEntity::class, RewardEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class KiddoDatabase : RoomDatabase() {
    abstract fun progressDao(): ProgressDao
    abstract fun rewardDao(): RewardDao
}
