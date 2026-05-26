package com.kido.app.core.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class NarrationService(context: Context) {

    private val _ready = MutableStateFlow(false)
    val ready: StateFlow<Boolean> = _ready.asStateFlow()

    private val _engineMissing = MutableStateFlow(false)
    val engineMissing: StateFlow<Boolean> = _engineMissing.asStateFlow()

    private val tts: TextToSpeech? = try {
        TextToSpeech(context.applicationContext) { status ->
            _ready.value = status == TextToSpeech.SUCCESS
            if (status != TextToSpeech.SUCCESS) _engineMissing.value = true
        }
    } catch (e: Exception) {
        _engineMissing.value = true
        null
    }

    fun setLocale(localeTag: String): LocaleResult {
        val engine = tts ?: return LocaleResult.NoEngine
        if (!_ready.value) return LocaleResult.NotReady
        val locale = Locale.forLanguageTag(localeTag)
        return when (engine.setLanguage(locale)) {
            TextToSpeech.LANG_MISSING_DATA -> LocaleResult.MissingData
            TextToSpeech.LANG_NOT_SUPPORTED -> LocaleResult.NotSupported
            else -> LocaleResult.Ok
        }
    }

    fun speak(text: String, utteranceId: String = text) {
        if (tts == null || !_ready.value) return
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }

    enum class LocaleResult { Ok, NotReady, MissingData, NotSupported, NoEngine }
}
