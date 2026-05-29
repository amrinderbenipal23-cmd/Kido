package com.quickfix.kidszone.ui.stories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickfix.kidszone.data.local.datastore.SettingsDataStore
import com.quickfix.kidszone.data.models.StoryItem
import com.quickfix.kidszone.data.models.StoryPage
import com.quickfix.kidszone.data.models.StoriesData
import com.quickfix.kidszone.data.repository.ProgressRepository
import com.quickfix.kidszone.utils.KiddoTextToSpeech
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StoriesUiState(
    val stories: List<StoryItem> = StoriesData.stories,
    val selectedStory: StoryItem? = null,
    val currentPage: StoryPage? = null,
    val currentPageIndex: Int = 0,
    val isAutoNarrating: Boolean = false,
    val isHindi: Boolean = false,
    val showMoral: Boolean = false,
    val showReward: Boolean = false,
    val favoriteIds: Set<Int> = emptySet(),
    val starsEarned: Int = 0,
)

@HiltViewModel
class StoriesViewModel @Inject constructor(
    private val tts: KiddoTextToSpeech,
    private val settingsDataStore: SettingsDataStore,
    private val progressRepository: ProgressRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(StoriesUiState())
    val uiState: StateFlow<StoriesUiState> = _uiState.asStateFlow()

    private var narrateJob: Job? = null

    fun openStory(storyId: Int) {
        val story = StoriesData.stories.find { it.id == storyId } ?: return
        val firstPage = story.pages.firstOrNull()
        _uiState.update {
            it.copy(
                selectedStory = story,
                currentPage = firstPage,
                currentPageIndex = 0,
                showMoral = false,
            )
        }
        narratePage(firstPage)
    }

    fun nextPage() {
        val state = _uiState.value
        val story = state.selectedStory ?: return
        val nextIndex = state.currentPageIndex + 1
        if (nextIndex >= story.pages.size) {
            _uiState.update { it.copy(showMoral = true) }
            narrateMoral(story)
            saveStoryProgress(story)
            return
        }
        val page = story.pages[nextIndex]
        _uiState.update { it.copy(currentPageIndex = nextIndex, currentPage = page) }
        narratePage(page)
    }

    fun previousPage() {
        val state = _uiState.value
        val story = state.selectedStory ?: return
        val prevIndex = (state.currentPageIndex - 1).coerceAtLeast(0)
        val page = story.pages[prevIndex]
        _uiState.update { it.copy(currentPageIndex = prevIndex, currentPage = page, showMoral = false) }
        narratePage(page)
    }

    fun speakCurrentPage() {
        val state = _uiState.value
        narratePage(state.currentPage)
    }

    fun toggleAutoNarrate() {
        if (_uiState.value.isAutoNarrating) stopAutoNarrate() else startAutoNarrate()
    }

    private fun startAutoNarrate() {
        _uiState.update { it.copy(isAutoNarrating = true) }
        narrateJob = viewModelScope.launch {
            val story = _uiState.value.selectedStory ?: return@launch
            while (_uiState.value.isAutoNarrating) {
                val state = _uiState.value
                narratePage(state.currentPage)
                val pageDuration = (state.currentPage?.textEn?.length ?: 50) * 60L + 2000L
                delay(pageDuration)
                if (state.currentPageIndex < story.pages.size - 1) {
                    nextPage()
                } else {
                    stopAutoNarrate()
                    _uiState.update { it.copy(showMoral = true) }
                    narrateMoral(story)
                    saveStoryProgress(story)
                }
            }
        }
    }

    private fun stopAutoNarrate() {
        narrateJob?.cancel()
        tts.stop()
        _uiState.update { it.copy(isAutoNarrating = false) }
    }

    private fun narratePage(page: StoryPage?) {
        page ?: return
        viewModelScope.launch {
            if (!settingsDataStore.soundEnabled.first()) return@launch
            val text = if (_uiState.value.isHindi) page.textHi else page.textEn
            tts.speak(text)
        }
    }

    private fun narrateMoral(story: StoryItem) {
        viewModelScope.launch {
            if (!settingsDataStore.soundEnabled.first()) return@launch
            val moral = if (_uiState.value.isHindi) story.moralHi else story.moralEn
            tts.speak("Moral of the story. $moral")
        }
    }

    fun toggleLanguage() = _uiState.update { it.copy(isHindi = !it.isHindi) }

    fun toggleFavorite(storyId: Int) {
        _uiState.update {
            val favs = it.favoriteIds.toMutableSet()
            if (storyId in favs) favs.remove(storyId) else favs.add(storyId)
            it.copy(favoriteIds = favs)
        }
    }

    private fun saveStoryProgress(story: StoryItem) {
        viewModelScope.launch {
            progressRepository.updateProgress(
                moduleId = "story_${story.id}",
                moduleName = story.titleEn,
                completed = story.pages.size,
                total = story.pages.size,
                stars = 3,
            )
            settingsDataStore.addStars(3)
            settingsDataStore.addCoins(10)
            _uiState.update { it.copy(showReward = true, starsEarned = 3) }
        }
    }

    fun dismissReward() = _uiState.update { it.copy(showReward = false) }

    override fun onCleared() {
        super.onCleared()
        narrateJob?.cancel()
        tts.stop()
    }
}
