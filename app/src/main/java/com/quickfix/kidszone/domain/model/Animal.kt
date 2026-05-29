package com.quickfix.kidszone.domain.model

data class Animal(
    val id: Int,
    val nameEn: String,
    val nameHi: String,
    val emoji: String,
    val soundDescription: String,
    val category: AnimalCategory,
    val funFact: String,
    val color: Long,
)

enum class AnimalCategory {
    FARM, WILD, BIRDS, SEA, PETS
}
