package com.quickfix.kidszone.domain.model

data class Alphabet(
    val letter: Char,
    val capitalLetter: String = letter.uppercaseChar().toString(),
    val smallLetter: String = letter.lowercaseChar().toString(),
    val exampleWordEn: String,
    val exampleWordHi: String,
    val imageEmoji: String,
    val hindiLetter: String,
    val color: Long,
)
