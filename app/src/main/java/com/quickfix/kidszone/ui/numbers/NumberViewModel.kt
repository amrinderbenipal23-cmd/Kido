package com.quickfix.kidszone.ui.numbers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickfix.kidszone.data.local.datastore.SettingsDataStore
import com.quickfix.kidszone.data.repository.ProgressRepository
import com.quickfix.kidszone.domain.model.NumberItem
import com.quickfix.kidszone.domain.usecase.GetNumbersUseCase
import com.quickfix.kidszone.utils.KiddoTextToSpeech
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NumberUiState(
    val numbers: List<NumberItem> = emptyList(),
    val currentIndex: Int = 0,
    val isCountingAnimating: Boolean = false,
    val showReward: Boolean = false,
    val completedCount: Int = 0,
    val isAutoPlaying: Boolean = false,
)

@HiltViewModel
class NumberViewModel @Inject constructor(
    private val getNumbersUseCase: GetNumbersUseCase,
    private val tts: KiddoTextToSpeech,
    private val settingsDataStore: SettingsDataStore,
    private val progressRepository: ProgressRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NumberUiState())
    val uiState: StateFlow<NumberUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(numbers = getNumbersUseCase()) }
        speakCurrentNumber()
    }

    fun speakCurrentNumber() {
        val current = _uiState.value.numbers.getOrNull(_uiState.value.currentIndex) ?: return
        viewModelScope.launch {
            if (!settingsDataStore.soundEnabled.first()) return@launch
            if (settingsDataStore.language.first() == "hi") {
                tts.speakHindi("${current.value}. ${current.wordHi}")
            } else {
                tts.speak("${current.value}. ${current.wordEn}")
            }
        }
    }

    fun countAnimation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCountingAnimating = true) }
            val current = _uiState.value.numbers.getOrNull(_uiState.value.currentIndex) ?: return@launch
            repeat(current.value) {
                tts.speak((it + 1).toString())
                delay(400)
            }
            _uiState.update { it.copy(isCountingAnimating = false) }
        }
    }

    fun next() {
        _uiState.update { state ->
            val next = (state.currentIndex + 1).coerceAtMost(state.numbers.size - 1)
            state.copy(
                currentIndex = next,
                completedCount = maxOf(state.completedCount, next + 1),
            )
        }
        speakCurrentNumber()
        saveProgress()
    }

    fun previous() {
        _uiState.update { it.copy(currentIndex = (it.currentIndex - 1).coerceAtLeast(0)) }
        speakCurrentNumber()
    }

    fun toggleAutoPlay() {
        val playing = _uiState.value.isAutoPlaying
        _uiState.update { it.copy(isAutoPlaying = !playing) }
        if (!playing) {
            viewModelScope.launch {
                while (_uiState.value.isAutoPlaying) {
                    speakCurrentNumber()
                    delay(2000)
                    val state = _uiState.value
                    if (state.currentIndex < state.numbers.size - 1) {
                        next()
                    } else {
                        _uiState.update { it.copy(isAutoPlaying = false, showReward = true) }
                    }
                }
            }
        }
    }

    fun dismissReward() = _uiState.update { it.copy(showReward = false) }

    private fun saveProgress() {
        viewModelScope.launch {
            val state = _uiState.value
            progressRepository.updateProgress("numbers", "Number Learning", state.completedCount, state.numbers.size, (state.completedCount / 5).coerceAtMost(3))
            settingsDataStore.addStars(1)
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts.stop()
    }
}
