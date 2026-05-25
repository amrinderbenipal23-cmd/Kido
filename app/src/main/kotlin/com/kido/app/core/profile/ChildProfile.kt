package com.kido.app.core.profile

data class ChildProfile(
    val name: String,
    val languageCode: String,
    val stars: Int,
    val lettersCompleted: Set<String>,
)
