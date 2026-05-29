package com.quickfix.kidszone.data.local.database.dao

import androidx.room.*
import com.quickfix.kidszone.data.local.database.entities.RewardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RewardDao {

    @Query("SELECT * FROM rewards ORDER BY starsRequired ASC")
    fun getAllRewards(): Flow<List<RewardEntity>>

    @Query("SELECT * FROM rewards WHERE isUnlocked = 1")
    fun getUnlockedRewards(): Flow<List<RewardEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(rewards: List<RewardEntity>)

    @Query("UPDATE rewards SET isUnlocked = 1, unlockedTimestamp = :timestamp WHERE id = :rewardId")
    suspend fun unlockReward(rewardId: Int, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM rewards WHERE isUnlocked = 1")
    fun getUnlockedCount(): Flow<Int>
}
