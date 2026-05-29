package com.quickfix.kidszone.domain.usecase

import com.quickfix.kidszone.data.repository.LearningRepository
import com.quickfix.kidszone.domain.model.Animal
import javax.inject.Inject

class GetAnimalsUseCase @Inject constructor(
    private val repository: LearningRepository,
) {
    operator fun invoke(): List<Animal> = repository.getAnimals()
}
