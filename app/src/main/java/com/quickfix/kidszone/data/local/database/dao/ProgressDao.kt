package com.quickfix.kidszone.data.local.database.dao

import androidx.room.*
import com.quickfix.kidszone.data.local.database.entities.ProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {

    @Query("SELECT * FROM progress ORDER BY lastPlayedTimestamp DESC")
    fun getAllProgress(): Flow<List<ProgressEntity>>

    @Query("SELECT * FROM progress WHERE moduleId = :moduleId")
    suspend fun getProgress(moduleId: String): ProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: ProgressEntity)

    @Query("UPDATE progress SET completedItems = :completed, stars = :stars, lastPlayedTimestamp = :timestamp WHERE moduleId = :moduleId")
    suspend fun updateProgress(moduleId: String, completed: Int, stars: Int, timestamp: Long)

    @Query("SELECT SUM(stars) FROM progress")
    fun getTotalStars(): Flow<Int?>

    @Query("SELECT SUM(completedItems) FROM progress")
    fun getTotalCompleted(): Flow<Int?>
}
