package com.quickfix.kidszone.domain.model

data class GameItem(
    val id: Int,
    val title: String,
    val description: String,
    val emoji: String,
    val difficulty: GameDifficulty,
    val type: GameType,
    val color: Long,
    val isUnlocked: Boolean = true,
)

enum class GameDifficulty { EASY, MEDIUM, HARD }

enum class GameType {
    MEMORY_CARDS,
    BALLOON_POP,
    MATCH_ANIMAL,
    FIND_ALPHABET,
    COUNT_OBJECTS,
    SHAPE_MATCHING,
}
