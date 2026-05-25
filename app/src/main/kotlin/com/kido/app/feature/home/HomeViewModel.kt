package com.kido.app.feature.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kido.app.KidoApp
import com.kido.app.core.profile.ChildProfile
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as KidoApp

    val profile: StateFlow<ChildProfile> = app.profile.profile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ChildProfile(name = "", languageCode = "en", stars = 0, lettersCompleted = emptySet()),
    )
}
