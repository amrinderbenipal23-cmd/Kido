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

    private var tapCount = 0

    /** Returns true on every 2nd tap — caller should show an interstitial. */
    fun recordTap(): Boolean {
        tapCount++
        return tapCount % 2 == 0
    }

    init {
        viewModelScope.launch { rewardsRepository.initDefaultRewards() }
    }
}
