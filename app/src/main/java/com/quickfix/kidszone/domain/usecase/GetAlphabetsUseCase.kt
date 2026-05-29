package com.quickfix.kidszone.domain.usecase

import com.quickfix.kidszone.data.repository.LearningRepository
import com.quickfix.kidszone.domain.model.Alphabet
import javax.inject.Inject

class GetAlphabetsUseCase @Inject constructor(
    private val repository: LearningRepository,
) {
    operator fun invoke(): List<Alphabet> = repository.getAlphabets()
}
