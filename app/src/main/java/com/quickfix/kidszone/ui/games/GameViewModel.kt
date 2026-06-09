package com.quickfix.kidszone.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickfix.kidszone.data.local.datastore.SettingsDataStore
import com.quickfix.kidszone.domain.model.GameItem
import com.quickfix.kidszone.domain.model.GameDifficulty
import com.quickfix.kidszone.domain.model.GameType
import com.quickfix.kidszone.utils.KiddoAudioManager
import com.quickfix.kidszone.utils.KiddoTextToSpeech
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GamesUiState(val games: List<GameItem> = emptyList())

@HiltViewModel
class GameViewModel @Inject constructor(
    private val tts: KiddoTextToSpeech,
    private val audio: KiddoAudioManager,
    private val settingsDataStore: SettingsDataStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GamesUiState())
    val uiState: StateFlow<GamesUiState> = _uiState.asStateFlow()

    init {
        _uiState.update {
            it.copy(games = listOf(
                GameItem(1, "Memory Cards", "Match the pairs!", "🃏", GameDifficulty.EASY, GameType.MEMORY_CARDS, 0xFF4ECDC4),
                GameItem(2, "Balloon Pop", "Pop the right balloon!", "🎈", GameDifficulty.EASY, GameType.BALLOON_POP, 0xFFFF6B6B),
                GameItem(3, "Match Animal", "Find the matching animal!", "🐾", GameDifficulty.MEDIUM, GameType.MATCH_ANIMAL, 0xFFFFBE0B),
                GameItem(4, "Find Alphabet", "Spot the letter!", "🔤", GameDifficulty.MEDIUM, GameType.FIND_ALPHABET, 0xFF845EC2),
                GameItem(5, "Count Objects", "Count the emojis!", "🔢", GameDifficulty.EASY, GameType.COUNT_OBJECTS, 0xFF06D6A0),
                GameItem(6, "Shape Match", "Match the shapes!", "⭐", GameDifficulty.HARD, GameType.SHAPE_MATCHING, 0xFFFF6B9D),
            ))
        }
    }

    fun onGameCompleted(stars: Int) {
        viewModelScope.launch {
            audio.playRewardSound()
            settingsDataStore.addStars(stars)
            settingsDataStore.addCoins(stars * 5)
            tts.speakPraise()
        }
    }

    /** Play a soft click for taps/selections in games. */
    fun playTap() = audio.playClickSound()

    /** Play the positive match chime. */
    fun playMatch() = audio.playSuccessSound()

    /** Play the gentle "try again" sound. */
    fun playMiss() = audio.playWrongSound()
}
