package com.quickfix.kidszone.data.models

data class WordItem(
    val wordEn: String,
    val wordHi: String,
    val emoji: String,
    val exampleEn: String,
    val category: WordCategory,
)

data class SentenceItem(
    val id: Int,
    val words: List<String>,
    val sentence: String,
    val meaningHi: String,
    val emoji: String,
)

enum class WordCategory(val displayName: String, val emoji: String, val colorHex: Long) {
    BASIC("Basic Words", "📖", 0xFFFF6B6BL),
    FRUITS("Fruits", "🍎", 0xFF06D6A0L),
    ANIMALS("Animals", "🐾", 0xFFFFBE0BL),
    COLORS("Colors", "🌈", 0xFF845EC2L),
    ACTIONS("Actions", "🏃", 0xFFFF6B9DL),
    OPPOSITES("Opposites", "↔️", 0xFF4ECDC4L),
    DAILY("Daily Use", "☀️", 0xFF3D5AF1L),
}

object WordsData {

    val words: List<WordItem> = listOf(
        // BASIC
        WordItem("Apple", "सेब", "🍎", "This is an apple.", WordCategory.BASIC),
        WordItem("Ball", "गेंद", "⚽", "I play with a ball.", WordCategory.BASIC),
        WordItem("Cat", "बिल्ली", "🐱", "The cat is cute.", WordCategory.BASIC),
        WordItem("Dog", "कुत्ता", "🐶", "The dog is happy.", WordCategory.BASIC),
        WordItem("Egg", "अंडा", "🥚", "This is an egg.", WordCategory.BASIC),
        WordItem("Fish", "मछली", "🐟", "The fish swims.", WordCategory.BASIC),
        WordItem("Girl", "लड़की", "👧", "The girl is kind.", WordCategory.BASIC),
        WordItem("Hat", "टोपी", "🎩", "I wear a hat.", WordCategory.BASIC),
        WordItem("Jar", "मर्तबान", "🫙", "The jar is big.", WordCategory.BASIC),
        WordItem("Kite", "पतंग", "🪁", "I fly a kite.", WordCategory.BASIC),
        // FRUITS
        WordItem("Mango", "आम", "🥭", "Mango is sweet.", WordCategory.FRUITS),
        WordItem("Banana", "केला", "🍌", "Banana is yellow.", WordCategory.FRUITS),
        WordItem("Orange", "संतरा", "🍊", "Orange is round.", WordCategory.FRUITS),
        WordItem("Grapes", "अंगूर", "🍇", "Grapes are small.", WordCategory.FRUITS),
        WordItem("Watermelon", "तरबूज", "🍉", "Watermelon is big.", WordCategory.FRUITS),
        WordItem("Strawberry", "स्ट्रॉबेरी", "🍓", "Strawberry is red.", WordCategory.FRUITS),
        WordItem("Pineapple", "अनानास", "🍍", "Pineapple is yellow.", WordCategory.FRUITS),
        WordItem("Cherry", "चेरी", "🍒", "Cherry is tiny.", WordCategory.FRUITS),
        WordItem("Lemon", "नींबू", "🍋", "Lemon is sour.", WordCategory.FRUITS),
        WordItem("Coconut", "नारियल", "🥥", "Coconut is hard.", WordCategory.FRUITS),
        // ANIMALS
        WordItem("Lion", "शेर", "🦁", "The lion roars.", WordCategory.ANIMALS),
        WordItem("Elephant", "हाथी", "🐘", "Elephant is big.", WordCategory.ANIMALS),
        WordItem("Monkey", "बंदर", "🐒", "Monkey climbs trees.", WordCategory.ANIMALS),
        WordItem("Rabbit", "खरगोश", "🐰", "Rabbit hops fast.", WordCategory.ANIMALS),
        WordItem("Tiger", "बाघ", "🐯", "Tiger is striped.", WordCategory.ANIMALS),
        WordItem("Parrot", "तोता", "🦜", "Parrot can talk.", WordCategory.ANIMALS),
        WordItem("Duck", "बतख", "🦆", "Duck swims well.", WordCategory.ANIMALS),
        WordItem("Horse", "घोड़ा", "🐴", "Horse runs fast.", WordCategory.ANIMALS),
        WordItem("Cow", "गाय", "🐄", "Cow gives milk.", WordCategory.ANIMALS),
        WordItem("Butterfly", "तितली", "🦋", "Butterfly is pretty.", WordCategory.ANIMALS),
        // COLORS
        WordItem("Red", "लाल", "🔴", "Red is bright.", WordCategory.COLORS),
        WordItem("Blue", "नीला", "🔵", "Sky is blue.", WordCategory.COLORS),
        WordItem("Green", "हरा", "🟢", "Leaves are green.", WordCategory.COLORS),
        WordItem("Yellow", "पीला", "🟡", "Sun is yellow.", WordCategory.COLORS),
        WordItem("Pink", "गुलाबी", "🌸", "Rose is pink.", WordCategory.COLORS),
        WordItem("Orange", "नारंगी", "🟠", "Carrot is orange.", WordCategory.COLORS),
        WordItem("Purple", "बैंगनी", "🟣", "Grapes are purple.", WordCategory.COLORS),
        WordItem("White", "सफेद", "⚪", "Snow is white.", WordCategory.COLORS),
        WordItem("Black", "काला", "⚫", "Night is black.", WordCategory.COLORS),
        WordItem("Brown", "भूरा", "🟤", "Soil is brown.", WordCategory.COLORS),
        // ACTIONS
        WordItem("Run", "दौड़ना", "🏃", "I can run fast.", WordCategory.ACTIONS),
        WordItem("Jump", "कूदना", "🦘", "Frogs can jump.", WordCategory.ACTIONS),
        WordItem("Eat", "खाना", "🍽️", "I eat food.", WordCategory.ACTIONS),
        WordItem("Sleep", "सोना", "😴", "I sleep at night.", WordCategory.ACTIONS),
        WordItem("Play", "खेलना", "🎮", "Kids love to play.", WordCategory.ACTIONS),
        WordItem("Read", "पढ़ना", "📚", "I read books.", WordCategory.ACTIONS),
        WordItem("Write", "लिखना", "✏️", "I write words.", WordCategory.ACTIONS),
        WordItem("Sing", "गाना", "🎵", "Birds sing songs.", WordCategory.ACTIONS),
        WordItem("Dance", "नाचना", "💃", "I love to dance.", WordCategory.ACTIONS),
        WordItem("Swim", "तैरना", "🏊", "Fish can swim.", WordCategory.ACTIONS),
        // OPPOSITES
        WordItem("Big", "बड़ा", "🐘", "Elephant is big.", WordCategory.OPPOSITES),
        WordItem("Small", "छोटा", "🐭", "Mouse is small.", WordCategory.OPPOSITES),
        WordItem("Hot", "गर्म", "🔥", "Fire is hot.", WordCategory.OPPOSITES),
        WordItem("Cold", "ठंडा", "🧊", "Ice is cold.", WordCategory.OPPOSITES),
        WordItem("Fast", "तेज़", "🏎️", "Car is fast.", WordCategory.OPPOSITES),
        WordItem("Slow", "धीमा", "🐌", "Snail is slow.", WordCategory.OPPOSITES),
        WordItem("Happy", "खुश", "😊", "I am happy.", WordCategory.OPPOSITES),
        WordItem("Sad", "दुखी", "😢", "Don't be sad.", WordCategory.OPPOSITES),
        WordItem("Day", "दिन", "☀️", "Day has sunshine.", WordCategory.OPPOSITES),
        WordItem("Night", "रात", "🌙", "Night has stars.", WordCategory.OPPOSITES),
        // DAILY
        WordItem("Home", "घर", "🏠", "My home is nice.", WordCategory.DAILY),
        WordItem("School", "स्कूल", "🏫", "I go to school.", WordCategory.DAILY),
        WordItem("Water", "पानी", "💧", "Water is needed.", WordCategory.DAILY),
        WordItem("Food", "खाना", "🍱", "Food gives energy.", WordCategory.DAILY),
        WordItem("Book", "किताब", "📚", "I read my book.", WordCategory.DAILY),
        WordItem("Pencil", "पेंसिल", "✏️", "I write with pencil.", WordCategory.DAILY),
        WordItem("Bag", "बैग", "🎒", "My bag is red.", WordCategory.DAILY),
        WordItem("Chair", "कुर्सी", "🪑", "Sit on a chair.", WordCategory.DAILY),
        WordItem("Table", "मेज़", "🪴", "Books are on the table.", WordCategory.DAILY),
        WordItem("Clock", "घड़ी", "🕐", "Clock shows time.", WordCategory.DAILY),
    )

