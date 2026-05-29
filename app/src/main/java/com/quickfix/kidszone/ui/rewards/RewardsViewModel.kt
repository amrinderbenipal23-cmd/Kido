package com.quickfix.kidszone.ui.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickfix.kidszone.data.local.datastore.SettingsDataStore
import com.quickfix.kidszone.data.repository.RewardsRepository
import com.quickfix.kidszone.domain.model.Reward
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class RewardsUiState(
    val allRewards: List<Reward> = emptyList(),
    val totalStars: Int = 0,
    val totalCoins: Int = 0,
    val canClaimDailyReward: Boolean = false,
    val showDailyClaimedMessage: Boolean = false,
)

@HiltViewModel
class RewardsViewModel @Inject constructor(
    private val rewardsRepository: RewardsRepository,
    private val settingsDataStore: SettingsDataStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RewardsUiState())
    val uiState: StateFlow<RewardsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                rewardsRepository.getAllRewards(),
                settingsDataStore.totalStars,
                settingsDataStore.totalCoins,
                settingsDataStore.getDailyRewardClaimedDate(),
            ) { rewards, stars, coins, lastClaimDate ->
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                val canClaim = lastClaimDate != today
                _uiState.update {
                    it.copy(
                        allRewards = rewards,
                        totalStars = stars,
                        totalCoins = coins,
                        canClaimDailyReward = canClaim,
                    )
                }
                checkUnlocks(rewards, stars)
            }.collect()
        }
    }

    private suspend fun checkUnlocks(rewards: List<Reward>, stars: Int) {
        rewards.filter { !it.isUnlocked && stars >= it.stars }.forEach { reward ->
            rewardsRepository.unlockReward(reward.id)
        }
    }

    fun claimDailyReward() {
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            settingsDataStore.setDailyRewardClaimedDate(today)
            settingsDataStore.addStars(5)
            settingsDataStore.addCoins(20)
            _uiState.update { it.copy(canClaimDailyReward = false, showDailyClaimedMessage = true) }
            kotlinx.coroutines.delay(2500)
            _uiState.update { it.copy(showDailyClaimedMessage = false) }
        }
    }
}
