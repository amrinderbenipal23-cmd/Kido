package com.kido.app.core.profile

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.profileDataStore by preferencesDataStore(name = "kido_profile")

class ChildProfileStore(private val context: Context) {

    private object Keys {
        val name = stringPreferencesKey("child_name")
        val languageCode = stringPreferencesKey("language_code")
        val stars = intPreferencesKey("stars")
        val lettersCompleted = stringSetPreferencesKey("letters_completed")
    }

    val profile: Flow<ChildProfile> = context.profileDataStore.data.map { prefs ->
        ChildProfile(
            name = prefs[Keys.name].orEmpty(),
            languageCode = prefs[Keys.languageCode] ?: "en",
            stars = prefs[Keys.stars] ?: 0,
            lettersCompleted = prefs[Keys.lettersCompleted].orEmpty(),
        )
    }

    suspend fun setName(name: String) {
        context.profileDataStore.edit { it[Keys.name] = name }
    }

    suspend fun setLanguage(code: String) {
        context.profileDataStore.edit { it[Keys.languageCode] = code }
    }

    suspend fun awardStar(letterId: String) {
        context.profileDataStore.edit { prefs ->
            prefs[Keys.stars] = (prefs[Keys.stars] ?: 0) + 1
            prefs[Keys.lettersCompleted] = prefs[Keys.lettersCompleted].orEmpty() + letterId
        }
    }

    suspend fun resetProgress() {
        context.profileDataStore.edit { prefs ->
            prefs.remove(Keys.stars)
            prefs.remove(Keys.lettersCompleted)
        }
    }
}
