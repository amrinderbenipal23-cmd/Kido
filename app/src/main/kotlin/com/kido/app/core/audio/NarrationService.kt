package com.kido.app.core.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import java.util.Locale

class NarrationService(context: Context) {

    private val _ready = MutableStateFlow(false)
    val ready: StateFlow<Boolean> = _ready.asStateFlow()

    private val _engineMissing = MutableStateFlow(false)
    val engineMissing: StateFlow<Boolean> = _engineMissing.asStateFlow()

    private val _utteranceDone = MutableSharedFlow<String>(extraBufferCapacity = 8)
    val utteranceDone: SharedFlow<String> = _utteranceDone.asSharedFlow()

    private val tts: TextToSpeech? = try {
        TextToSpeech(context.applicationContext) { status ->
            _ready.value = status == TextToSpeech.SUCCESS
            if (status != TextToSpeech.SUCCESS) _engineMissing.value = true
        }
    } catch (e: Exception) {
        _engineMissing.value = true
        null
    }

    init {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String) {}

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String) {
                _utteranceDone.tryEmit(utteranceId)
            }

            override fun onError(utteranceId: String, errorCode: Int) {
                _utteranceDone.tryEmit(utteranceId)
            }

            override fun onDone(utteranceId: String) {
                _utteranceDone.tryEmit(utteranceId)
            }
        })
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

    suspend fun speakAndWait(text: String, utteranceId: String) {
        if (tts == null || !_ready.value) return
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        utteranceDone.first { it == utteranceId }
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
