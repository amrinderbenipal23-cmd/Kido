package com.quickfix.kidszone.ui.parent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickfix.kidszone.data.local.datastore.SettingsDataStore
import com.quickfix.kidszone.data.repository.ProgressRepository
import com.quickfix.kidszone.domain.model.Progress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ParentUiState(
    val isUnlocked: Boolean = false,
    val inputAnswer: String = "",
    val mathQuestion: String = "5 + 3 = ?",
    val correctAnswer: Int = 8,
    val showError: Boolean = false,
    val allProgress: List<Progress> = emptyList(),
    val totalStars: Int = 0,
    val totalCoins: Int = 0,
    val screenTimeMinutes: Long = 0L,
    val childName: String = "Kiddo",
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val adsEnabled: Boolean = true,
)

@HiltViewModel
class ParentViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
    private val progressRepository: ProgressRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ParentUiState())
    val uiState: StateFlow<ParentUiState> = _uiState.asStateFlow()

    init {
        generateMathQuestion()
        viewModelScope.launch {
            combine(
                progressRepository.getAllProgress(),
                settingsDataStore.totalStars,
                settingsDataStore.totalCoins,
                settingsDataStore.screenTimeMinutes,
                settingsDataStore.childName,
            ) { progress, stars, coins, time, name ->
                _uiState.update {
                    it.copy(
                        allProgress = progress,
                        totalStars = stars,
                        totalCoins = coins,
                        screenTimeMinutes = time,
                        childName = name,
                    )
                }
            }.collect()
        }
    }

    private fun generateMathQuestion() {
        val a = (2..9).random()
        val b = (2..9).random()
        _uiState.update { it.copy(mathQuestion = "$a + $b = ?", correctAnswer = a + b, inputAnswer = "") }
    }

    fun onAnswerChange(value: String) {
        if (value.length <= 2 && value.all { it.isDigit() }) {
            _uiState.update { it.copy(inputAnswer = value, showError = false) }
        }
    }

    fun submitAnswer() {
        val answer = _uiState.value.inputAnswer.toIntOrNull()
        if (answer == _uiState.value.correctAnswer) {
            _uiState.update { it.copy(isUnlocked = true, showError = false) }
        } else {
            _uiState.update { it.copy(showError = true) }
            generateMathQuestion()
        }
    }

    fun lock() {
        _uiState.update { it.copy(isUnlocked = false, inputAnswer = "") }
        generateMathQuestion()
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.setSoundEnabled(enabled) }
        _uiState.update { it.copy(soundEnabled = enabled) }
    }

    fun setMusicEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.setMusicEnabled(enabled) }
        _uiState.update { it.copy(musicEnabled = enabled) }
    }

    fun setAdsEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.setAdsEnabled(enabled) }
        _uiState.update { it.copy(adsEnabled = enabled) }
    }

    fun setChildName(name: String) {
        if (name.length <= 20) {
            viewModelScope.launch { settingsDataStore.setChildName(name) }
            _uiState.update { it.copy(childName = name) }
        }
    }
}
