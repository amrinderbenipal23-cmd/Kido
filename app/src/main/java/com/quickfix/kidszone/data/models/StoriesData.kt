package com.quickfix.kidszone.data.models

data class StoryItem(
    val id: Int,
    val titleEn: String,
    val titleHi: String,
    val emoji: String,
    val color: Long,
    val pages: List<StoryPage>,
    val moralEn: String,
    val moralHi: String,
)

data class StoryPage(
    val pageNumber: Int,
    val emoji: String,
    val textEn: String,
    val textHi: String,
)

object StoriesData {
    val stories: List<StoryItem> = listOf(
        StoryItem(
            id = 1, titleEn = "Thirsty Crow", titleHi = "प्यासा कौआ",
            emoji = "🐦", color = 0xFF4ECDC4L,
            moralEn = "Where there is a will, there is a way.",
            moralHi = "जहाँ चाह वहाँ राह।",
            pages = listOf(
                StoryPage(1, "🐦", "Once upon a time, a thirsty crow flew far and wide looking for water on a hot summer day.", "एक बार एक प्यासा कौआ गर्म गर्मी के दिन पानी की तलाश में दूर-दूर उड़ा।"),
                StoryPage(2, "🏺", "At last he found a pot! But there was only a little water at the bottom. His beak could not reach it.", "अंत में उसे एक घड़ा मिला! पर उसमें नीचे थोड़ा पानी था। उसकी चोंच पानी तक नहीं पहुँच सकी।"),
                StoryPage(3, "🪨", "The clever crow looked around and spotted some pebbles. He had a great idea!", "चालाक कौए ने आसपास देखा और कुछ कंकड़ देखे। उसके मन में एक शानदार विचार आया!"),
                StoryPage(4, "💧", "One by one, the crow dropped pebbles into the pot. The water level slowly rose up.", "कौए ने एक-एक करके कंकड़ घड़े में डाले। पानी का स्तर धीरे-धीरे ऊपर आने लगा।"),
                StoryPage(5, "🎉", "Finally the water reached the top! The crow drank the cool water happily. Smart thinking saved the day!", "आखिरकार पानी ऊपर आ गया! कौए ने खुशी से ठंडा पानी पिया। बुद्धिमानी ने दिन बचा लिया!"),
            ),
        ),
        StoryItem(
            id = 2, titleEn = "Greedy Dog", titleHi = "लालची कुत्ता",
            emoji = "🐕", color = 0xFFFF6B6BL,
            moralEn = "Greed leads to loss.",
            moralHi = "लालच बुरी बला है।",
            pages = listOf(
                StoryPage(1, "🐕", "A hungry dog found a juicy bone. He was very happy and picked it up in his mouth.", "एक भूखे कुत्ते को एक रसीली हड्डी मिली। वह बहुत खुश था और उसने उसे मुँह में उठा लिया।"),
                StoryPage(2, "🌉", "He was crossing a bridge over a river. He looked down and saw his own reflection in the water.", "वह एक नदी पर पुल पार कर रहा था। उसने नीचे देखा और पानी में अपना प्रतिबिंब देखा।"),
                StoryPage(3, "😡", "He thought another dog had a bigger bone! He growled and barked at the reflection.", "उसने सोचा किसी दूसरे कुत्ते के पास बड़ी हड्डी है! उसने प्रतिबिंब पर गुर्राया और भौंका।"),
                StoryPage(4, "💦", "As he opened his mouth to bark, his bone fell into the river with a splash and sank!", "भौंकने के लिए मुँह खोला तो उसकी हड्डी छपाक से नदी में गिर गई और डूब गई!"),
                StoryPage(5, "😢", "The greedy dog was left with nothing. Be grateful for what you have and never be greedy!", "लालची कुत्ते के पास कुछ नहीं रहा। जो मिला है उसका शुक्र करो और कभी लालच मत करो!"),
            ),
        ),
        StoryItem(
            id = 3, titleEn = "Lion and Mouse", titleHi = "शेर और चूहा",
            emoji = "🦁", color = 0xFFFFBE0BL,
            moralEn = "Small acts of kindness are never wasted.",
            moralHi = "छोटी मदद भी काम आती है।",
            pages = listOf(
                StoryPage(1, "🦁", "A mighty lion was sleeping peacefully in the jungle. A tiny mouse was playing nearby.", "एक बड़ा शेर जंगल में आराम से सो रहा था। एक छोटा चूहा पास में खेल रहा था।"),
                StoryPage(2, "😤", "The mouse accidentally ran over the lion's nose! The lion woke up and caught the mouse in his paw.", "चूहा गलती से शेर की नाक पर दौड़ गया! शेर जाग गया और चूहे को अपने पंजे में पकड़ लिया।"),
                StoryPage(3, "🙏", "The mouse begged, 'Please let me go! I promise I will help you someday.' The lion laughed but let him go.", "चूहे ने विनती की, 'कृपया मुझे जाने दो! मैं वादा करता हूँ एक दिन मैं आपकी मदद करूँगा।' शेर हँसा पर उसे जाने दिया।"),
                StoryPage(4, "🕸️", "One day the lion got trapped in a hunter's net. He roared loudly but could not break free.", "एक दिन शेर शिकारी के जाल में फँस गया। वह ज़ोर से दहाड़ा पर आज़ाद नहीं हो सका।"),
                StoryPage(5, "🎉", "The little mouse heard his friend! He chewed through the ropes quickly and freed the lion. True friendship wins!", "छोटे चूहे ने अपने दोस्त की आवाज़ सुनी! उसने जल्दी से रस्सी काट दी और शेर को आज़ाद किया। सच्ची दोस्ती जीती!"),
            ),
        ),
        StoryItem(
            id = 4, titleEn = "Fox and Grapes", titleHi = "लोमड़ी और अंगूर",
            emoji = "🦊", color = 0xFF845EC2L,
            moralEn = "It is easy to despise what you cannot get.",
            moralHi = "न मिले तो बहाना मत बनाओ।",
            pages = listOf(
                StoryPage(1, "🦊", "A hungry fox was wandering through a vineyard. She spotted a bunch of juicy, ripe grapes hanging high on a vine.", "एक भूखी लोमड़ी अंगूर के बगीचे में घूम रही थी। उसने एक बेल पर ऊँचे लटके रसीले पके अंगूर देखे।"),
                StoryPage(2, "🍇", "The grapes looked delicious! The fox jumped up as high as she could. But the grapes were just too high!", "अंगूर बहुत स्वादिष्ट लग रहे थे! लोमड़ी जितनी ऊँची कूद सकती थी, कूदी। पर अंगूर बहुत ऊँचे थे!"),
                StoryPage(3, "😤", "She tried again and again, leaping with all her might. Each time she fell short. She was getting tired.", "उसने बार-बार पूरी ताकत से छलांग लगाई। हर बार कम पड़ गई। वह थकने लगी।"),
                StoryPage(4, "😒", "Finally the fox gave up. She walked away with her nose in the air saying, 'Those grapes are sour anyway! I don't want them!'", "अंत में लोमड़ी हार मान गई। वह नाक ऊँची करके चली गई और बोली, 'वैसे भी वो अंगूर खट्टे होंगे! मुझे नहीं चाहिए!'"),
            ),
        ),
        StoryItem(
            id = 5, titleEn = "Hare and Tortoise", titleHi = "खरगोश और कछुआ",
            emoji = "🐢", color = 0xFF06D6A0L,
            moralEn = "Slow and steady wins the race.",
            moralHi = "धीरे-धीरे रे मना, धीरे सब कुछ होय।",
            pages = listOf(
                StoryPage(1, "🐰", "A hare always boasted about how fast he could run. A quiet tortoise challenged him to a race. Everyone laughed!", "एक खरगोश हमेशा अपनी रफ्तार की डींग मारता था। एक शांत कछुए ने उसे दौड़ की चुनौती दी। सब हँसे!"),
                StoryPage(2, "🏁", "The race began. The hare shot off like a rocket. The tortoise moved slowly and steadily, one step at a time.", "दौड़ शुरू हुई। खरगोश रॉकेट की तरह दौड़ा। कछुआ धीरे-धीरे लेकिन लगातार एक-एक कदम चलता रहा।"),
                StoryPage(3, "😴", "The hare looked back and saw the tortoise was far behind. He thought, 'I have plenty of time!' He sat under a shady tree and fell fast asleep.", "खरगोश ने पीछे देखा और कछुए को बहुत पीछे पाया। उसने सोचा, 'मेरे पास बहुत समय है!' वह एक छायादार पेड़ के नीचे बैठ गया और गहरी नींद में सो गया।"),
                StoryPage(4, "🐢", "The tortoise never stopped. He walked past the sleeping hare slowly but surely. Closer and closer to the finish line!", "कछुआ कभी नहीं रुका। वह सोते खरगोश के पास से धीरे-धीरे लेकिन निश्चित रूप से गुज़रा। फिनिश लाइन के पास और पास!"),
                StoryPage(5, "🏆", "When the hare finally woke up and ran, it was too late! The tortoise had already crossed the finish line and won the race!", "जब खरगोश आखिरकार जागा और दौड़ा, बहुत देर हो गई थी! कछुआ पहले ही दौड़ जीत चुका था!"),
            ),
        ),
        StoryItem(
            id = 6, titleEn = "Golden Egg", titleHi = "सोने का अंडा",
            emoji = "🥚", color = 0xFFFFD60AL,
            moralEn = "Be content with what you have; greed destroys everything.",
            moralHi = "जो है उसमें संतुष्ट रहो; लालच सब नष्ट कर देता है।",
            pages = listOf(
                StoryPage(1, "👨‍🌾", "A farmer had a wonderful goose that laid one golden egg every morning. He sold the eggs and lived happily.", "एक किसान के पास एक अद्भुत हंसिनी थी जो हर सुबह एक सोने का अंडा देती थी। वह अंडे बेचकर खुशी से रहता था।"),
                StoryPage(2, "🥚", "Day by day the farmer became richer. But he grew impatient and greedy. He wanted all the eggs at once!", "दिन-ब-दिन किसान और अमीर होता गया। पर वह अधीर और लालची हो गया। वह एक साथ सारे अंडे चाहता था!"),
                StoryPage(3, "🤔", "He thought, 'Inside this goose there must be hundreds of golden eggs! If I cut her open I will be the richest man!'", "उसने सोचा, 'इस हंसिनी के अंदर सैकड़ों सोने के अंडे होंगे! अगर मैं उसे काटूँ तो सबसे अमीर आदमी बन जाऊँगा!'"),
                StoryPage(4, "😢", "He cut open the goose, but found nothing inside. No golden eggs, no treasure. Just an ordinary goose.", "उसने हंसिनी काटी, पर अंदर कुछ नहीं मिला। न सोने के अंडे, न खजाना। बस एक साधारण हंसिनी।"),
                StoryPage(5, "😭", "The foolish farmer had lost his magical goose forever. Never destroy what gives you happiness in greed for more!", "मूर्ख किसान ने अपनी जादुई हंसिनी हमेशा के लिए खो दी। ज़्यादा के लालच में जो खुशी देता है उसे कभी नष्ट मत करो!"),
            ),
        ),
        StoryItem(
            id = 7, titleEn = "Honest Woodcutter", titleHi = "ईमानदार लकड़हारा",
            emoji = "🪓", color = 0xFF3D5AF1L,
            moralEn = "Honesty is the best policy.",
            moralHi = "ईमानदारी सबसे अच्छी नीति है।",
            pages = listOf(
                StoryPage(1, "🪓", "A poor woodcutter worked hard every day near a river to support his family. His old iron axe was his only treasure.", "एक गरीब लकड़हारा अपने परिवार के लिए नदी के पास हर दिन कड़ी मेहनत करता था। उसकी पुरानी लोहे की कुल्हाड़ी उसका एकमात्र खजाना थी।"),
                StoryPage(2, "😢", "One day while cutting wood, his axe slipped from his hand and fell into the deep river. He sat down and cried bitterly.", "एक दिन लकड़ी काटते समय उसकी कुल्हाड़ी हाथ से फिसलकर गहरी नदी में गिर गई। वह बैठकर फूट-फूटकर रोने लगा।"),
                StoryPage(3, "🧚", "A river fairy appeared before him. She dived into the river and came back with a shining golden axe. 'Is this yours?' she asked kindly.", "एक नदी परी उसके सामने प्रकट हुई। वह नदी में गोता लगाकर चमकती सोने की कुल्हाड़ी लेकर आई। 'क्या यह तुम्हारी है?' उसने दयालुता से पूछा।"),
                StoryPage(4, "✋", "The honest woodcutter shook his head. 'No, that is not mine. My axe was made of plain iron.' The fairy smiled and dived again.", "ईमानदार लकड़हारे ने सिर हिलाया। 'नहीं, यह मेरी नहीं है। मेरी कुल्हाड़ी साधारण लोहे की थी।' परी मुस्कुराई और फिर गोता लगाया।"),
                StoryPage(5, "🎁", "The fairy returned with the iron axe. She was so happy with his honesty that she gave him all three axes as a gift! Honesty always pays!", "परी लोहे की कुल्हाड़ी लेकर लौटी। वह उसकी ईमानदारी से इतनी खुश थी कि उसने उपहार में तीनों कुल्हाड़ियाँ दे दीं! ईमानदारी का हमेशा फल मिलता है!"),
            ),
        ),
        StoryItem(
            id = 8, titleEn = "Monkey and Crocodile", titleHi = "बंदर और मगरमच्छ",
            emoji = "🐊", color = 0xFFFF9671L,
            moralEn = "Use your intelligence to outsmart the wicked.",
            moralHi = "बुद्धि से बुरे लोगों को मात दो।",
            pages = listOf(
                StoryPage(1, "🐒", "A clever monkey lived happily on a berry tree by a river. He became friends with a crocodile who lived in the river.", "एक चालाक बंदर नदी के किनारे जामुन के पेड़ पर खुशी से रहता था। उसकी दोस्ती नदी में रहने वाले एक मगरमच्छ से हो गई।"),
                StoryPage(2, "🍒", "Every day the monkey would share sweet berries with the crocodile. They were the best of friends.", "हर दिन बंदर मगरमच्छ के साथ मीठे जामुन बाँटता था। वे सबसे अच्छे दोस्त थे।"),
                StoryPage(3, "😈", "The crocodile's jealous wife wanted to eat the monkey's heart. She forced her husband to bring the monkey home for dinner.", "मगरमच्छ की ईर्ष्यालु पत्नी बंदर का दिल खाना चाहती थी। उसने अपने पति को बंदर को रात के खाने पर घर लाने को मजबूर किया।"),
                StoryPage(4, "🌊", "The crocodile invited the monkey. In the middle of the river he told the truth. 'My wife wants your heart!'", "मगरमच्छ ने बंदर को आमंत्रित किया। नदी के बीच में उसने सच बता दिया। 'मेरी पत्नी तुम्हारा दिल चाहती है!'"),
                StoryPage(5, "🧠", "The quick-thinking monkey said 'Oh no! I left my heart on the tree! Take me back!' The foolish crocodile turned back and the monkey jumped to safety! Wit saves the day!", "तेज़ दिमाग बंदर ने कहा, 'अरे नहीं! मेरा दिल पेड़ पर छूट गया! मुझे वापस ले चलो!' मूर्ख मगरमच्छ लौट गया और बंदर कूदकर सुरक्षित हो गया! बुद्धि ने दिन बचाया!"),
            ),
        ),
    )
}
