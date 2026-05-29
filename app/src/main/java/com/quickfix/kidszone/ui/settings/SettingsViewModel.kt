package com.quickfix.kidszone.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickfix.kidszone.data.local.datastore.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val voiceEnabled: Boolean = true,
    val language: String = "en",
    val childName: String = "Kiddo",
    val adsEnabled: Boolean = true,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                settingsDataStore.soundEnabled,
                settingsDataStore.musicEnabled,
                settingsDataStore.voiceEnabled,
                settingsDataStore.language,
                settingsDataStore.childName,
            ) { sound, music, voice, lang, name ->
                _uiState.update {
                    it.copy(
                        soundEnabled = sound,
                        musicEnabled = music,
                        voiceEnabled = voice,
                        language = lang,
                        childName = name,
                    )
                }
            }.collect()
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.setSoundEnabled(enabled) }
        _uiState.update { it.copy(soundEnabled = enabled) }
    }

    fun setMusicEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.setMusicEnabled(enabled) }
        _uiState.update { it.copy(musicEnabled = enabled) }
    }

    fun setVoiceEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsDataStore.setVoiceEnabled(enabled) }
        _uiState.update { it.copy(voiceEnabled = enabled) }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch { settingsDataStore.setLanguage(lang) }
        _uiState.update { it.copy(language = lang) }
    }

    fun setChildName(name: String) {
        if (name.length <= 20) {
            viewModelScope.launch { settingsDataStore.setChildName(name) }
            _uiState.update { it.copy(childName = name) }
        }
    }
}
