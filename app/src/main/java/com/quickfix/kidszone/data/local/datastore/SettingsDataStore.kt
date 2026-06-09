package com.quickfix.kidszone.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "kiddo_settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val MUSIC_ENABLED = booleanPreferencesKey("music_enabled")
        val LANGUAGE = stringPreferencesKey("language")
        val TOTAL_STARS = intPreferencesKey("total_stars")
        val TOTAL_COINS = intPreferencesKey("total_coins")
        val SCREEN_TIME_MINUTES = longPreferencesKey("screen_time_minutes")
        val SCREEN_TIME_SECONDS = longPreferencesKey("screen_time_seconds")
        val ADS_ENABLED = booleanPreferencesKey("ads_enabled")
        val CHILD_NAME = stringPreferencesKey("child_name")
        val DAILY_REWARD_CLAIMED_DATE = stringPreferencesKey("daily_reward_date")
        val SELECTED_THEME = stringPreferencesKey("selected_theme")
        val VOICE_ENABLED = booleanPreferencesKey("voice_enabled")
        val CURRENT_STREAK = intPreferencesKey("current_streak")
        val LAST_ACTIVE_DATE = stringPreferencesKey("last_active_date")
    }

    val soundEnabled: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[SOUND_ENABLED] ?: true }

    val musicEnabled: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[MUSIC_ENABLED] ?: true }

    val language: Flow<String> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[LANGUAGE] ?: "en" }

    val totalStars: Flow<Int> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[TOTAL_STARS] ?: 0 }

    val totalCoins: Flow<Int> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[TOTAL_COINS] ?: 0 }

    // Screen time is accumulated in seconds (so short sessions still count) and
    // surfaced to the UI as whole minutes.
    val screenTimeMinutes: Flow<Long> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { (it[SCREEN_TIME_SECONDS] ?: 0L) / 60L }

    val currentStreak: Flow<Int> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[CURRENT_STREAK] ?: 0 }

    val adsEnabled: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[ADS_ENABLED] ?: true }

    val childName: Flow<String> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[CHILD_NAME] ?: "Kiddo" }

    val voiceEnabled: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[VOICE_ENABLED] ?: true }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { it[SOUND_ENABLED] = enabled }
    }

    suspend fun setMusicEnabled(enabled: Boolean) {
        context.dataStore.edit { it[MUSIC_ENABLED] = enabled }
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { it[LANGUAGE] = lang }
    }

    suspend fun addStars(count: Int) {
        context.dataStore.edit { prefs ->
            prefs[TOTAL_STARS] = (prefs[TOTAL_STARS] ?: 0) + count
        }
    }

    suspend fun addCoins(count: Int) {
        context.dataStore.edit { prefs ->
            prefs[TOTAL_COINS] = (prefs[TOTAL_COINS] ?: 0) + count
        }
    }

    suspend fun addScreenTime(minutes: Long) {
        context.dataStore.edit { prefs ->
            prefs[SCREEN_TIME_MINUTES] = (prefs[SCREEN_TIME_MINUTES] ?: 0L) + minutes
        }
    }

    /** Accumulate elapsed active time, in seconds. */
    suspend fun addScreenTimeSeconds(seconds: Long) {
        if (seconds <= 0L) return
        context.dataStore.edit { prefs ->
            prefs[SCREEN_TIME_SECONDS] = (prefs[SCREEN_TIME_SECONDS] ?: 0L) + seconds
        }
    }

    /**
     * Update the daily learning streak. Call once when the app comes to the
     * foreground. Returns the new streak value.
     *
     * - Same day as last open → unchanged.
     * - Exactly the next day → streak + 1.
     * - Any longer gap (or first ever open) → streak resets to 1.
     */
    suspend fun updateStreakOnAppOpen(today: String, yesterday: String): Int {
        var newStreak = 1
        context.dataStore.edit { prefs ->
            val last = prefs[LAST_ACTIVE_DATE]
            val current = prefs[CURRENT_STREAK] ?: 0
            newStreak = when (last) {
                today -> current.coerceAtLeast(1)
                yesterday -> current + 1
                else -> 1
            }
            prefs[CURRENT_STREAK] = newStreak
            prefs[LAST_ACTIVE_DATE] = today
        }
        return newStreak
    }

    suspend fun setAdsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[ADS_ENABLED] = enabled }
    }

    suspend fun setChildName(name: String) {
        context.dataStore.edit { it[CHILD_NAME] = name }
    }

    suspend fun setVoiceEnabled(enabled: Boolean) {
        context.dataStore.edit { it[VOICE_ENABLED] = enabled }
    }

    suspend fun setDailyRewardClaimedDate(date: String) {
        context.dataStore.edit { it[DAILY_REWARD_CLAIMED_DATE] = date }
    }

    fun getDailyRewardClaimedDate(): Flow<String> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[DAILY_REWARD_CLAIMED_DATE] ?: "" }
}
