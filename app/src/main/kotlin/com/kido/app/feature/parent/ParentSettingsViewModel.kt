package com.kido.app.feature.parent

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kido.app.KidoApp
import com.kido.app.core.profile.ChildProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ParentSettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as KidoApp

    val profile: StateFlow<ChildProfile> = app.profile.profile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ChildProfile(name = "", languageCode = "en", stars = 0, lettersCompleted = emptySet()),
    )

    private val _availableLanguages = MutableStateFlow<List<String>>(emptyList())
    val availableLanguages: StateFlow<List<String>> = _availableLanguages.asStateFlow()

    init {
        viewModelScope.launch {
            _availableLanguages.value = app.content.availableLanguages()
        }
    }

    fun setName(name: String) = viewModelScope.launch {
        app.profile.setName(name)
    }

    fun setLanguage(code: String) = viewModelScope.launch {
        app.profile.setLanguage(code)
    }

    fun resetProgress() = viewModelScope.launch {
        app.profile.resetProgress()
    }
}