    val sentences: List<SentenceItem> = listOf(
        SentenceItem(1, listOf("This", "is", "an", "apple"), "This is an apple.", "यह एक सेब है।", "🍎"),
        SentenceItem(2, listOf("The", "dog", "is", "happy"), "The dog is happy.", "कुत्ता खुश है।", "🐶"),
        SentenceItem(3, listOf("I", "like", "mangoes"), "I like mangoes.", "मुझे आम पसंद हैं।", "🥭"),
        SentenceItem(4, listOf("The", "cat", "is", "cute"), "The cat is cute.", "बिल्ली प्यारी है।", "🐱"),
        SentenceItem(5, listOf("Birds", "can", "fly"), "Birds can fly.", "पक्षी उड़ सकते हैं।", "🐦"),
        SentenceItem(6, listOf("I", "go", "to", "school"), "I go to school.", "मैं स्कूल जाता हूँ।", "🏫"),
        SentenceItem(7, listOf("Water", "is", "clear"), "Water is clear.", "पानी साफ है।", "💧"),
        SentenceItem(8, listOf("The", "sun", "is", "bright"), "The sun is bright.", "सूरज चमकदार है।", "☀️"),
        SentenceItem(9, listOf("Elephant", "is", "very", "big"), "Elephant is very big.", "हाथी बहुत बड़ा है।", "🐘"),
        SentenceItem(10, listOf("I", "love", "to", "read"), "I love to read.", "मुझे पढ़ना पसंद है।", "📚"),
    )

    fun getByCategory(category: WordCategory) = words.filter { it.category == category }
}
