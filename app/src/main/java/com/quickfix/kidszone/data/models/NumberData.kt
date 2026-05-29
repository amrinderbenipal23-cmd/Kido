package com.quickfix.kidszone.data.models

import com.quickfix.kidszone.domain.model.NumberItem

object NumberData {

    private val colors = listOf(
        0xFFFF6B6B, 0xFF4ECDC4, 0xFFFFBE0B, 0xFFFF6B9D, 0xFF845EC2,
        0xFF00C9A7, 0xFFFF9671, 0xFFD65DB1, 0xFF4ECDC4, 0xFFFFBE0B,
    )

    private val emojis = listOf(
        "1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣",
        "6️⃣", "7️⃣", "8️⃣", "9️⃣", "🔟",
        "⑪", "⑫", "⑬", "⑭", "⑮", "⑯", "⑰", "⑱", "⑲", "⑳",
    )

    private val hindiWords = listOf(
        "एक", "दो", "तीन", "चार", "पाँच", "छः", "सात", "आठ", "नौ", "दस",
        "ग्यारह", "बारह", "तेरह", "चौदह", "पंद्रह", "सोलह", "सत्रह", "अठारह", "उन्नीस", "बीस",
    )

    private val englishWords = listOf(
        "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
        "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen", "Twenty",
    )

    val numbers: List<NumberItem> = (1..20).map { i ->
        NumberItem(
            value = i,
            wordEn = englishWords[i - 1],
            wordHi = hindiWords[i - 1],
            emoji = emojis.getOrElse(i - 1) { "$i" },
            color = colors[(i - 1) % colors.size],
        )
    }
}
