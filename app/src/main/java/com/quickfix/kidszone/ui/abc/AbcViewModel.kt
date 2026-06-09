package com.quickfix.kidszone.ui.abc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickfix.kidszone.data.local.datastore.SettingsDataStore
import com.quickfix.kidszone.data.repository.ProgressRepository
import com.quickfix.kidszone.domain.model.Alphabet
import com.quickfix.kidszone.domain.usecase.GetAlphabetsUseCase
import com.quickfix.kidszone.utils.KiddoAudioManager
import com.quickfix.kidszone.utils.KiddoTextToSpeech
import com.quickfix.kidszone.utils.SpeechRecognitionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AbcUiState(
    val alphabets: List<Alphabet> = emptyList(),
    val currentIndex: Int = 0,
    val isAutoPlaying: Boolean = false,
    val showReward: Boolean = false,
    val completedCount: Int = 0,
    val isListening: Boolean = false,
    val voiceFeedback: String? = null,
)

@HiltViewModel
class AbcViewModel @Inject constructor(
    private val getAlphabetsUseCase: GetAlphabetsUseCase,
    private val tts: KiddoTextToSpeech,
    private val audio: KiddoAudioManager,
    private val speech: SpeechRecognitionManager,
    private val settingsDataStore: SettingsDataStore,
    private val progressRepository: ProgressRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AbcUiState())
    val uiState: StateFlow<AbcUiState> = _uiState.asStateFlow()

    private var autoPlayJob: Job? = null

    init {
        val alphabets = getAlphabetsUseCase()
        _uiState.update { it.copy(alphabets = alphabets) }
        observeSpeech()
    }

    // ── Voice / speaking practice ────────────────────────────────────────────

    private fun observeSpeech() {
        viewModelScope.launch {
            speech.state.collect { state ->
                when (state) {
                    is SpeechRecognitionManager.SpeechState.Listening ->
                        _uiState.update { it.copy(isListening = true, voiceFeedback = "Listening… 👂") }
                    is SpeechRecognitionManager.SpeechState.Result -> {
                        _uiState.update { it.copy(isListening = false) }
                        evaluateSpeech(state.text)
                    }
                    is SpeechRecognitionManager.SpeechState.Error ->
                        _uiState.update { it.copy(isListening = false, voiceFeedback = "Didn't catch that — try again! 🎤") }
                    is SpeechRecognitionManager.SpeechState.Idle ->
                        _uiState.update { it.copy(isListening = false) }
                }
            }
        }
    }

    fun startVoice() {
        viewModelScope.launch {
            if (!settingsDataStore.voiceEnabled.first()) {
                _uiState.update { it.copy(voiceFeedback = "Voice is turned off in Settings.") }
                return@launch
            }
            _uiState.update { it.copy(voiceFeedback = null) }
            speech.startListening()
        }
    }

    fun stopVoice() {
        speech.stopListening()
        _uiState.update { it.copy(isListening = false) }
    }

    private fun evaluateSpeech(spokenRaw: String) {
        val state = _uiState.value
        val current = state.alphabets.getOrNull(state.currentIndex) ?: return
        val spoken = spokenRaw.trim().lowercase()
        val letter = current.capitalLetter.lowercase()
        val word = current.exampleWordEn.lowercase()
        val correct = spoken.isNotEmpty() && (
            spoken == letter ||
                spoken.contains(word) ||
                spoken.split(" ", ",").any { it == letter || it == word }
            )
        viewModelScope.launch {
            if (correct) {
                audio.playSuccessSound()
                tts.speakPraise()
                settingsDataStore.addStars(1)
                _uiState.update { it.copy(voiceFeedback = "🎉 Perfect! You said it right!") }
            } else {
                audio.playWrongSound()
                tts.speakEncouragement()
                _uiState.update {
                    it.copy(voiceFeedback = "You said \"$spokenRaw\". Say \"${current.capitalLetter}\" — try again! 💪")
                }
            }
        }
    }

    fun clearVoiceFeedback() = _uiState.update { it.copy(voiceFeedback = null) }

    fun speakCurrentLetter() {
        val state = _uiState.value
        val current = state.alphabets.getOrNull(state.currentIndex) ?: return
        viewModelScope.launch {
            val soundEnabled = settingsDataStore.soundEnabled.first()
            if (!soundEnabled) return@launch
            val isHindi = settingsDataStore.language.first() == "hi"
            if (isHindi) {
                tts.speakHindi(current.hindiLetter)
                delay(600)
                tts.speakHindi(current.exampleWordHi)
            } else {
                tts.speakAlphabet(current.capitalLetter)
                delay(600)
                tts.speakWord("${current.capitalLetter} for ${current.exampleWordEn}")
            }
        }
    }

    fun next() {
        _uiState.update { state ->
            val next = (state.currentIndex + 1).coerceAtMost(state.alphabets.size - 1)
            val completed = maxOf(state.completedCount, next + 1)
            state.copy(currentIndex = next, completedCount = completed)
        }
        speakCurrentLetter()
        saveProgress()
    }

    fun previous() {
        _uiState.update { it.copy(currentIndex = (it.currentIndex - 1).coerceAtLeast(0)) }
        speakCurrentLetter()
    }

    fun navigateTo(index: Int) {
        _uiState.update { it.copy(currentIndex = index.coerceIn(0, it.alphabets.size - 1)) }
        speakCurrentLetter()
    }

    fun toggleAutoPlay() {
        val isPlaying = _uiState.value.isAutoPlaying
        if (isPlaying) {
            stopAutoPlay()
        } else {
            startAutoPlay()
        }
    }

    private fun startAutoPlay() {
        _uiState.update { it.copy(isAutoPlaying = true) }
        autoPlayJob = viewModelScope.launch {
            while (_uiState.value.isAutoPlaying) {
                speakCurrentLetter()
                delay(2500)
                val state = _uiState.value
                if (state.currentIndex < state.alphabets.size - 1) {
                    next()
                } else {
                    stopAutoPlay()
                    _uiState.update { it.copy(showReward = true) }
                }
            }
        }
    }

    private fun stopAutoPlay() {
        autoPlayJob?.cancel()
        _uiState.update { it.copy(isAutoPlaying = false) }
    }

    fun dismissReward() {
        _uiState.update { it.copy(showReward = false) }
    }

    private fun saveProgress() {
        viewModelScope.launch {
            val state = _uiState.value
            val stars = (state.completedCount / 5).coerceAtMost(3)
            progressRepository.updateProgress(
                moduleId = "abc",
                moduleName = "ABC Learning",
                completed = state.completedCount,
                total = state.alphabets.size,
                stars = stars,
            )
            if (state.completedCount >= 5) {
                settingsDataStore.addStars(1)
                settingsDataStore.addCoins(2)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        autoPlayJob?.cancel()
        tts.stop()
        speech.stopListening()
    }
}
