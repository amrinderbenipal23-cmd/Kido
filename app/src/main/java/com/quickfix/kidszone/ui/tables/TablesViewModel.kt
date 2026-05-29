package com.quickfix.kidszone.ui.tables

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickfix.kidszone.data.local.datastore.SettingsDataStore
import com.quickfix.kidszone.data.models.MultiplicationTable
import com.quickfix.kidszone.data.models.TablesData
import com.quickfix.kidszone.data.repository.ProgressRepository
import com.quickfix.kidszone.utils.KiddoTextToSpeech
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

data class QuizQuestion(
    val tableNum: Int,
    val multiplicand: Int,
    val correctAnswer: Int,
    val options: List<Int>,
)

data class TablesUiState(
    val tables: List<MultiplicationTable> = emptyList(),
    val selectedTable: MultiplicationTable? = null,
    val currentRowIndex: Int = 0,
    val isAutoPlaying: Boolean = false,
    val isHindi: Boolean = false,
    // Quiz
    val quizQuestion: QuizQuestion? = null,
    val quizScore: Int = 0,
    val quizTotal: Int = 0,
    val quizAnswered: Boolean = false,
    val lastAnswerCorrect: Boolean = false,
    val quizFinished: Boolean = false,
    // Rewards
    val showReward: Boolean = false,
    val completedTables: Set<Int> = emptySet(),
    val starsEarned: Int = 0,
)

@HiltViewModel
class TablesViewModel @Inject constructor(
    private val tts: KiddoTextToSpeech,
    private val settingsDataStore: SettingsDataStore,
    private val progressRepository: ProgressRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TablesUiState(tables = TablesData.tables))
    val uiState: StateFlow<TablesUiState> = _uiState.asStateFlow()

    private var autoPlayJob: Job? = null

    fun selectTable(tableNum: Int) {
        val table = TablesData.tables.find { it.number == tableNum } ?: return
        _uiState.update { it.copy(selectedTable = table, currentRowIndex = 0) }
        speakRow(table, 0)
    }

    fun nextRow() {
        val state = _uiState.value
        val table = state.selectedTable ?: return
        val next = (state.currentRowIndex + 1).coerceAtMost(table.rows.size - 1)
        _uiState.update { it.copy(currentRowIndex = next) }
        speakRow(table, next)
        if (next == table.rows.size - 1) markTableComplete(table.number)
    }

    fun previousRow() {
        _uiState.update { it.copy(currentRowIndex = (it.currentRowIndex - 1).coerceAtLeast(0)) }
        val state = _uiState.value
        state.selectedTable?.let { speakRow(it, state.currentRowIndex) }
    }

    fun speakCurrentRow() {
        val state = _uiState.value
        state.selectedTable?.let { speakRow(it, state.currentRowIndex) }
    }

    fun toggleAutoPlay() {
        if (_uiState.value.isAutoPlaying) stopAutoPlay() else startAutoPlay()
    }

    fun toggleLanguage() {
        _uiState.update { it.copy(isHindi = !it.isHindi) }
    }

    private fun startAutoPlay() {
        _uiState.update { it.copy(isAutoPlaying = true) }
        autoPlayJob = viewModelScope.launch {
            val table = _uiState.value.selectedTable ?: return@launch
            while (_uiState.value.isAutoPlaying) {
                val state = _uiState.value
                speakRow(table, state.currentRowIndex)
                delay(2800)
                if (state.currentRowIndex < table.rows.size - 1) {
                    _uiState.update { it.copy(currentRowIndex = it.currentRowIndex + 1) }
                } else {
                    stopAutoPlay()
                    markTableComplete(table.number)
                }
            }
        }
    }

    private fun stopAutoPlay() {
        autoPlayJob?.cancel()
        _uiState.update { it.copy(isAutoPlaying = false) }
    }

    private fun speakRow(table: MultiplicationTable, index: Int) {
        viewModelScope.launch {
            if (!settingsDataStore.soundEnabled.first()) return@launch
            val row = table.rows.getOrNull(index) ?: return@launch
            val text = if (_uiState.value.isHindi) row.narrationHi else row.narrationEn
            tts.speak(text)
        }
    }

    // ── Quiz ──────────────────────────────────────────────────────────────────

    fun startQuiz(tableNum: Int) {
        val table = TablesData.tables.find { it.number == tableNum } ?: return
        _uiState.update { it.copy(quizScore = 0, quizTotal = 0, quizFinished = false) }
        generateQuestion(table)
    }

    private fun generateQuestion(table: MultiplicationTable) {
        val multiplicand = Random.nextInt(1, 11)
        val correct = table.number * multiplicand
        val wrong = generateWrongOptions(correct, table.number)
        val options = (wrong + correct).shuffled()
        _uiState.update {
            it.copy(
                quizQuestion = QuizQuestion(table.number, multiplicand, correct, options),
                quizAnswered = false,
                lastAnswerCorrect = false,
            )
        }
        tts.speak("What is ${table.number} times $multiplicand?")
    }

    private fun generateWrongOptions(correct: Int, tableNum: Int): List<Int> {
        val wrongs = mutableSetOf<Int>()
        while (wrongs.size < 3) {
            val offset = Random.nextInt(-4, 5)
            val candidate = correct + offset * tableNum
            if (candidate != correct && candidate > 0) wrongs.add(candidate)
        }
        return wrongs.toList()
    }

    fun answerQuiz(selected: Int) {
        val state = _uiState.value
        val question = state.quizQuestion ?: return
        if (state.quizAnswered) return
        val correct = selected == question.correctAnswer
        val newScore = if (correct) state.quizScore + 1 else state.quizScore
        val newTotal = state.quizTotal + 1
        _uiState.update {
            it.copy(quizAnswered = true, lastAnswerCorrect = correct, quizScore = newScore, quizTotal = newTotal)
        }
        tts.speak(if (correct) "Correct! Well done!" else "Oops! The answer is ${question.correctAnswer}")
        viewModelScope.launch {
            delay(1800)
            if (newTotal >= 10) {
                _uiState.update { it.copy(quizFinished = true, showReward = true, starsEarned = if (newScore >= 8) 3 else if (newScore >= 5) 2 else 1) }
                saveQuizProgress(question.tableNum, newScore)
            } else {
                val table = TablesData.tables.find { it.number == question.tableNum } ?: return@launch
                generateQuestion(table)
            }
        }
    }

    fun restartQuiz(tableNum: Int) {
        _uiState.update { it.copy(quizFinished = false) }
        startQuiz(tableNum)
    }

    private fun markTableComplete(tableNum: Int) {
        _uiState.update { it.copy(completedTables = it.completedTables + tableNum) }
        viewModelScope.launch {
            progressRepository.updateProgress(
                moduleId = "tables_$tableNum",
                moduleName = "Table of $tableNum",
                completed = 10, total = 10, stars = 2,
            )
            settingsDataStore.addStars(1)
            settingsDataStore.addCoins(3)
        }
    }

    private fun saveQuizProgress(tableNum: Int, score: Int) {
        viewModelScope.launch {
            val stars = if (score >= 8) 3 else if (score >= 5) 2 else 1
            progressRepository.updateProgress(
                moduleId = "tables_quiz_$tableNum",
                moduleName = "Table $tableNum Quiz",
                completed = score, total = 10, stars = stars,
            )
            settingsDataStore.addStars(stars)
            settingsDataStore.addCoins(score * 2)
        }
    }

    fun dismissReward() = _uiState.update { it.copy(showReward = false) }

    override fun onCleared() {
        super.onCleared()
        autoPlayJob?.cancel()
        tts.stop()
    }
}
