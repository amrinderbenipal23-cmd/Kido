package com.quickfix.kidszone.data.models

data class MultiplicationTable(
    val number: Int,
    val color: Long,
    val emoji: String,
    val rows: List<TableRow>,
)

data class TableRow(
    val multiplier: Int,
    val multiplicand: Int,
    val product: Int,
    val narrationEn: String,
    val narrationHi: String,
)

object TablesData {

    private val tableColors = listOf(
        0xFFFF6B6BL, 0xFF4ECDC4L, 0xFFFFBE0BL, 0xFF845EC2L,
        0xFF06D6A0L, 0xFFFF6B9DL, 0xFF3D5AF1L, 0xFFFF9671L,
        0xFF00C9A7L, 0xFFB24592L, 0xFFFA709AL, 0xFF11998EL,
        0xFF4ECDC4L, 0xFFFF6B35L, 0xFF0077B6L, 0xFF8AC926L,
        0xFFFF4757L, 0xFF845EC2L, 0xFFFFD60AL,
    )

    private val tableEmojis = listOf(
        "🍎", "🌙", "🌟", "🦋", "🍕", "🌈", "🎯", "🚀",
        "🎸", "🌸", "🎨", "🦄", "🎃", "🍦", "🏆", "🐉",
        "🎪", "🌊", "⭐",
    )

    private fun numWord(n: Int) = when (n) {
        1 -> "One"; 2 -> "Two"; 3 -> "Three"; 4 -> "Four"; 5 -> "Five"
        6 -> "Six"; 7 -> "Seven"; 8 -> "Eight"; 9 -> "Nine"; 10 -> "Ten"
        11 -> "Eleven"; 12 -> "Twelve"; 13 -> "Thirteen"; 14 -> "Fourteen"
        15 -> "Fifteen"; 16 -> "Sixteen"; 17 -> "Seventeen"; 18 -> "Eighteen"
        19 -> "Nineteen"; 20 -> "Twenty"; 21 -> "Twenty one"; 22 -> "Twenty two"
        24 -> "Twenty four"; 25 -> "Twenty five"; 27 -> "Twenty seven"
        28 -> "Twenty eight"; 30 -> "Thirty"; 32 -> "Thirty two"; 33 -> "Thirty three"
        35 -> "Thirty five"; 36 -> "Thirty six"; 40 -> "Forty"; 42 -> "Forty two"
        45 -> "Forty five"; 48 -> "Forty eight"; 49 -> "Forty nine"; 50 -> "Fifty"
        54 -> "Fifty four"; 56 -> "Fifty six"; 60 -> "Sixty"; 63 -> "Sixty three"
        64 -> "Sixty four"; 70 -> "Seventy"; 72 -> "Seventy two"; 77 -> "Seventy seven"
        80 -> "Eighty"; 81 -> "Eighty one"; 90 -> "Ninety"; 99 -> "Ninety nine"
        100 -> "One hundred"; 110 -> "One ten"; 120 -> "One twenty"
        130 -> "One thirty"; 140 -> "One forty"; 150 -> "One fifty"
        160 -> "One sixty"; 170 -> "One seventy"; 180 -> "One eighty"
        190 -> "One ninety"; 200 -> "Two hundred"
        else -> n.toString()
    }

    private fun numWordHi(n: Int) = when (n) {
        1 -> "एक"; 2 -> "दो"; 3 -> "तीन"; 4 -> "चार"; 5 -> "पाँच"
        6 -> "छः"; 7 -> "सात"; 8 -> "आठ"; 9 -> "नौ"; 10 -> "दस"
        11 -> "ग्यारह"; 12 -> "बारह"; 13 -> "तेरह"; 14 -> "चौदह"
        15 -> "पंद्रह"; 16 -> "सोलह"; 17 -> "सत्रह"; 18 -> "अठारह"
        19 -> "उन्नीस"; 20 -> "बीस"
        else -> n.toString()
    }

    val tables: List<MultiplicationTable> = (2..20).mapIndexed { index, tableNum ->
        MultiplicationTable(
            number = tableNum,
            color = tableColors[index],
            emoji = tableEmojis[index],
            rows = (1..10).map { mult ->
                val product = tableNum * mult
                TableRow(
                    multiplier = tableNum,
                    multiplicand = mult,
                    product = product,
                    narrationEn = "${numWord(tableNum)} times ${numWord(mult)} equals ${numWord(product)}",
                    narrationHi = "${numWordHi(tableNum)} गुना ${numWordHi(mult)} बराबर ${numWordHi(product)}",
                )
            },
        )
    }
}
