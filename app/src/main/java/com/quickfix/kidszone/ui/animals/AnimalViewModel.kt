package com.quickfix.kidszone.ui.animals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickfix.kidszone.data.local.datastore.SettingsDataStore
import com.quickfix.kidszone.data.repository.ProgressRepository
import com.quickfix.kidszone.domain.model.Animal
import com.quickfix.kidszone.domain.model.AnimalCategory
import com.quickfix.kidszone.domain.usecase.GetAnimalsUseCase
import com.quickfix.kidszone.utils.KiddoTextToSpeech
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AnimalUiState(
    val animals: List<Animal> = emptyList(),
    val filteredAnimals: List<Animal> = emptyList(),
    val selectedCategory: AnimalCategory? = null,
    val selectedAnimal: Animal? = null,
    val isQuizMode: Boolean = false,
    val quizQuestion: Animal? = null,
    val quizOptions: List<Animal> = emptyList(),
    val quizResult: Boolean? = null,
    val score: Int = 0,
    val showReward: Boolean = false,
    val playingSound: Int? = null,
)

@HiltViewModel
class AnimalViewModel @Inject constructor(
    private val getAnimalsUseCase: GetAnimalsUseCase,
    private val tts: KiddoTextToSpeech,
    private val settingsDataStore: SettingsDataStore,
    private val progressRepository: ProgressRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnimalUiState())
    val uiState: StateFlow<AnimalUiState> = _uiState.asStateFlow()

    init {
        val animals = getAnimalsUseCase()
        _uiState.update { it.copy(animals = animals, filteredAnimals = animals) }
    }

    fun filterByCategory(category: AnimalCategory?) {
        val animals = _uiState.value.animals
        _uiState.update {
            it.copy(
                selectedCategory = category,
                filteredAnimals = if (category == null) animals else animals.filter { a -> a.category == category },
            )
        }
    }

    fun selectAnimal(animal: Animal) {
        _uiState.update { it.copy(selectedAnimal = animal) }
        speakAnimal(animal)
        saveProgress(animal.id)
    }

    fun clearSelected() = _uiState.update { it.copy(selectedAnimal = null) }

    fun speakAnimal(animal: Animal) {
        viewModelScope.launch {
            if (!settingsDataStore.soundEnabled.first()) return@launch
            _uiState.update { it.copy(playingSound = animal.id) }
            tts.speak("${animal.nameEn}. ${animal.soundDescription}")
            delay(1500)
            _uiState.update { it.copy(playingSound = null) }
        }
    }

    fun startQuiz() {
        val animals = _uiState.value.filteredAnimals.shuffled()
        if (animals.size < 4) return
        val question = animals.first()
        val options = (listOf(question) + animals.drop(1).take(3)).shuffled()
        _uiState.update {
            it.copy(isQuizMode = true, quizQuestion = question, quizOptions = options, quizResult = null)
        }
        tts.speak("Which animal says ${question.soundDescription}?")
    }

    fun answerQuiz(answer: Animal) {
        val correct = _uiState.value.quizQuestion?.id == answer.id
        _uiState.update { it.copy(quizResult = correct, score = if (correct) it.score + 1 else it.score) }
        viewModelScope.launch {
            if (correct) {
                tts.speakPraise()
                delay(1000)
                if (_uiState.value.score >= 5) {
                    settingsDataStore.addStars(2)
                    _uiState.update { it.copy(showReward = true) }
                } else {
                    startQuiz()
                }
            } else {
                tts.speakEncouragement()
                delay(1500)
                startQuiz()
            }
        }
    }

    fun stopQuiz() = _uiState.update { it.copy(isQuizMode = false) }

    fun dismissReward() = _uiState.update { it.copy(showReward = false) }

    private fun saveProgress(animalId: Int) {
        viewModelScope.launch {
            progressRepository.updateProgress("animals", "Animal Learning", animalId, _uiState.value.animals.size, (_uiState.value.score / 5).coerceAtMost(3))
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts.stop()
    }
}
