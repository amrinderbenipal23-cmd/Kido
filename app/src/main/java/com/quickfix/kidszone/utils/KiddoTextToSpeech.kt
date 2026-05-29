package com.quickfix.kidszone.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KiddoTextToSpeech @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private var tts: TextToSpeech? = null
    private var isReady = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking

    init {
        initialize()
    }

    private fun initialize() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                tts?.setSpeechRate(0.85f)
                tts?.setPitch(1.2f)
                isReady = true
                setupListener()
            } else {
                Log.e("KiddoTTS", "TTS initialization failed: $status")
            }
        }
    }

    private fun setupListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isSpeaking.value = true
            }
            override fun onDone(utteranceId: String?) {
                _isSpeaking.value = false
            }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _isSpeaking.value = false
            }
        })
    }

    fun speak(text: String, locale: Locale = Locale.US) {
        if (!isReady) return
        tts?.language = locale
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "kiddo_${System.currentTimeMillis()}")
    }

    fun speakHindi(text: String) {
        speak(text, Locale("hi", "IN"))
    }

    fun speakAlphabet(letter: String) {
        speak(letter, Locale.US)
    }

    fun speakWord(word: String) {
        speak(word, Locale.US)
    }

    fun speakPraise(childName: String = "Kiddo") {
        val phrases = listOf(
            "Great job $childName!",
            "Wow, you are so smart!",
            "Excellent! Keep it up!",
            "Amazing! You did it!",
            "Super! You are a star!",
            "Wonderful! You are brilliant!",
        )
        speak(phrases.random(), Locale.US)
    }

    fun speakEncouragement() {
        val phrases = listOf(
            "Try again, you can do it!",
            "Don't give up!",
            "Almost there, keep trying!",
        )
        speak(phrases.random(), Locale.US)
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun shutdown() {
        tts?.shutdown()
        tts = null
        isReady = false
    }
}
