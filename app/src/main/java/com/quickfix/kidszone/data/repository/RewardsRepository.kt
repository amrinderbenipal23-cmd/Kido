package com.quickfix.kidszone.data.repository

import com.quickfix.kidszone.data.local.database.dao.RewardDao
import com.quickfix.kidszone.data.local.database.entities.RewardEntity
import com.quickfix.kidszone.domain.model.Reward
import com.quickfix.kidszone.domain.model.RewardType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RewardsRepository @Inject constructor(
    private val rewardDao: RewardDao,
) {
    fun getAllRewards(): Flow<List<Reward>> = rewardDao.getAllRewards().map { list ->
        list.map { it.toDomain() }
    }

    fun getUnlockedRewards(): Flow<List<Reward>> = rewardDao.getUnlockedRewards().map { list ->
        list.map { it.toDomain() }
    }

    suspend fun initDefaultRewards() {
        val defaults = listOf(
            RewardEntity(1, "Star Collector", "Earn your first star!", "⭐", 1, type = "BADGE"),
            RewardEntity(2, "ABC Explorer", "Learn all 26 alphabets", "🔤", 10, type = "BADGE"),
            RewardEntity(3, "Number Wizard", "Count to 20!", "🔢", 15, type = "BADGE"),
            RewardEntity(4, "Animal Friend", "Meet 10 animals", "🐾", 20, type = "STICKER"),
            RewardEntity(5, "Super Artist", "Complete a drawing", "🎨", 5, type = "STICKER"),
            RewardEntity(6, "Game Champion", "Win 3 games", "🏆", 25, type = "THEME"),
            RewardEntity(7, "Rainbow Theme", "Unlock rainbow colors", "🌈", 30, type = "THEME"),
            RewardEntity(8, "Space Explorer", "Learn 50 items", "🚀", 50, type = "ANIMATION"),
            RewardEntity(9, "Speed Reader", "Complete ABC in under 2 min", "⚡", 35, type = "ANIMATION"),
            RewardEntity(10, "Master Learner", "Complete all modules!", "👑", 100, type = "BADGE"),
        )
        rewardDao.insertAll(defaults)
    }

    suspend fun unlockReward(rewardId: Int) = rewardDao.unlockReward(rewardId)

    private fun RewardEntity.toDomain() = Reward(
        id = id,
        title = title,
        description = description,
        emoji = emoji,
        stars = starsRequired,
        isUnlocked = isUnlocked,
        type = RewardType.valueOf(type),
    )
}
