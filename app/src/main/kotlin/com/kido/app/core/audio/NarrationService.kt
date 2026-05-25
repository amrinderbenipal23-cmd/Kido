package com.kido.app.core.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * V0 narration backed by Android TextToSpeech. Real recorded audio will
 * replace this once native-speaker recordings are bundled in
 * `assets/audio/{languageCode}/`. Swap point: [speak].
 */
class NarrationService(context: Context) {

    private val _ready = MutableStateFlow(false)
    val ready: StateFlow<Boolean> = _ready.asStateFlow()

    private val tts: TextToSpeech = TextToSpeech(context.applicationContext) { status ->
        _ready.value = status == TextToSpeech.SUCCESS
    }

    fun setLocale(localeTag: String) {
        if (!_ready.value) return
        val locale = Locale.forLanguageTag(localeTag)
        tts.language = locale
    }

    fun speak(text: String, utteranceId: String = text) {
        if (!_ready.value) return
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stop() {
        tts.stop()
    }

    fun shutdown() {
        tts.stop()
        tts.shutdown()
    }
}
