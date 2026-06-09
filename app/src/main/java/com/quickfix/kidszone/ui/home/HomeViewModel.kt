package com.quickfix.kidszone.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickfix.kidszone.data.local.datastore.SettingsDataStore
import com.quickfix.kidszone.data.repository.ProgressRepository
import com.quickfix.kidszone.data.repository.RewardsRepository
import com.quickfix.kidszone.domain.model.Progress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    private val progressRepository: ProgressRepository,
    private val rewardsRepository: RewardsRepository,
) : ViewModel() {

    val childName: StateFlow<String> = settingsDataStore.childName
        .stateIn(viewModelScope, SharingStarted.Eagerly, "Kiddo")

    val totalStars: StateFlow<Int> = settingsDataStore.totalStars
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val totalCoins: StateFlow<Int> = settingsDataStore.totalCoins
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val allProgress: StateFlow<List<Progress>> = progressRepository.getAllProgress()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val currentStreak: StateFlow<Int> = settingsDataStore.currentStreak
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val language: StateFlow<String> = settingsDataStore.language
        .stateIn(viewModelScope, SharingStarted.Eagerly, "en")

    private var tapCount = 0

    /** Returns true on every 2nd tap — caller should show an interstitial. */
    fun recordTap(): Boolean {
        tapCount++
        return tapCount % 2 == 0
    }

    init {
        viewModelScope.launch { rewardsRepository.initDefaultRewards() }
        updateStreak()
    }

    private fun updateStreak() {
        viewModelScope.launch {
            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val today = fmt.format(Date())
            val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
            val yesterday = fmt.format(cal.time)
            settingsDataStore.updateStreakOnAppOpen(today, yesterday)
        }
    }
}
