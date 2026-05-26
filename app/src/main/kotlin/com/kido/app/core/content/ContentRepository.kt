package com.kido.app.core.content

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.FileNotFoundException

sealed interface ContentResult<out T> {
    data class Ok<T>(val value: T) : ContentResult<T>
    data class NotFound(val path: String) : ContentResult<Nothing>
    data class ParseError(val cause: Throwable) : ContentResult<Nothing>
}

class ContentRepository(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun loadAlphabet(languageCode: String): ContentResult<AlphabetPack> = withContext(Dispatchers.IO) {
        val path = "content/alphabets/$languageCode.json"
        try {
            val text = context.assets.open(path).bufferedReader().use { it.readText() }
            ContentResult.Ok(json.decodeFromString(AlphabetPack.serializer(), text))
        } catch (e: FileNotFoundException) {
            ContentResult.NotFound(path)
        } catch (e: SerializationException) {
            ContentResult.ParseError(e)
        }
    }

    suspend fun availableLanguages(): List<String> = withContext(Dispatchers.IO) {
        context.assets.list("content/alphabets").orEmpty()
            .filter { it.endsWith(".json") }
            .map { it.removeSuffix(".json") }
            .sorted()
    }
}
