package com.kido.app.core.content

import kotlinx.serialization.Serializable

@Serializable
data class AlphabetPack(
    val languageCode: String,
    val languageName: String,
    val localeTag: String,
    val direction: TextDirection = TextDirection.LTR,
    val letters: List<Letter>,
)

@Serializable
data class Letter(
    val id: String,
    val glyph: String,
    val name: String,
    val word: String,
    val emoji: String,
)

@Serializable
enum class TextDirection { LTR, RTL }
