package com.kido.app.feature.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kido.app.KidoApp
import com.kido.app.R
import com.kido.app.core.content.AlphabetPack
import com.kido.app.core.content.ContentResult
import com.kido.app.core.content.Letter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class GameState(
    val round: Int,
    val totalRounds: Int,
    val currentLetter: Letter,
    val choices: List<Letter>,
    val phase: GamePhase,
    val starsThisSession: Int,
)

enum class GamePhase { Asking, Correct, Incorrect, Complete }

enum class GameError { ContentUnavailable }

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as KidoApp

    private val _state = MutableStateFlow<GameState?>(null)
    val state: StateFlow<GameState?> = _state.asStateFlow()

    private val _errorState = MutableStateFlow<GameError?>(null)
    val errorState: StateFlow<GameError?> = _errorState.asStateFlow()

    private var alphabet: AlphabetPack? = null
    private var sessionPool: MutableList<Letter> = mutableListOf()
    private val totalRounds = 5
    private var started = false

    fun start() {
        if (started) return
        started = true
        viewModelScope.launch {
            val profile = app.profile.profile.first()
            when (val result = app.content.loadAlphabet(profile.languageCode)) {
                is ContentResult.Ok -> {
                    alphabet = result.value
                    sessionPool = result.value.letters.shuffled().toMutableList()
                    app.narration.setLocale(result.value.localeTag)
                    nextRound(roundNum = 1, starsSoFar = 0)
                }
                is ContentResult.NotFound, is ContentResult.ParseError -> {
                    _errorState.value = GameError.ContentUnavailable
                }
            }
        }
    }

    private fun nextRound(roundNum: Int, starsSoFar: Int) {
        val pack = alphabet ?: return
        if (sessionPool.isEmpty()) sessionPool = pack.letters.shuffled().toMutableList()
        val letter = sessionPool.removeFirst()
        val distractors = pack.letters.filter { it.id != letter.id }.shuffled().take(3)
        val choices = (distractors + letter).shuffled()
        _state.value = GameState(
            round = roundNum,
            totalRounds = totalRounds,
            currentLetter = letter,
            choices = choices,
            phase = GamePhase.Asking,
            starsThisSession = starsSoFar,
        )
        viewModelScope.launch {
            delay(350)
            speakPrompt(letter)
        }
    }

    private fun speakPrompt(letter: Letter) {
        val text = app.getString(R.string.narration_prompt, letter.name)
        app.narration.speak(text, utteranceId = "prompt-${letter.id}")
    }

    fun onChoice(letter: Letter) {
        val current = _state.value ?: return
        if (current.phase != GamePhase.Asking) return

        if (letter.id == current.currentLetter.id) {
            _state.value = current.copy(phase = GamePhase.Correct)
            viewModelScope.launch {
                app.profile.awardStar(letter.id)
                app.narration.speakAndWait(
                    app.getString(R.string.narration_correct, letter.glyph, letter.word),
                    utteranceId = "good-${letter.id}",
                )
                val newStars = current.starsThisSession + 1
                if (current.round < current.totalRounds) {
                    nextRound(current.round + 1, newStars)
                } else {
                    _state.value = current.copy(phase = GamePhase.Complete, starsThisSession = newStars)
                    app.narration.speakAndWait(
                        app.getString(R.string.narration_complete, newStars),
                        utteranceId = "complete",
                    )
                }
            }
        } else {
            _state.value = current.copy(phase = GamePhase.Incorrect)
            viewModelScope.launch {
                app.narration.speakAndWait(
                    app.getString(R.string.narration_retry),
                    utteranceId = "retry-${letter.id}",
                )
                if (_state.value?.phase == GamePhase.Incorrect) {
                    _state.value = _state.value?.copy(phase = GamePhase.Asking)
                }
            }
        }
    }

    fun repeatPrompt() {
        val current = _state.value ?: return
        if (current.phase == GamePhase.Asking) speakPrompt(current.currentLetter)
    }

    override fun onCleared() {
        app.narration.stop()
        super.onCleared()
    }
}
