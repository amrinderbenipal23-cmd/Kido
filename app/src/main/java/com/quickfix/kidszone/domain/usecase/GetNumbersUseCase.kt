package com.quickfix.kidszone.domain.usecase

import com.quickfix.kidszone.data.repository.LearningRepository
import com.quickfix.kidszone.domain.model.NumberItem
import javax.inject.Inject

class GetNumbersUseCase @Inject constructor(
    private val repository: LearningRepository,
) {
    operator fun invoke(): List<NumberItem> = repository.getNumbers()
}
