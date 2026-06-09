package com.quickfix.kidszone.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object AbcLearning : Screen("abc_learning")
    object NumberLearning : Screen("number_learning")
    object AnimalLearning : Screen("animal_learning")
    object Drawing : Screen("drawing")
    object Games : Screen("games")
    object MemoryGame : Screen("memory_game")
    object BalloonGame : Screen("balloon_game")
    object MatchAnimalGame : Screen("match_animal_game")
    object FindAlphabetGame : Screen("find_alphabet_game")
    object CountObjectsGame : Screen("count_objects_game")
    object ShapeMatchGame : Screen("shape_match_game")
    object Rewards : Screen("rewards")
    object ParentDashboard : Screen("parent_dashboard")
    object Settings : Screen("settings")

    // Tables module
    object TablesLearning : Screen("tables_learning")
    object TableDetail : Screen("table_detail/{tableNum}") {
        fun createRoute(tableNum: Int) = "table_detail/$tableNum"
    }
    object TableQuiz : Screen("table_quiz/{tableNum}") {
        fun createRoute(tableNum: Int) = "table_quiz/$tableNum"
    }

    // Words module
    object WordsLearning : Screen("words_learning")
    object WordCategory : Screen("word_category/{category}") {
        fun createRoute(category: String) = "word_category/$category"
    }
    object SentenceMaking : Screen("sentence_making")

    // Stories module
    object StoriesLearning : Screen("stories_learning")
    object StoryReader : Screen("story_reader/{storyId}") {
        fun createRoute(storyId: Int) = "story_reader/$storyId"
    }

    // Poems module
    object PoemsLearning : Screen("poems_learning")
    object PoemPlayer : Screen("poem_player/{poemId}") {
        fun createRoute(poemId: Int) = "poem_player/$poemId"
    }
}
