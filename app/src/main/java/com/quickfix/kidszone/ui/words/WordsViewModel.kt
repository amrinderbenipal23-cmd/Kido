package com.quickfix.kidszone.ui.words

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickfix.kidszone.data.local.datastore.SettingsDataStore
import com.quickfix.kidszone.data.models.SentenceItem
import com.quickfix.kidszone.data.models.WordCategory
import com.quickfix.kidszone.data.models.WordItem
import com.quickfix.kidszone.data.models.WordsData
import com.quickfix.kidszone.data.repository.ProgressRepository
import com.quickfix.kidszone.utils.KiddoTextToSpeech
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WordsUiState(
    val categories: List<WordCategory> = WordCategory.entries,
    val selectedCategory: WordCategory? = null,
    val wordsInCategory: List<WordItem> = emptyList(),
    val currentWordIndex: Int = 0,
    val isAutoPlaying: Boolean = false,
    val isHindi: Boolean = false,
    // Sentence making
    val sentences: List<SentenceItem> = WordsData.sentences,
    val currentSentenceIndex: Int = 0,
    val shuffledWords: List<String> = emptyList(),
    val selectedWords: List<String> = emptyList(),
    val sentenceResult: SentenceResult = SentenceResult.NONE,
    // Reward
    val showReward: Boolean = false,
    val starsEarned: Int = 0,
)

enum class SentenceResult { NONE, CORRECT, WRONG }

@HiltViewModel
class WordsViewModel @Inject constructor(
    private val tts: KiddoTextToSpeech,
    private val settingsDataStore: SettingsDataStore,
    private val progressRepository: ProgressRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WordsUiState())
    val uiState: StateFlow<WordsUiState> = _uiState.asStateFlow()

    fun selectCategory(category: WordCategory) {
        val words = WordsData.getByCategory(category)
        _uiState.update { it.copy(selectedCategory = category, wordsInCategory = words, currentWordIndex = 0) }
        speakWord(words.firstOrNull())
    }

    fun speakCurrentWord() {
        val state = _uiState.value
        speakWord(state.wordsInCategory.getOrNull(state.currentWordIndex))
    }

    private fun speakWord(word: WordItem?) {
        word ?: return
        viewModelScope.launch {
            if (!settingsDataStore.soundEnabled.first()) return@launch
            if (_uiState.value.isHindi) {
                tts.speak("${word.wordEn}. Hindi: ${word.wordHi}")
            } else {
                tts.speak("${word.wordEn}. ${word.exampleEn}")
            }
        }
    }

    fun nextWord() {
        val state = _uiState.value
        val next = (state.currentWordIndex + 1).coerceAtMost(state.wordsInCategory.size - 1)
        _uiState.update { it.copy(currentWordIndex = next) }
        speakWord(state.wordsInCategory.getOrNull(next))
        if (next == state.wordsInCategory.size - 1) {
            saveWordProgress(state.selectedCategory)
        }
    }

    fun previousWord() {
        val state = _uiState.value
        val prev = (state.currentWordIndex - 1).coerceAtLeast(0)
        _uiState.update { it.copy(currentWordIndex = prev) }
        speakWord(state.wordsInCategory.getOrNull(prev))
    }

    fun toggleAutoPlay() {
        if (_uiState.value.isAutoPlaying) {
            _uiState.update { it.copy(isAutoPlaying = false) }
        } else {
            _uiState.update { it.copy(isAutoPlaying = true) }
            viewModelScope.launch {
                while (_uiState.value.isAutoPlaying) {
                    speakCurrentWord()
                    delay(3000)
                    val state = _uiState.value
                    if (state.currentWordIndex < state.wordsInCategory.size - 1) {
                        nextWord()
                    } else {
                        _uiState.update { it.copy(isAutoPlaying = false) }
                    }
                }
            }
        }
    }

    fun toggleLanguage() = _uiState.update { it.copy(isHindi = !it.isHindi) }

    // ── Sentence Making ───────────────────────────────────────────────────────

    fun loadSentence(index: Int) {
        val sentence = WordsData.sentences.getOrNull(index) ?: return
        _uiState.update {
            it.copy(
                currentSentenceIndex = index,
                shuffledWords = sentence.words.shuffled(),
                selectedWords = emptyList(),
                sentenceResult = SentenceResult.NONE,
            )
        }
    }

    fun selectWord(word: String) {
        val state = _uiState.value
        if (state.sentenceResult != SentenceResult.NONE) return
        val remaining = state.shuffledWords.toMutableList().also { it.remove(word) }
        val selected = state.selectedWords + word
        _uiState.update { it.copy(shuffledWords = remaining, selectedWords = selected) }
    }

    fun removeWord(word: String) {
        val state = _uiState.value
        if (state.sentenceResult != SentenceResult.NONE) return
        val selected = state.selectedWords.toMutableList().also { it.remove(word) }
        val remaining = state.shuffledWords + word
        _uiState.update { it.copy(selectedWords = selected, shuffledWords = remaining) }
    }

    fun checkSentence() {
        val state = _uiState.value
        val sentence = state.sentences.getOrNull(state.currentSentenceIndex) ?: return
        val built = state.selectedWords.joinToString(" ")
        val correct = built.equals(sentence.sentence.trimEnd('.'), ignoreCase = true) ||
                built.equals(sentence.sentence, ignoreCase = true)
        val result = if (correct) SentenceResult.CORRECT else SentenceResult.WRONG
        _uiState.update { it.copy(sentenceResult = result) }
        if (correct) {
            tts.speak("Excellent! ${sentence.sentence}")
            viewModelScope.launch {
                settingsDataStore.addStars(1)
                settingsDataStore.addCoins(2)
            }
        } else {
            tts.speak("Try again! You can do it!")
        }
    }

    fun speakSentence() {
        val sentence = _uiState.value.sentences.getOrNull(_uiState.value.currentSentenceIndex) ?: return
        tts.speak(sentence.sentence)
    }

    fun nextSentence() {
        val next = (_uiState.value.currentSentenceIndex + 1).coerceAtMost(_uiState.value.sentences.size - 1)
        loadSentence(next)
    }

    fun previousSentence() {
        val prev = (_uiState.value.currentSentenceIndex - 1).coerceAtLeast(0)
        loadSentence(prev)
    }

    fun resetSentence() {
        loadSentence(_uiState.value.currentSentenceIndex)
    }

    private fun saveWordProgress(category: WordCategory?) {
        category ?: return
        viewModelScope.launch {
            val words = WordsData.getByCategory(category)
            progressRepository.updateProgress(
                moduleId = "words_${category.name}",
                moduleName = "${category.displayName} Words",
                completed = words.size, total = words.size, stars = 2,
            )
            settingsDataStore.addStars(2)
            settingsDataStore.addCoins(5)
            _uiState.update { it.copy(showReward = true, starsEarned = 2) }
        }
    }

    fun dismissReward() = _uiState.update { it.copy(showReward = false) }

    override fun onCleared() {
        super.onCleared()
        tts.stop()
    }
}
