package com.quickfix.kidszone.data.repository

import com.quickfix.kidszone.data.local.database.dao.ProgressDao
import com.quickfix.kidszone.data.local.database.entities.ProgressEntity
import com.quickfix.kidszone.domain.model.Progress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgressRepository @Inject constructor(
    private val progressDao: ProgressDao,
) {
    fun getAllProgress(): Flow<List<Progress>> = progressDao.getAllProgress().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun updateProgress(moduleId: String, moduleName: String, completed: Int, total: Int, stars: Int) {
        progressDao.insertOrUpdate(
            ProgressEntity(
                moduleId = moduleId,
                moduleName = moduleName,
                completedItems = completed,
                totalItems = total,
                stars = stars,
                lastPlayedTimestamp = System.currentTimeMillis(),
            )
        )
    }

    fun getTotalStars(): Flow<Int?> = progressDao.getTotalStars()

    private fun ProgressEntity.toDomain() = Progress(
        moduleId = moduleId,
        moduleName = moduleName,
        completedItems = completedItems,
        totalItems = totalItems,
        stars = stars,
        lastPlayedTimestamp = lastPlayedTimestamp,
    )
}
