package com.quickfix.kidszone.domain.model

data class Progress(
    val moduleId: String,
    val moduleName: String,
    val completedItems: Int,
    val totalItems: Int,
    val stars: Int,
    val lastPlayedTimestamp: Long,
) {
    val percentage: Float get() = if (totalItems > 0) completedItems.toFloat() / totalItems else 0f
}
