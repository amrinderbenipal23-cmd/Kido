package com.quickfix.kidszone.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickfix.kidszone.data.local.datastore.SettingsDataStore
import com.quickfix.kidszone.data.models.AnimalData
import com.quickfix.kidszone.data.repository.ProgressRepository
import com.quickfix.kidszone.domain.model.GameType
import com.quickfix.kidszone.utils.KiddoAudioManager
import com.quickfix.kidszone.utils.KiddoTextToSpeech
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/** A single question in a quiz-style mini-game. */
data class QuizRound(
    val prompt: String,
    val display: String,            // big content shown to the child (emoji / letter / emoji row)
    val displayIsRow: Boolean,      // true → render [display] as a wrapped row of emojis (Count Objects)
    val options: List<String>,
    val correctIndex: Int,
    val speak: String,              // what TTS says when the round appears
)

data class QuizUiState(
    val title: String = "",
    val headerEmoji: String = "🎮",
    val rounds: List<QuizRound> = emptyList(),
    val currentIndex: Int = 0,
    val score: Int = 0,
    val selectedIndex: Int? = null,
    val answered: Boolean = false,
    val lastCorrect: Boolean = false,
    val finished: Boolean = false,
) {
    val total: Int get() = rounds.size
    val current: QuizRound? get() = rounds.getOrNull(currentIndex)
    val starsEarned: Int
        get() = when {
            total == 0 -> 0
            score >= total -> 3
            score >= (total * 2) / 3 -> 2
            score >= total / 2 -> 1
            else -> 0
        }
}

private const val ROUNDS = 8

@HiltViewModel
class QuizGameViewModel @Inject constructor(
    private val tts: KiddoTextToSpeech,
    private val audio: KiddoAudioManager,
    private val settingsDataStore: SettingsDataStore,
    private val progressRepository: ProgressRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var started = false
    private var gameType: GameType = GameType.FIND_ALPHABET

    fun start(type: GameType) {
        if (started) return
        started = true
        gameType = type
        val (title, emoji) = titleFor(type)
        val rounds = List(ROUNDS) { buildRound(type) }
        _uiState.update { it.copy(title = title, headerEmoji = emoji, rounds = rounds) }
        speakCurrentPrompt()
    }

    fun speakCurrentPrompt() {
        val round = _uiState.value.current ?: return
        viewModelScope.launch {
            if (settingsDataStore.soundEnabled.first()) tts.speak(round.speak)
        }
    }

    fun answer(index: Int) {
        val state = _uiState.value
        if (state.answered) return
        val round = state.current ?: return
        val correct = index == round.correctIndex
        _uiState.update {
            it.copy(
                selectedIndex = index,
                answered = true,
                lastCorrect = correct,
                score = if (correct) it.score + 1 else it.score,
            )
        }
        viewModelScope.launch {
            if (correct) {
                audio.playSuccessSound()
                tts.speakPraise()
            } else {
                audio.playWrongSound()
                tts.speak("Oops! The answer was ${round.options[round.correctIndex]}.")
            }
            delay(1300)
            advance()
        }
    }

    private fun advance() {
        val state = _uiState.value
        if (state.currentIndex >= state.rounds.size - 1) {
            finish(state.score)
        } else {
            _uiState.update {
                it.copy(currentIndex = it.currentIndex + 1, selectedIndex = null, answered = false)
            }
            speakCurrentPrompt()
        }
    }

    private fun finish(score: Int) {
        val stars = _uiState.value.starsEarned
        _uiState.update { it.copy(finished = true) }
        viewModelScope.launch {
            audio.playRewardSound()
            settingsDataStore.addStars(stars)
            settingsDataStore.addCoins(stars * 5)
            val (title, _) = titleFor(gameType)
            progressRepository.updateProgress(
                moduleId = "game_${gameType.name.lowercase()}",
                moduleName = title,
                completed = score,
                total = ROUNDS,
                stars = stars,
            )
        }
    }

    fun playAgain() {
        val rounds = List(ROUNDS) { buildRound(gameType) }
        _uiState.update {
            it.copy(
                rounds = rounds,
                currentIndex = 0,
                score = 0,
                selectedIndex = null,
                answered = false,
                lastCorrect = false,
                finished = false,
            )
        }
        speakCurrentPrompt()
    }

    fun onTap() = audio.playClickSound()

    // ── Round generation ─────────────────────────────────────────────────────

    private fun titleFor(type: GameType): Pair<String, String> = when (type) {
        GameType.FIND_ALPHABET -> "Find the Letter" to "🔤"
        GameType.COUNT_OBJECTS -> "Count the Objects" to "🔢"
        GameType.MATCH_ANIMAL -> "Match the Animal" to "🐾"
        GameType.SHAPE_MATCHING -> "Match the Shape" to "⭐"
        else -> "Quiz" to "🎮"
    }

    private fun buildRound(type: GameType): QuizRound = when (type) {
        GameType.FIND_ALPHABET -> {
            val letters = ('A'..'Z').toList()
            val target = letters.random()
            val opts = (listOf(target) + letters.filter { it != target }.shuffled().take(3)).shuffled()
            QuizRound(
                prompt = "Find the letter \"$target\"",
                display = "🔍",
                displayIsRow = false,
                options = opts.map { it.toString() },
                correctIndex = opts.indexOf(target),
                speak = "Find the letter $target",
            )
        }

        GameType.COUNT_OBJECTS -> {
            val emoji = countEmojis.random()
            val count = (1..6).random()
            val distractors = (1..9).filter { it != count }.shuffled().take(3)
            val opts = (listOf(count) + distractors).shuffled()
            QuizRound(
                prompt = "How many do you see?",
                display = emoji.repeat(count),
                displayIsRow = true,
                options = opts.map { it.toString() },
                correctIndex = opts.indexOf(count),
                speak = "How many do you see? Count them!",
            )
        }

        GameType.MATCH_ANIMAL -> {
            val animals = AnimalData.animals
            val target = animals.random()
            val others = animals.filter { it.id != target.id }.shuffled().take(3)
            val opts = (listOf(target) + others).shuffled()
            QuizRound(
                prompt = "Which animal is this?",
                display = target.emoji,
                displayIsRow = false,
                options = opts.map { it.nameEn },
                correctIndex = opts.indexOf(target),
                speak = "Which animal is this?",
            )
        }

        GameType.SHAPE_MATCHING -> {
            val target = shapes.random()
            val others = shapes.filter { it.first != target.first }.shuffled().take(3)
            val opts = (listOf(target) + others).shuffled()
            QuizRound(
                prompt = "Which shape is this?",
                display = target.second,
                displayIsRow = false,
                options = opts.map { it.first },
                correctIndex = opts.indexOf(target),
                speak = "Which shape is this?",
            )
        }

        else -> QuizRound("?", "❓", false, listOf("A", "B"), 0, "Quiz")
    }

    override fun onCleared() {
        super.onCleared()
        tts.stop()
    }

    companion object {
        private val countEmojis = listOf("🍎", "⭐", "🎈", "🐠", "🌸", "🍓", "🚗", "🦋", "🍪", "🌟")
        private val shapes = listOf(
            "Circle" to "🔵",
            "Square" to "🟦",
            "Triangle" to "🔺",
            "Star" to "⭐",
            "Heart" to "❤️",
            "Diamond" to "🔷",
        )
    }
}
