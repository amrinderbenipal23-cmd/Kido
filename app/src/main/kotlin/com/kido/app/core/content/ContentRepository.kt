package com.kido.app.core.content

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class ContentRepository(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun loadAlphabet(languageCode: String): AlphabetPack = withContext(Dispatchers.IO) {
        val path = "content/alphabets/$languageCode.json"
        context.assets.open(path).bufferedReader().use { reader ->
            json.decodeFromString(AlphabetPack.serializer(), reader.readText())
        }
    }

    suspend fun availableLanguages(): List<String> = withContext(Dispatchers.IO) {
        context.assets.list("content/alphabets").orEmpty()
            .filter { it.endsWith(".json") }
            .map { it.removeSuffix(".json") }
            .sorted()
    }
}
