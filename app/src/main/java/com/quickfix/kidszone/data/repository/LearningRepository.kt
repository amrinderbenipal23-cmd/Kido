package com.quickfix.kidszone.data.repository

import com.quickfix.kidszone.data.models.AlphabetData
import com.quickfix.kidszone.data.models.AnimalData
import com.quickfix.kidszone.data.models.NumberData
import com.quickfix.kidszone.domain.model.Alphabet
import com.quickfix.kidszone.domain.model.Animal
import com.quickfix.kidszone.domain.model.AnimalCategory
import com.quickfix.kidszone.domain.model.NumberItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LearningRepository @Inject constructor() {

    fun getAlphabets(): List<Alphabet> = AlphabetData.englishAlphabets

    fun getAlphabet(letter: Char): Alphabet? =
        AlphabetData.englishAlphabets.find { it.letter == letter }

    fun getAnimals(): List<Animal> = AnimalData.animals

    fun getAnimalsByCategory(category: AnimalCategory): List<Animal> =
        AnimalData.animals.filter { it.category == category }

    fun getNumbers(): List<NumberItem> = NumberData.numbers

    fun getNumber(value: Int): NumberItem? = NumberData.numbers.find { it.value == value }
}
