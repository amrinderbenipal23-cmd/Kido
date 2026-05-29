package com.quickfix.kidszone.ui.poems

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickfix.kidszone.data.local.datastore.SettingsDataStore
import com.quickfix.kidszone.data.models.PoemItem
import com.quickfix.kidszone.data.models.PoemsData
import com.quickfix.kidszone.data.repository.ProgressRepository
import com.quickfix.kidszone.utils.KiddoTextToSpeech
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PoemsUiState(
    val poems: List<PoemItem> = PoemsData.poems,
    val selectedPoem: PoemItem? = null,
    val currentLineIndex: Int = -1,
    val isPlaying: Boolean = false,
    val isRepeatOn: Boolean = false,
    val favoriteIds: Set<Int> = emptySet(),
    val showReward: Boolean = false,
)

@HiltViewModel
class PoemsViewModel @Inject constructor(
    private val tts: KiddoTextToSpeech,
    private val settingsDataStore: SettingsDataStore,
    private val progressRepository: ProgressRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PoemsUiState())
    val uiState: StateFlow<PoemsUiState> = _uiState.asStateFlow()

    private var playJob: Job? = null

    fun openPoem(poemId: Int) {
        val poem = PoemsData.poems.find { it.id == poemId } ?: return
        _uiState.update { it.copy(selectedPoem = poem, currentLineIndex = -1, isPlaying = false) }
    }

    fun playPoem() {
        if (_uiState.value.isPlaying) {
            pausePoem()
        } else {
            startPlay()
        }
    }

    private fun startPlay() {
        _uiState.update { it.copy(isPlaying = true) }
        playJob = viewModelScope.launch {
            val poem = _uiState.value.selectedPoem ?: return@launch
            do {
                for (index in poem.lines.indices) {
                    if (!_uiState.value.isPlaying) break
                    _uiState.update { it.copy(currentLineIndex = index) }
                    if (settingsDataStore.soundEnabled.first()) {
                        tts.speak(poem.lines[index].text)
                    }
                    delay(poem.lines[index].durationMs)
                }
                if (_uiState.value.isPlaying && _uiState.value.isRepeatOn) {
                    _uiState.update { it.copy(currentLineIndex = -1) }
                    delay(800)
                }
            } while (_uiState.value.isPlaying && _uiState.value.isRepeatOn)

            if (_uiState.value.isPlaying) {
                _uiState.update { it.copy(isPlaying = false, currentLineIndex = -1) }
                savePoemProgress(poem)
            }
        }
    }

    private fun pausePoem() {
        playJob?.cancel()
        tts.stop()
        _uiState.update { it.copy(isPlaying = false) }
    }

    fun stopPoem() {
        playJob?.cancel()
        tts.stop()
        _uiState.update { it.copy(isPlaying = false, currentLineIndex = -1) }
    }

    fun toggleRepeat() = _uiState.update { it.copy(isRepeatOn = !it.isRepeatOn) }

    fun speakLine(index: Int) {
        val poem = _uiState.value.selectedPoem ?: return
        val line = poem.lines.getOrNull(index) ?: return
        viewModelScope.launch {
            if (!settingsDataStore.soundEnabled.first()) return@launch
            tts.speak(line.text)
        }
    }

    fun toggleFavorite(poemId: Int) {
        _uiState.update {
            val favs = it.favoriteIds.toMutableSet()
            if (poemId in favs) favs.remove(poemId) else favs.add(poemId)
            it.copy(favoriteIds = favs)
        }
    }

    private fun savePoemProgress(poem: PoemItem) {
        viewModelScope.launch {
            progressRepository.updateProgress(
                moduleId = "poem_${poem.id}",
                moduleName = poem.titleEn,
                completed = poem.lines.size,
                total = poem.lines.size,
                stars = 2,
            )
            settingsDataStore.addStars(2)
            settingsDataStore.addCoins(5)
            _uiState.update { it.copy(showReward = true) }
        }
    }

    fun dismissReward() = _uiState.update { it.copy(showReward = false) }

    override fun onCleared() {
        super.onCleared()
        playJob?.cancel()
        tts.stop()
    }
}
