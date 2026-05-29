package com.quickfix.kidszone.data.models

data class PoemItem(
    val id: Int,
    val titleEn: String,
    val titleHi: String,
    val emoji: String,
    val color: Long,
    val isHindi: Boolean = false,
    val lines: List<PoemLine>,
)

data class PoemLine(
    val text: String,
    val durationMs: Long = 2200L,
)

object PoemsData {
    val poems: List<PoemItem> = listOf(
        PoemItem(
            id = 1, titleEn = "Twinkle Twinkle", titleHi = "तारे की चमक",
            emoji = "⭐", color = 0xFF845EC2L,
            lines = listOf(
                PoemLine("Twinkle, twinkle, little star,"),
                PoemLine("How I wonder what you are!"),
                PoemLine("Up above the world so high,"),
                PoemLine("Like a diamond in the sky."),
                PoemLine("Twinkle, twinkle, little star,"),
                PoemLine("How I wonder what you are!"),
            ),
        ),
        PoemItem(
            id = 2, titleEn = "Johny Johny", titleHi = "जॉनी जॉनी",
            emoji = "👦", color = 0xFFFF6B6BL,
            lines = listOf(
                PoemLine("Johny Johny, yes papa?"),
                PoemLine("Eating sugar? No papa!"),
                PoemLine("Telling lies? No papa!"),
                PoemLine("Open your mouth — Ha ha ha!"),
                PoemLine("Johny Johny, yes papa?"),
                PoemLine("Ha ha ha ha ha ha!"),
            ),
        ),
        PoemItem(
            id = 3, titleEn = "Humpty Dumpty", titleHi = "हम्प्टी डम्प्टी",
            emoji = "🥚", color = 0xFF4ECDC4L,
            lines = listOf(
                PoemLine("Humpty Dumpty sat on a wall,"),
                PoemLine("Humpty Dumpty had a great fall."),
                PoemLine("All the king's horses,"),
                PoemLine("And all the king's men,"),
                PoemLine("Couldn't put Humpty together again!"),
            ),
        ),
        PoemItem(
            id = 4, titleEn = "ABC Song", titleHi = "एबीसी गीत",
            emoji = "🔤", color = 0xFFFFBE0BL,
            lines = listOf(
                PoemLine("A B C D E F G,"),
                PoemLine("H I J K L M N O P,"),
                PoemLine("Q R S — T U V,"),
                PoemLine("W X Y and Z."),
                PoemLine("Now I know my A B Cs,"),
                PoemLine("Next time won't you sing with me!"),
            ),
        ),
        PoemItem(
            id = 5, titleEn = "Rain Rain Go Away", titleHi = "बारिश भागो",
            emoji = "🌧️", color = 0xFF3D5AF1L,
            lines = listOf(
                PoemLine("Rain, rain, go away,"),
                PoemLine("Come again another day."),
                PoemLine("Little Johnny wants to play,"),
                PoemLine("Rain, rain, go away!"),
                PoemLine("Rain, rain, go away,"),
                PoemLine("All the children want to play!"),
            ),
        ),
        PoemItem(
            id = 6, titleEn = "Baa Baa Black Sheep", titleHi = "काली भेड़",
            emoji = "🐑", color = 0xFF06D6A0L,
            lines = listOf(
                PoemLine("Baa, baa, black sheep,"),
                PoemLine("Have you any wool?"),
                PoemLine("Yes sir, yes sir, three bags full!"),
                PoemLine("One for my master,"),
                PoemLine("And one for my dame,"),
                PoemLine("One for the little boy down the lane!"),
            ),
        ),
        PoemItem(
            id = 7, titleEn = "Wheels on the Bus", titleHi = "बस के पहिए",
            emoji = "🚌", color = 0xFFFF9671L,
            lines = listOf(
                PoemLine("The wheels on the bus go round and round,"),
                PoemLine("Round and round, round and round."),
                PoemLine("The wheels on the bus go round and round,"),
                PoemLine("All through the town!"),
                PoemLine("The horn on the bus goes beep beep beep,"),
                PoemLine("All through the town!"),
            ),
        ),
        PoemItem(
            id = 8, titleEn = "Chanda Mama", titleHi = "चंदा मामा",
            emoji = "🌙", color = 0xFFB24592L, isHindi = true,
            lines = listOf(
                PoemLine("चंदा मामा दूर के,"),
                PoemLine("पुए पकाएं बूर के,"),
                PoemLine("आप खाएं थाली में,"),
                PoemLine("मुन्ने को दें प्याली में।"),
                PoemLine("प्याली गई टूट,"),
                PoemLine("मुन्ना गया रूठ!"),
            ),
        ),
        PoemItem(
            id = 9, titleEn = "Lakdi Ki Kathi", titleHi = "लकड़ी की काठी",
            emoji = "🐎", color = 0xFFFA709AL, isHindi = true,
            lines = listOf(
                PoemLine("लकड़ी की काठी, काठी पे घोड़ा,"),
                PoemLine("घोड़े की दुम पे जो मारा हथौड़ा,"),
                PoemLine("दौड़ा दौड़ा दौड़ा घोड़ा,"),
                PoemLine("दुम उठाके दौड़ा!"),
                PoemLine("लकड़ी की काठी, काठी पे घोड़ा!"),
            ),
        ),
        PoemItem(
            id = 10, titleEn = "Jack and Jill", titleHi = "जैक और जिल",
            emoji = "⛰️", color = 0xFF11998EL,
            lines = listOf(
                PoemLine("Jack and Jill went up the hill,"),
                PoemLine("To fetch a pail of water."),
                PoemLine("Jack fell down and broke his crown,"),
                PoemLine("And Jill came tumbling after!"),
                PoemLine("Up Jack got and home did trot,"),
                PoemLine("As fast as he could caper!"),
            ),
        ),
    )
}
