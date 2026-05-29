package com.quickfix.kidszone.data.models

import com.quickfix.kidszone.domain.model.Animal
import com.quickfix.kidszone.domain.model.AnimalCategory

object AnimalData {

    val animals: List<Animal> = listOf(
        Animal(1, "Lion", "शेर", "🦁", "Roar!", AnimalCategory.WILD, "Lions are the only cats that live in groups!", 0xFFFFBE0B),
        Animal(2, "Elephant", "हाथी", "🐘", "Trumpet!", AnimalCategory.WILD, "Elephants never forget!", 0xFF845EC2),
        Animal(3, "Cow", "गाय", "🐄", "Moo!", AnimalCategory.FARM, "Cows give us yummy milk!", 0xFFFFFFFF),
        Animal(4, "Dog", "कुत्ता", "🐶", "Woof!", AnimalCategory.PETS, "Dogs are human's best friend!", 0xFFFF9671),
        Animal(5, "Cat", "बिल्ली", "🐱", "Meow!", AnimalCategory.PETS, "Cats sleep up to 16 hours a day!", 0xFFFF6B6B),
        Animal(6, "Parrot", "तोता", "🦜", "Hello! Hello!", AnimalCategory.BIRDS, "Parrots can copy human speech!", 0xFF00C9A7),
        Animal(7, "Monkey", "बंदर", "🐒", "Ooh ooh ah!", AnimalCategory.WILD, "Monkeys love bananas!", 0xFFFF9671),
        Animal(8, "Tiger", "बाघ", "🐯", "Growl!", AnimalCategory.WILD, "Tigers are excellent swimmers!", 0xFFFF6B9D),
        Animal(9, "Horse", "घोड़ा", "🐴", "Neigh!", AnimalCategory.FARM, "Horses can sleep standing up!", 0xFF845EC2),
        Animal(10, "Duck", "बतख", "🦆", "Quack!", AnimalCategory.BIRDS, "Ducks always look happy!", 0xFFFFBE0B),
        Animal(11, "Fish", "मछली", "🐟", "Blub!", AnimalCategory.SEA, "Fish can breathe underwater!", 0xFF4ECDC4),
        Animal(12, "Frog", "मेंढक", "🐸", "Ribbit!", AnimalCategory.WILD, "Frogs can jump really far!", 0xFF00C9A7),
        Animal(13, "Rabbit", "खरगोश", "🐰", "Squeak!", AnimalCategory.PETS, "Rabbits' teeth never stop growing!", 0xFFFF6B9D),
        Animal(14, "Bear", "भालू", "🐻", "Roar!", AnimalCategory.WILD, "Bears love honey just like Winnie!", 0xFF845EC2),
        Animal(15, "Penguin", "पेंगुइन", "🐧", "Squawk!", AnimalCategory.BIRDS, "Penguins can't fly but they can swim!", 0xFF4ECDC4),
        Animal(16, "Snake", "साँप", "🐍", "Hisss!", AnimalCategory.WILD, "Snakes smell with their tongues!", 0xFF00C9A7),
        Animal(17, "Butterfly", "तितली", "🦋", "Flutter!", AnimalCategory.WILD, "Butterflies taste with their feet!", 0xFFFF6B6B),
        Animal(18, "Hen", "मुर्गी", "🐔", "Cluck!", AnimalCategory.FARM, "Hens lay eggs every day!", 0xFFFFBE0B),
        Animal(19, "Dolphin", "डॉल्फिन", "🐬", "Click click!", AnimalCategory.SEA, "Dolphins are very smart and playful!", 0xFF4ECDC4),
        Animal(20, "Giraffe", "जिराफ", "🦒", "Hum!", AnimalCategory.WILD, "Giraffes have the longest necks!", 0xFFFFBE0B),
    )
}
