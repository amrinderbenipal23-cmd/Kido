package com.quickfix.kidszone.utils

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

object AnalyticsManager {

    private val analytics: FirebaseAnalytics by lazy { Firebase.analytics }

    fun logModuleOpened(moduleName: String) {
        analytics.logEvent("module_opened", Bundle().apply {
            putString("module_name", moduleName)
        })
    }

    fun logModuleCompleted(moduleName: String, itemsCompleted: Int, starsEarned: Int) {
        analytics.logEvent("module_completed", Bundle().apply {
            putString("module_name", moduleName)
            putInt("items_completed", itemsCompleted)
            putInt("stars_earned", starsEarned)
        })
    }

    fun logAlphabetLearned(letter: String) {
        analytics.logEvent("alphabet_learned", Bundle().apply {
            putString("letter", letter)
        })
    }

    fun logAnimalTapped(animalName: String) {
        analytics.logEvent("animal_tapped", Bundle().apply {
            putString("animal_name", animalName)
        })
    }

    fun logGameStarted(gameType: String, difficulty: String) {
        analytics.logEvent("game_started", Bundle().apply {
            putString("game_type", gameType)
            putString("difficulty", difficulty)
        })
    }

    fun logGameCompleted(gameType: String, score: Int, timeSeconds: Int) {
        analytics.logEvent("game_completed", Bundle().apply {
            putString("game_type", gameType)
            putInt("score", score)
            putInt("time_seconds", timeSeconds)
        })
    }

    fun logRewardEarned(rewardTitle: String, stars: Int) {
        analytics.logEvent("reward_earned", Bundle().apply {
            putString("reward_title", rewardTitle)
            putInt("stars", stars)
        })
    }

    fun logVoiceInteraction(success: Boolean) {
        analytics.logEvent("voice_interaction", Bundle().apply {
            putBoolean("success", success)
        })
    }

    fun logDrawingCompleted() {
        analytics.logEvent("drawing_completed", Bundle())
    }

    fun logScreenView(screenName: String) {
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        })
    }

    fun setUserProperty(name: String, value: String) {
        analytics.setUserProperty(name, value)
    }
}
