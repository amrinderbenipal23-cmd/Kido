# Kido — Claude Code Implementation Playbook

**Companion to:** `AUDIT_REPORT.md`
**Purpose:** A sequential, executable plan Claude Code can follow task-by-task to bring Kido from the v0 demo to a state-of-the-art kids' learning product.

---

## How to Use This Playbook

1. **One task at a time.** Hand Claude Code a single task block. Do not paste the whole playbook at once — Claude Code works best with a tight, scoped brief.
2. **Each task is sized for one Claude Code session** (~30 min to 2 hours of work).
3. **Follow the phases sequentially.** Phase 0 unblocks Phase 1, which unblocks Phase 2, etc. Within a phase, tasks are also ordered by dependency.
4. **For each task, the prompt to give Claude Code is:**

   > Read `AUDIT_REPORT.md` and `PLAYBOOK.md`. Execute task **[Task ID]** exactly as specified. Follow the global rules. When done, post a BUILD_COMPLETE report listing files created, files modified, tests passing, and any deviations.

5. **Verify each task before moving on.** Run the listed validation commands. Don't batch.

---

## Global Rules (apply to every task)

1. **Branch per task.** `git checkout -b task/<task-id>-<short-name>` before starting.
2. **One commit per task.** Message: `<task-id>: <one-line summary>`.
3. **Tests must pass.** `./gradlew test connectedAndroidTest` before reporting complete.
4. **Lint must pass.** `./gradlew lint` — no new warnings.
5. **No `TODO`, `FIXME`, or `console.log`-equivalents (`Log.d`) in committed code.**
6. **No new `any`-equivalents.** No `Any` types, no `as Any`, no `@Suppress("UNCHECKED_CAST")` without a comment.
7. **All user-facing strings go to `strings.xml`.** From Phase 0 Task 2 onward this is enforced.
8. **Update `AUDIT_REPORT.md`** with a `[x]` next to the gap this task closes, if applicable.
9. **Compose previews** for every new composable.
10. **Accessibility content descriptions** on every Image, Canvas, IconButton, and tappable surface.
11. **No new dependencies without a one-line justification** in the commit message.

---

# PHASE 0 — STABILIZE (~5 working days)

**Goal:** Fix every embarrassing bug, lay foundations that every later task depends on. After Phase 0, the v0 demo is shippable as a real beta.

---

## Task 0.1 — CI, lint, formatting baseline

**Goal:** Every PR builds, tests, and lints automatically.

**Files to create:**
- `.github/workflows/ci.yml`
- `config/detekt/detekt.yml`
- `.editorconfig`
- `gradle/libs.versions.toml` — add `detekt`, `ktlint` plugin entries

**Steps:**
1. Add detekt + ktlint plugins to root `build.gradle.kts` (apply false) and `app/build.gradle.kts` (apply).
2. Write `ci.yml` with two jobs: `build` (runs `./gradlew assembleDebug`) and `verify` (runs `./gradlew test lint detekt ktlintCheck`).
3. Cache Gradle, set Java 17, set `actions/checkout@v4`, `actions/setup-java@v4`.
4. Pre-commit hook is optional; CI is mandatory.

**Validate:**
- `./gradlew detekt ktlintCheck lint test assembleDebug` passes locally.
- Push branch, confirm GitHub Actions green.

**Files touched:** ~5 new, 2 modified.

---

## Task 0.2 — Migrate every UI string to `strings.xml`

**Goal:** Zero user-facing strings remain in Kotlin source. Unblocks all localization work.

**Files to modify:**
- `app/src/main/res/values/strings.xml` — add all strings
- `app/src/main/kotlin/com/kido/app/feature/home/HomeScreen.kt`
- `app/src/main/kotlin/com/kido/app/feature/game/GameScreen.kt`
- `app/src/main/kotlin/com/kido/app/feature/parent/ParentGateScreen.kt`
- `app/src/main/kotlin/com/kido/app/feature/parent/ParentSettingsScreen.kt`
- `app/src/main/kotlin/com/kido/app/feature/game/GameViewModel.kt` — narration prompts move too

**String keys to add (use this naming):**
```
home_greeting_default        "Hi, friend!"
home_greeting_named          "Hi, %1$s!"
home_stars_count             "You have %1$d ⭐"
home_action_play_alphabets   "Play Alphabets"
home_cd_parent_settings      "Parent settings"

game_title                   "Alphabets"
game_prompt_listen           "Tap the letter you hear"
game_feedback_correct        "Great! %1$s for %2$s"
game_feedback_incorrect      "Try again!"
game_feedback_complete       "All done! Amazing work!"
game_round_indicator         "Round %1$d of %2$d"
game_cd_back_home            "Back home"
game_cd_hear_again           "Hear it again"
game_action_done             "Done!"

narration_prompt              "Tap the letter %1$s"
narration_correct             "Great job! %1$s for %2$s!"
narration_retry               "Try again!"
narration_complete            "Amazing work! You earned %1$d stars!"

parent_gate_title             "Grown-up only"
parent_gate_heading           "For grown-ups"
parent_gate_question          "What is %1$d × %2$d?"
parent_gate_error             "Not quite — try again."
parent_gate_action_continue   "Continue"

parent_settings_title         "Parent settings"
parent_settings_name_section  "Child's name"
parent_settings_name_save     "Save name"
parent_settings_lang_section  "Alphabet language"
parent_settings_lang_current  "Current: %1$s"
parent_settings_lang_hint     "More languages arrive as content packs are added."
parent_settings_progress_section "Progress"
parent_settings_progress_body "Stars earned: %1$d\nLetters completed: %2$d"
parent_settings_reset         "Reset progress"
parent_settings_loading       "Loading…"
```

**Pattern:**
```kotlin
// Old
Text(text = "Tap the letter you hear")

// New
Text(text = stringResource(R.string.game_prompt_listen))

// Old (with format args)
Text(text = "Hi, ${profile.name}!")

// New
Text(text = stringResource(R.string.home_greeting_named, profile.name))
```

For narration (non-composable), pass a `Context` (or inject string resources via Hilt later):
```kotlin
private fun speakPrompt(letter: Letter) {
    val text = app.getString(R.string.narration_prompt, letter.name)
    app.narration.speak(text, utteranceId = "prompt-${letter.id}")
}
```

**Validate:**
- `grep -r "\"[A-Za-z][^\"]*\"" app/src/main/kotlin/com/kido/app/feature/` returns zero matches for user-facing strings (log/debug strings exempt).
- App still runs, all text appears.

---

## Task 0.3 — Fix `MaterialTheme.colorScheme` to use KidoColors

**Goal:** TopAppBar, Buttons (default), TextField focus all reflect the brand palette, not the template purple.

**Files to modify:**
- `app/src/main/kotlin/com/kido/app/ui/theme/Color.kt` — delete `Purple40`/`Purple80`/`Pink40`/`Pink80`/`PurpleGrey40`/`PurpleGrey80`, keep file or delete entirely.
- `app/src/main/kotlin/com/kido/app/ui/theme/Theme.kt` — build the `lightColorScheme` from `KidoColors`.

**Pattern:**
```kotlin
private val LightColorScheme = lightColorScheme(
    primary = KidoColors.Berry,
    onPrimary = Color.White,
    primaryContainer = KidoColors.SoftPurple,
    onPrimaryContainer = KidoColors.DeepPurple,
    secondary = KidoColors.Sky,
    onSecondary = Color.White,
    tertiary = KidoColors.Sunshine,
    onTertiary = KidoColors.DeepPurple,
    background = KidoColors.Cream,
    onBackground = Color(0xFF2A1A4A),
    surface = Color.White,
    onSurface = Color(0xFF2A1A4A),
    error = KidoColors.Cherry,
    onError = Color.White,
)
```

**Validate:**
- `./gradlew installDebug` and visually confirm TopAppBar is Berry, not muted purple.
- Compose preview renders without errors.

---

## Task 0.4 — Lock portrait orientation

**File:** `app/src/main/AndroidManifest.xml`

```xml
<activity
    android:name=".MainActivity"
    android:exported="true"
    android:label="@string/app_name"
    android:screenOrientation="portrait"
    android:configChanges="keyboardHidden|screenSize|smallestScreenSize|screenLayout|orientation"
    android:theme="@style/Theme.Kido">
```

**Validate:** rotate device → app stays portrait.

---

## Task 0.5 — Splash screen (Android 12+ compliant)

**Goal:** No black flash on cold start. Mascot logo briefly visible.

**Steps:**
1. Add `androidx.core:core-splashscreen:1.0.1` to `libs.versions.toml` + `app/build.gradle.kts`.
2. New theme in `res/values/themes.xml`:
```xml
<style name="Theme.Kido.Splash" parent="Theme.SplashScreen">
    <item name="windowSplashScreenBackground">@color/splash_background</item>
    <item name="windowSplashScreenAnimatedIcon">@drawable/ic_launcher_foreground</item>
    <item name="postSplashScreenTheme">@style/Theme.Kido</item>
</style>
```
3. Add `<color name="splash_background">#FF5E35B1</color>` to `colors.xml`.
4. Set `MainActivity` theme to `@style/Theme.Kido.Splash`.
5. In `MainActivity.onCreate()` call `installSplashScreen()` **before** `super.onCreate()`.

**Validate:** Cold launch on a device, observe colored splash with logo.

---

## Task 0.6 — Robust error handling around TTS + content loading

**Goal:** Two crash classes eliminated. App degrades gracefully when TTS engine is missing or JSON is malformed.

**Files to modify:**
- `app/src/main/kotlin/com/kido/app/core/audio/NarrationService.kt`
- `app/src/main/kotlin/com/kido/app/core/content/ContentRepository.kt`
- `app/src/main/kotlin/com/kido/app/feature/game/GameViewModel.kt`

**Patterns:**

*NarrationService:*
```kotlin
class NarrationService(context: Context) {
    private val _ready = MutableStateFlow(false)
    val ready: StateFlow<Boolean> = _ready.asStateFlow()

    private val _engineMissing = MutableStateFlow(false)
    val engineMissing: StateFlow<Boolean> = _engineMissing.asStateFlow()

    private val tts: TextToSpeech? = try {
        TextToSpeech(context.applicationContext) { status ->
            _ready.value = status == TextToSpeech.SUCCESS
            if (status != TextToSpeech.SUCCESS) _engineMissing.value = true
        }
    } catch (e: Exception) {
        _engineMissing.value = true
        null
    }

    fun setLocale(localeTag: String): LocaleResult {
        val engine = tts ?: return LocaleResult.NoEngine
        if (!_ready.value) return LocaleResult.NotReady
        val locale = Locale.forLanguageTag(localeTag)
        return when (engine.setLanguage(locale)) {
            TextToSpeech.LANG_MISSING_DATA -> LocaleResult.MissingData
            TextToSpeech.LANG_NOT_SUPPORTED -> LocaleResult.NotSupported
            else -> LocaleResult.Ok
        }
    }

    enum class LocaleResult { Ok, NotReady, MissingData, NotSupported, NoEngine }
    // speak/stop guarded by `engine?.` and `_ready.value`
}
```

*ContentRepository:*
```kotlin
sealed interface ContentResult<out T> {
    data class Ok<T>(val value: T) : ContentResult<T>
    data class NotFound(val path: String) : ContentResult<Nothing>
    data class ParseError(val cause: Throwable) : ContentResult<Nothing>
}

suspend fun loadAlphabet(languageCode: String): ContentResult<AlphabetPack> = withContext(Dispatchers.IO) {
    val path = "content/alphabets/$languageCode.json"
    try {
        val text = context.assets.open(path).bufferedReader().use { it.readText() }
        ContentResult.Ok(json.decodeFromString(AlphabetPack.serializer(), text))
    } catch (e: FileNotFoundException) {
        ContentResult.NotFound(path)
    } catch (e: SerializationException) {
        ContentResult.ParseError(e)
    }
}
```

*GameViewModel.start():*
```kotlin
fun start() {
    if (started) return
    started = true
    viewModelScope.launch {
        val profile = app.profile.profile.first()
        when (val result = app.content.loadAlphabet(profile.languageCode)) {
            is ContentResult.Ok -> {
                alphabet = result.value
                app.narration.setLocale(result.value.localeTag)
                nextRound(roundNum = 1, starsSoFar = 0)
            }
            is ContentResult.NotFound, is ContentResult.ParseError -> {
                _errorState.value = GameError.ContentUnavailable
            }
        }
    }
}
```

**Validate:**
- Unit test `ContentRepository` with missing file and malformed JSON, assert both return the right error.
- Unit test `GameViewModel` shows error state.

---

## Task 0.7 — `UtteranceProgressListener` to fix audio overlap

**Goal:** No more `delay(1_800)` magic numbers. Game advances when TTS actually finishes.

**Files to modify:**
- `app/src/main/kotlin/com/kido/app/core/audio/NarrationService.kt`
- `app/src/main/kotlin/com/kido/app/feature/game/GameViewModel.kt`

**Pattern:**

*NarrationService — expose completion as a `SharedFlow<String>` (utteranceId):*
```kotlin
private val _utteranceDone = MutableSharedFlow<String>(extraBufferCapacity = 8)
val utteranceDone: SharedFlow<String> = _utteranceDone.asSharedFlow()

init {
    tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
        override fun onStart(utteranceId: String) {}
        override fun onError(utteranceId: String, errorCode: Int) {
            _utteranceDone.tryEmit(utteranceId)
        }
        override fun onDone(utteranceId: String) {
            _utteranceDone.tryEmit(utteranceId)
        }
    })
}

suspend fun speakAndWait(text: String, utteranceId: String) {
    if (tts == null || !_ready.value) return
    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    utteranceDone.first { it == utteranceId }
}
```

*GameViewModel — replace delays:*
```kotlin
if (letter.id == current.currentLetter.id) {
    _state.value = current.copy(phase = GamePhase.Correct)
    viewModelScope.launch {
        app.profile.awardStar(letter.id)
        app.narration.speakAndWait(
            context.getString(R.string.narration_correct, letter.glyph, letter.word),
            "good-${letter.id}"
        )
        val newStars = current.starsThisSession + 1
        if (current.round < current.totalRounds) {
            nextRound(current.round + 1, newStars)
        } else {
            _state.value = current.copy(phase = GamePhase.Complete, starsThisSession = newStars)
            app.narration.speakAndWait(
                context.getString(R.string.narration_complete, newStars),
                "complete"
            )
        }
    }
}
```

**Validate:** Run on a slow emulator (set CPU = 1, ARM); previous version had overlapping audio, now does not.

---

## Task 0.8 — Sampling without replacement for letter rounds

**File:** `app/src/main/kotlin/com/kido/app/feature/game/GameViewModel.kt`

**Pattern:**
```kotlin
private var sessionPool: MutableList<Letter> = mutableListOf()

fun start() {
    // ... after loading pack:
    sessionPool = pack.letters.shuffled().toMutableList()
    nextRound(1, 0)
}

private fun nextRound(roundNum: Int, starsSoFar: Int) {
    val pack = alphabet ?: return
    if (sessionPool.isEmpty()) sessionPool = pack.letters.shuffled().toMutableList()
    val letter = sessionPool.removeFirst()
    val distractors = pack.letters.filter { it.id != letter.id }.shuffled().take(3)
    val choices = (distractors + letter).shuffled()
    // ... rest as before
}
```

**Validate:** Unit test: run 5 rounds, assert all 5 `currentLetter.id` values are distinct.

---

## Task 0.9 — Confirmation dialog for "Reset progress"

**File:** `app/src/main/kotlin/com/kido/app/feature/parent/ParentSettingsScreen.kt`

**Pattern:** add `AlertDialog`:
```kotlin
var showResetDialog by remember { mutableStateOf(false) }

BigButton(
    label = stringResource(R.string.parent_settings_reset),
    onClick = { showResetDialog = true },
    color = KidoColors.Cherry,
)

if (showResetDialog) {
    AlertDialog(
        onDismissRequest = { showResetDialog = false },
        title = { Text(stringResource(R.string.reset_dialog_title)) },
        text = { Text(stringResource(R.string.reset_dialog_body, profile.name.ifBlank { "your child" })) },
        confirmButton = {
            TextButton(onClick = {
                viewModel.resetProgress()
                showResetDialog = false
            }) { Text(stringResource(R.string.reset_dialog_confirm)) }
        },
        dismissButton = {
            TextButton(onClick = { showResetDialog = false }) {
                Text(stringResource(R.string.reset_dialog_cancel))
            }
        },
    )
}
```

Add the four new strings to `strings.xml`.

**Validate:** UI test taps Reset, asserts dialog shows, dismiss does not reset, confirm does.

---

## Task 0.10 — Display friendly language names

**Goal:** Parent settings shows "English", "Hindi" — not "en", "hi".

**Pattern:** Don't just load the code list — load each pack and surface `languageName`.

**File:** `app/src/main/kotlin/com/kido/app/core/content/ContentRepository.kt`

Add:
```kotlin
data class LanguageOption(val code: String, val displayName: String, val localeTag: String)

suspend fun availableLanguageOptions(): List<LanguageOption> = withContext(Dispatchers.IO) {
    val codes = availableLanguages()
    codes.mapNotNull { code ->
        when (val result = loadAlphabet(code)) {
            is ContentResult.Ok -> LanguageOption(code, result.value.languageName, result.value.localeTag)
            else -> null
        }
    }
}
```

**File:** `ParentSettingsViewModel.kt` — replace `availableLanguages` with `availableLanguageOptions`.

**File:** `ParentSettingsScreen.kt` — display `displayName`, fall back to `code`.

**Validate:** Settings shows "English" with a ✓ when selected.

---

## Task 0.11 — Backup rules + manifest hardening

**Files to create:**
- `app/src/main/res/xml/backup_rules.xml`
- `app/src/main/res/xml/data_extraction_rules.xml`

**Manifest:**
```xml
<application
    android:name=".KidoApp"
    android:allowBackup="false"
    android:dataExtractionRules="@xml/data_extraction_rules"
    android:fullBackupContent="@xml/backup_rules"
    android:hasFragileUserData="true"
    ...>
```

Both XML files: define explicit excludes (or set `<full-backup-content><exclude domain="sharedpref" path="kido_profile"/></full-backup-content>` if you keep allowBackup=true).

Recommendation for v0: **allowBackup="false"**. Cloud sync (Phase 3) will replace it.

**Validate:** App still launches; manifest merger reports no errors.

---

## Task 0.12 — ProGuard, signing, release build

**Files to create:**
- `app/proguard-rules.pro` — exists, populate it.
- `keystore.properties` (gitignored) and key generation instructions in `README.md`.

**Pattern (`app/build.gradle.kts`):**
```kotlin
val keystoreProperties = Properties().apply {
    val f = rootProject.file("keystore.properties")
    if (f.exists()) load(f.inputStream())
}

android {
    signingConfigs {
        create("release") {
            storeFile = keystoreProperties["storeFile"]?.let { file(it as String) }
            storePassword = keystoreProperties["storePassword"] as String?
            keyAlias = keystoreProperties["keyAlias"] as String?
            keyPassword = keystoreProperties["keyPassword"] as String?
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

**ProGuard rules:**
```
-keep class com.kido.app.core.content.** { *; }     # serialization
-keep class kotlinx.serialization.** { *; }
-keepclasseswithmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
```

**Validate:** `./gradlew assembleRelease` produces a signed APK; install on a device and run a full session.

---

## Task 0.13 — Real unit tests for `core/`

**Goal:** Replace `ExampleUnitTest` with meaningful coverage.

**Files to create:**
- `app/src/test/kotlin/com/kido/app/core/content/ContentRepositoryTest.kt`
- `app/src/test/kotlin/com/kido/app/core/profile/ChildProfileStoreTest.kt`
- `app/src/test/kotlin/com/kido/app/core/audio/NarrationServiceTest.kt` (limited — TTS is hard to mock)
- `app/src/test/kotlin/com/kido/app/feature/game/GameViewModelTest.kt`
- `app/src/test/kotlin/com/kido/app/feature/parent/ParentGateViewModelTest.kt`

**Deps to add (`libs.versions.toml`):**
- `kotlinx-coroutines-test`
- `mockk`
- `turbine`
- `androidx.arch.core:core-testing`

**Minimum test cases per file:**

*ContentRepositoryTest:* loads en.json, missing file returns NotFound, malformed JSON returns ParseError.

*ChildProfileStoreTest:* awardStar increments stars and adds letter; resetProgress clears stars + letters but preserves name and language; setLanguage round-trips.

*GameViewModelTest:* (use Robolectric or refactor to take services via constructor) start → asking phase → correct choice → correct phase + star awarded; incorrect choice → incorrect phase → back to asking; 5 rounds completes → Complete phase.

*ParentGateViewModelTest:* `verify(correct)` true, `verify("abc")` false, `verify("")` false.

**Refactor required:** `GameViewModel` and `HomeViewModel` should take their dependencies via constructor (`Profile`, `Content`, `Narration` interfaces) rather than reaching into `KidoApp`. This makes them testable. Wire via a `viewModelFactory { ... }` until Hilt lands in Task 0.15.

**Validate:** `./gradlew test` shows ≥ 15 passing tests, 0 failing.

---

## Task 0.14 — Compose UI tests

**Files to create:**
- `app/src/androidTest/kotlin/com/kido/app/feature/parent/ParentGateScreenTest.kt`
- `app/src/androidTest/kotlin/com/kido/app/feature/game/GameScreenTest.kt`
- `app/src/androidTest/kotlin/com/kido/app/feature/home/HomeScreenTest.kt`

**Test cases:**
- *ParentGateScreenTest:* enters correct answer → `onPassed` invoked; wrong answer → error text visible.
- *GameScreenTest:* loading state renders; with state, all 4 tiles visible; tapping correct tile transitions to correct phase.
- *HomeScreenTest:* greeting renders default when name blank; with profile name renders "Hi, X!"; Play button click invokes onPlay.

**Pattern:**
```kotlin
@get:Rule val composeRule = createComposeRule()

@Test fun correctAnswerPasses() {
    var passed = false
    composeRule.setContent {
        KidoTheme { ParentGateScreen(onPassed = { passed = true }, onCancel = {},
            viewModel = ParentGateViewModelFake(answer = 42)) }
    }
    composeRule.onNodeWithText("What is 6 × 7?").assertExists()
    composeRule.onNode(hasSetTextAction()).performTextInput("42")
    composeRule.onNodeWithText("Continue").performClick()
    assertTrue(passed)
}
```

**Validate:** `./gradlew connectedAndroidTest` on a running emulator passes.

---

## Task 0.15 — Hilt dependency injection

**Goal:** Kill the `application as KidoApp` cast in every ViewModel.

**Steps:**
1. Add `hilt-android`, `hilt-compiler`, `hilt-navigation-compose` to `libs.versions.toml` + `app/build.gradle.kts` + KSP plugin.
2. `@HiltAndroidApp` on `KidoApp`. Delete the `lateinit var` service-locator fields.
3. New `core/di/AppModule.kt` providing `NarrationService`, `ContentRepository`, `ChildProfileStore` as `@Singleton`.
4. ViewModels: `@HiltViewModel class GameViewModel @Inject constructor(...) : ViewModel()`. Drop `AndroidViewModel`.
5. In screens: `hiltViewModel()` instead of `viewModel()`.
6. Update tests to use `HiltAndroidRule`.

**Validate:** App launches, all 4 screens work, existing tests pass.

---

## Task 0.16 — Logging + crash reporting

**Steps:**
1. Add **Timber** (`com.jakewharton.timber:timber:5.0.1`) for logging.
2. Plant in `KidoApp.onCreate()`:
```kotlin
if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
else Timber.plant(CrashlyticsTree())
```
3. Add **Firebase Crashlytics** (defer Firebase project setup to Task 3.1 — for now, set up SDK with a placeholder google-services.json so the build compiles; do not enable crash collection until COPPA flow is in place).
4. Replace any future `Log.d` calls with `Timber.d`.

**Validate:** Force a crash in debug, see it logged. Stub Crashlytics until Phase 3.

---

## Task 0.17 — Accessibility content descriptions

**Files to modify:**
- `ui/components/LetterTile.kt` — add `contentDescription`
- `ui/components/Mascot.kt` — add `contentDescription`
- `ui/components/Star.kt` — `Modifier.semantics { contentDescription = ... }`

**Pattern:**
```kotlin
fun LetterTile(
    glyph: String,
    onClick: () -> Unit,
    contentDescription: String,   // <-- new required param
    ...
) {
    Surface(
        modifier = modifier
            .semantics { this.contentDescription = contentDescription; role = Role.Button }
            ...
    ) { ... }
}
```

Callsite:
```kotlin
LetterTile(
    glyph = letter.glyph,
    contentDescription = stringResource(R.string.game_cd_letter_tile, letter.name),
    ...
)
```

**Validate:** Enable TalkBack on a device, navigate the Game screen, hear each tile announce its letter.

---

## Task 0.18 — Real font for kid literacy

**Goal:** Replace system default with an early-literacy font (single-story `a`, single-loop `g`).

**Recommendation:** **Andika** (open source, designed for literacy) or **Atkinson Hyperlegible** (open source, max-legibility).

**Steps:**
1. Drop `Andika-Regular.ttf` and `Andika-Bold.ttf` in `app/src/main/res/font/`.
2. Add `font/andika.xml` font family.
3. `ui/theme/Type.kt`: define a `KidoFontFamily`, set it on every typography token.
4. Verify `LetterTile`'s `fontSize = 72.sp` uses the new family (it will, via MaterialTheme).

**Validate:** Letter "a" in the tile is single-story (not the system double-story `a`).

---

## Task 0.19 — First-run onboarding

**Goal:** If `profile.name.isBlank()`, the first time the app launches show a friendly "What's your child's name?" screen.

**Files to create:**
- `feature/onboarding/OnboardingScreen.kt`
- `feature/onboarding/OnboardingViewModel.kt`

**Navigation:** `KidoNavHost` — `startDestination` becomes `Onboarding` when profile name is blank, else `Home`. Use a `SplashViewModel` that reads profile once and exposes the start route.

**UX:** mascot waves, two fields (kid's name, language picker), big "Let's play!" button.

**Validate:** Fresh install → onboarding shown. Set name → Home shown. Reinstall confirms.

---

## Task 0.20 — Star fill animation + tile bounce upgrade

**Goal:** Cheap dopamine for the kid.

**Files to modify:**
- `ui/components/Star.kt` — `animateFloatAsState` on scale when transitioning from empty to filled, with a 1.2× overshoot.
- `ui/components/LetterTile.kt` — increase squash from 0.92f to 0.85f, restore Material ripple (`indication = ripple()`), add a `success-flash` animation when the tile is the correct answer (caller passes a `flash: Boolean` flag).

**Validate:** Run the game; correct answer makes the tile bounce + flash, complete screen makes all 5 stars pop in sequence.

---

### Phase 0 Definition of Done

- [ ] All 20 tasks merged.
- [ ] `./gradlew test connectedAndroidTest lint detekt ktlintCheck` green.
- [ ] No hardcoded user-facing strings anywhere in Kotlin.
- [ ] Cold-start no longer flashes black.
- [ ] Reset progress requires confirmation.
- [ ] Crash reports flow (stubbed) into Crashlytics.
- [ ] 25+ unit tests + 6+ UI tests passing.
- [ ] Hilt fully wired; no `application as KidoApp` casts remain.
- [ ] Release APK is signed and shrunk.
- [ ] First-run shows onboarding.

---

# PHASE 1 — REAL ASSETS & LOCALIZATION (~3 weeks)

**Goal:** Ship two more languages, swap the placeholder mascot and TTS for real assets, polish UI animation.

---

## Task 1.1 — Hindi locale + content pack

**Files to create:**
- `app/src/main/res/values-hi/strings.xml` — translated UI strings
- `app/src/main/assets/content/alphabets/hi.json` — vowels + consonants
- `app/src/main/assets/audio/hi/*.wav` — placeholder for v0.Y; for now, use TTS fallback

**Schema additions to `AlphabetPack` / `Letter`:**
```kotlin
@Serializable data class Letter(
    val id: String,
    val glyph: String,
    val name: String,
    val word: String,
    val emoji: String,
    val phoneme: String? = null,         // NEW — IPA pronunciation
    val audioFile: String? = null,       // NEW — filename in assets/audio/{lang}/
)
```

**Validate:** Launch app, switch language to Hindi in parent settings, return to Home, all UI in Hindi, narration speaks Hindi letters.

---

## Task 1.2 — Spanish locale + content pack

Identical structure to Task 1.1, with `values-es/strings.xml` and `es.json`.

---

## Task 1.3 — Recorded narration asset pipeline

**Goal:** When `letter.audioFile` is non-null, play the file instead of TTS.

**Files to modify:**
- `core/audio/NarrationService.kt` — add `playAudioFile(path: String, utteranceId: String)` using `MediaPlayer` or `SoundPool`. Falls back to TTS if file missing.
- `GameViewModel.kt` — call `playAudioFile` when `letter.audioFile != null`, else current TTS path.

**Validate:** Drop a sample `a.wav` in `assets/audio/en/`, set `audioFile: "a.wav"` for letter A, verify the recording plays instead of TTS.

---

## Task 1.4 — Lottie mascot

**Goal:** Replace the procedural Canvas mascot with a real animated character.

**Steps:**
1. Source / commission a Lottie file with at least 3 named segments: `idle`, `cheer`, `think`.
2. Add `com.airbnb.android:lottie-compose:6.4.0` to dependencies.
3. Refactor `ui/components/Mascot.kt`:
```kotlin
@Composable
fun Mascot(modifier: Modifier = Modifier, mood: MascotMood = MascotMood.Happy) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("mascot/kido.lottie.json"))
    val clipSpec = when (mood) {
        MascotMood.Happy -> LottieClipSpec.Marker("idle")
        MascotMood.Cheering -> LottieClipSpec.Marker("cheer")
        MascotMood.Thinking -> LottieClipSpec.Marker("think")
    }
    LottieAnimation(composition = composition, iterations = LottieConstants.IterateForever,
        clipSpec = clipSpec, modifier = modifier)
}
```
4. Keep the old procedural mascot as `MascotFallback` for screenshot tests / when Lottie fails.

**Validate:** Mascot animates idle on Home, cheers on correct answer, thinks on incorrect.

---

## Task 1.5 — App icon redesign

Replace the placeholder `K`-glyph foreground with a real adaptive icon (foreground + background + monochrome layer for Android 13+).

**Files to create:**
- `res/mipmap-anydpi-v33/ic_launcher.xml` (monochrome support)
- `res/drawable/ic_launcher_monochrome.xml`
- Refreshed `ic_launcher_foreground.xml`

---

### Phase 1 Definition of Done

- [ ] English, Hindi, Spanish — all working end-to-end (UI + content + narration).
- [ ] Lottie mascot animating in 3 moods.
- [ ] Recorded audio plays when present, TTS fallback otherwise.
- [ ] New app icon shipped.
- [ ] All Phase 0 tests still green.

---

# PHASE 2 — ADAPTIVE ENGINE & NEW MODULES (~6 weeks)

**Goal:** Build the moat. Track per-letter mastery, adapt the next round, support multiple kids, add the second subject (Numbers 1–10), introduce streaks and sticker book.

---

## Task 2.1 — Add Room

Add `androidx.room:room-runtime`, `room-ktx`, `room-compiler` (KSP). Bump targetSdk if needed. New `core/db/KidoDatabase.kt` empty for now.

---

## Task 2.2 — Schema design

**Entities:**
```kotlin
@Entity data class Child(
    @PrimaryKey val id: String,           // UUID
    val name: String,
    val avatarId: String?,
    val createdAt: Long,
    val activeLanguageCode: String,
)

@Entity data class Attempt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val childId: String,
    val subjectId: String,                // "alphabet:en", "numbers:en"
    val itemId: String,                   // "en_a", "num_3"
    val isCorrect: Boolean,
    val responseTimeMs: Long,
    val timestamp: Long,
)

@Entity data class Mastery(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val childId: String,
    val subjectId: String,
    val itemId: String,
    val masteryScore: Double,              // 0.0–1.0, BKT
    val attemptsCount: Int,
    val lastSeenAt: Long,
)

@Entity data class StreakSnapshot(
    @PrimaryKey val childId: String,
    val currentStreak: Int,
    val longestStreak: Int,
    val lastActiveDate: String,            // YYYY-MM-DD
)
```

**DAOs:** standard CRUD + analytical queries (last 7 days attempts, mastery distribution, weakest 5 items).

---

## Task 2.3 — Data migration from DataStore to Room

**Goal:** Existing v0 users don't lose their stars.

**Steps:**
1. On first Room init: read DataStore profile, create a default `Child` row, port `stars` and `lettersCompleted` into seeded `Mastery` rows (score = 1.0 for completed letters).
2. After migration, mark a flag in DataStore (`migration_v1_done = true`) to avoid re-running.
3. Keep DataStore for non-profile state (current child id, locale-only flags).

**Validate:** Install v0.X, accumulate stars; upgrade in-place to v1, confirm stars preserved.

---

## Task 2.4 — Multi-profile UI

**New screens:**
- `feature/profile/ProfilePickerScreen.kt` — show all children, "Add child" button.
- `feature/profile/AddChildScreen.kt` — name + avatar picker.

**Navigation:** picker becomes the start destination (or Home if there's a "current child" selected and the parent has enabled "skip picker").

---

## Task 2.5 — Mastery engine

**File:** `core/learning/MasteryEngine.kt`

**Algorithm (start simple):** Elo-style rolling update per item.
```
On correct: score += learningRate * (1 - score)
On wrong:   score -= learningRate * score
learningRate = 0.15
```

Later: replace with BKT (Bayesian Knowledge Tracing) using fixed parameters (slip=0.1, guess=0.2, transit=0.1, prior=0.3).

**Surface a `MasteryEngine.nextItem(childId, subjectId): Item` that:**
1. Picks 70% from items with score in (0.3, 0.7) — the productive struggle zone.
2. 20% from items not seen in 7+ days — spaced repetition.
3. 10% from items with score < 0.3 — confidence rebuilding.

---

## Task 2.6 — Subject abstraction

**Interfaces (`core/subject/`):**
```kotlin
interface Subject {
    val id: String
    val displayName: String
    val items: List<SubjectItem>
}

interface SubjectItem { val id: String; val displayLabel: String }

interface RoundProducer {
    suspend fun produceRound(childId: String, subject: Subject): Round
}

data class Round(
    val target: SubjectItem,
    val distractors: List<SubjectItem>,
    val promptText: String,
    val promptAudioId: String,
)
```

**Refactor:** `GameViewModel` becomes a generic `ActivityRunner` that takes a `Subject` and a `RoundProducer`. The alphabet pack adapts via `AlphabetSubject(pack)`.

---

## Task 2.7 — Numbers 1–10 module

Build using the new abstractions. Should be ~150 lines of new code (the abstractions do the heavy lifting). Add UI tile variant that shows the numeral + a visual count (e.g., 3 → "3" with three apples below).

**Validate:** Home screen lists "Alphabets" and "Numbers 1–10"; tapping either launches the same `ActivityRunner` with different content.

---

## Task 2.8 — Adaptive selection in production

Wire `MasteryEngine.nextItem()` into the `RoundProducer` for both subjects. Verify in logs that the same item rarely appears twice if mastered, and frequently if struggling.

---

## Task 2.9 — Streak tracking + daily goal

**Files:**
- `core/streak/StreakTracker.kt`
- UI: streak badge on Home screen ("🔥 3 day streak").
- Forgiveness rule: missing one day reduces streak by half, not to zero (kid-friendly).

---

## Task 2.10 — Sticker book

**Goal:** Visual reward collection.

- Every letter / number mastered to score ≥ 0.95 unlocks a sticker.
- New screen `feature/stickers/StickerBookScreen.kt` displays a grid of unlocked / locked stickers.
- On unlock, show a celebration overlay (Lottie burst).

---

## Task 2.11 — Parent dashboard

**File:** `feature/parent/ParentDashboardScreen.kt` (behind the existing parent gate).

**Surfaces:**
- This week's time-on-task.
- Items mastered this week.
- Weakest 3 items right now (with names, not IDs).
- A "share progress" button (creates a PNG via Compose `captureToImage`).

---

### Phase 2 Definition of Done

- [ ] Room migration shipped, no data loss for existing users.
- [ ] Two or more children can coexist on one device, separate progress.
- [ ] Adaptive selection is observable in `Mastery` rows and in the UI (struggling items reappear).
- [ ] Numbers 1–10 module shipped using the generic abstractions.
- [ ] Streak + sticker book live.
- [ ] Parent dashboard live.

---

# PHASE 3 — CLOUD (~8 weeks)

**Goal:** Cross-device sync, downloadable content, paid subscription, parent-only notifications.

---

## Task 3.1 — Firebase project + COPPA-safe config

1. Create Firebase project, register app, drop `google-services.json` in `app/`.
2. Enable: Auth (email/password + Google), Firestore, Storage, Remote Config, Cloud Messaging, Crashlytics, Analytics.
3. **Disable** Analytics auto-collection until parent consent: `setAnalyticsCollectionEnabled(false)` in `KidoApp`.
4. Configure default Remote Config values for all feature flags.

---

## Task 3.2 — Parent account (email auth)

- New `feature/account/AccountScreen.kt` behind the parent gate.
- Sign up / sign in / sign out via Firebase Auth.
- Only the parent has an account; children are subdocuments under the parent's doc.

---

## Task 3.3 — Firestore sync layer

**Schema:**
```
/parents/{parentId}
  email, createdAt, plan, locale
  /children/{childId}
    name, avatarId, activeLanguageCode, createdAt
    /mastery/{itemId}
      subjectId, score, attemptsCount, lastSeenAt
    /attempts/{attemptId}
      subjectId, itemId, isCorrect, timestamp
    /streak/current
      currentStreak, longestStreak, lastActiveDate
```

**Sync engine:** Room is the source of truth on-device. A `SyncWorker` (WorkManager) pushes local deltas hourly, pulls remote deltas on app start. Conflict resolution: last-write-wins on simple fields, max() on mastery scores.

---

## Task 3.4 — Downloadable content packs

**Goal:** A 70 MB app on Play Store, with content downloaded on demand.

1. Move bundled language packs to Firebase Storage under `content/alphabets/{code}-{version}.zip`.
2. Strip everything except English from `app/src/main/assets/` in release builds (keep English as default).
3. New `ContentDownloader` service: download → verify checksum → unzip into `filesDir/content/alphabets/`.
4. `ContentRepository` falls back to bundled if download not present.
5. Parent settings: "Manage languages" screen with download / delete per language.

---

## Task 3.5 — Remote Config & feature flags

Flags to ship: `enable_streaks`, `enable_sticker_book`, `numbers_module_visible`, `subscription_enabled`, `free_trial_days`.

Wrap every flag-gated UI in a `FeatureFlag` composable.

---

## Task 3.6 — Push notifications (parent-only)

- FCM topic per parent: `parent_{parentId}`.
- Server-driven (Cloud Functions) "weekly summary" notification: "Riya learned 4 letters this week!"
- **No notifications to or about the child directly. Never use notifications to bring the child back to the app.** (Google Play Families requirement.)

---

## Task 3.7 — Subscription (Play Billing)

1. Add `com.android.billingclient:billing-ktx`.
2. Three plans: monthly, yearly, family (lifetime).
3. Free tier: alphabet English-only, 5 rounds/day cap.
4. Paid tier: all languages, unlimited rounds, all subjects, stickers, dashboard.
5. Family Library compatible.
6. **All paywall UI behind parent gate**, no purchase buttons reachable by child.

---

## Task 3.8 — Network security & privacy

- `network_security_config.xml` — pin Firebase certs in production.
- `data_extraction_rules.xml` — explicit allowlist (only the room db file).
- Disable cleartext traffic.

---

### Phase 3 Definition of Done

- [ ] Parent can sign up, sign in, and see their child's progress on a second device.
- [ ] Downloading a new language pack works and is gated by parent gate.
- [ ] Subscription purchase, restore, and cancel work.
- [ ] Weekly summary push notifications fire correctly.
- [ ] No analytics collected before parent consent.

---

# PHASE 4 — LAUNCH (~4 weeks)

---

## Task 4.1 — Privacy policy + COPPA consent

- Draft a privacy policy (hire a lawyer for sign-off — non-negotiable for kids' apps).
- Host on a public URL.
- In-app: first launch shows a parent-facing consent screen with the policy summary and an explicit checkbox. Without consent, the app runs in "no analytics, no sync, local only" mode.

---

## Task 4.2 — GDPR-K consent

For EU users (detect via locale + IP), additional explicit consent flow per article 8 GDPR-K. Without consent for under-13 processing, no data leaves the device.

---

## Task 4.3 — Data Safety form

Fill the Play Console Data Safety form. Required disclosures: name, email (parent only), usage data (with consent), device identifiers (none), location (none), purchases (Play Billing tokens only).

---

## Task 4.4 — Play Families certification

Submit to the Designed for Families program. Pre-flight checklist:
- Age targeting: 3–5.
- No third-party ads of any kind (or only DFF-compliant ad networks — recommend zero ads).
- No persistent identifiers.
- Parent gate on all external links and purchases.
- Content rated 3+.
- Designed-for-Children attestation.

---

## Task 4.5 — Tablet layouts + responsive design

- Adaptive layouts for sw600dp and sw720dp.
- 3- and 4-column tile grids on tablet.
- Test on at least one 7" and one 10" form factor.

---

## Task 4.6 — Accessibility audit

Run with TalkBack on every screen, switch control on every screen, low-vision testing (large fonts, colorblind sim). File and fix every issue.

---

## Task 4.7 — Performance pass

- Cold-start to interactive < 1.5 s on a Pixel 6a.
- APK size < 25 MB.
- 60 fps on all animations.
- No leaks on `LeakCanary`.
- `Macrobenchmark` baseline profile generated.

---

## Task 4.8 — Soft launch + iteration

- Soft launch in 2 markets (e.g., India + Mexico) for 4 weeks.
- Monitor Crashlytics, Analytics retention curves, parent reviews.
- Iterate before global launch.

---

### Phase 4 Definition of Done

- [ ] Approved on Google Play Families.
- [ ] Privacy policy live and linked from app.
- [ ] Data Safety form complete and accurate.
- [ ] Two soft-launch markets running.

---

# Appendix A — Standard Claude Code Brief Template

Use this template every time you hand a task to Claude Code. Replace the placeholders.

```
PROJECT: Kido
TASK ID: <e.g., 0.7>
TASK NAME: <e.g., UtteranceProgressListener>

CONTEXT:
- Read AUDIT_REPORT.md and PLAYBOOK.md.
- The task block in PLAYBOOK.md is the authoritative spec.

GOAL: <one sentence from the playbook>

FILES IN SCOPE:
- <paste from playbook>

FILES OUT OF SCOPE (DO NOT TOUCH):
- Anything not in the in-scope list.

CONSTRAINTS:
- Follow global rules in PLAYBOOK.md.
- One commit, one branch, named task/<id>-<slug>.
- All tests must pass.
- No new TODO/FIXME.
- Compose previews for new composables.
- Accessibility content descriptions on every tappable surface.

DEFINITION OF DONE:
- <copy "Validate" section from playbook>
- ./gradlew test connectedAndroidTest lint detekt ktlintCheck green.

REPORT BACK:
- Files created
- Files modified
- Test counts (passing / total)
- Any deviations from the spec, with reasoning
- Any questions or blockers
```

---

# Appendix B — Risk Register

| Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|
| TTS quality varies per device | High | Medium | Phase 1 replaces with recorded audio |
| Lottie mascot blows up APK size | Medium | Low | Use `.lottie` format, lazy-load |
| Room migration corrupts existing data | Low | High | Migration tests + Crashlytics alerts on migration failure |
| Firebase costs spike on launch | Medium | Medium | Set budget alerts; cache aggressively; Firestore reads should be < 50/session |
| Play Families rejection | Medium | High | Pre-submit to a compliance reviewer (Tinybop / Sago Sago / external consultancy) |
| Parent gate trivially bypassed by older kid | High | Medium | Increase difficulty by Phase 4 (3-digit math, swipe-pattern) |
| Voice actors deliver poor audio | Low | High | Hire pros via Voquent or BunnyStudio; review samples before contract |
| GDPR-K compliance miss | Low | Catastrophic | Lawyer review before EU launch |

---

# Appendix C — Effort & Calendar Estimate

| Phase | Duration | Engineers | Notes |
|---|---|---|---|
| Phase 0 | 1 week | 1 | Solo work, mostly mechanical |
| Phase 1 | 3 weeks | 1 + voice actors + illustrator | Asset commissioning gates ship date |
| Phase 2 | 6 weeks | 1–2 | Adaptive engine is the time sink |
| Phase 3 | 8 weeks | 1–2 + 1 part-time backend | Firebase + Billing |
| Phase 4 | 4 weeks | 1 + compliance consultant | Iteration-heavy |
| **Total to launch** | **~22 weeks (~5 months)** | | Doable with one focused engineer + occasional contractors |

---

*End of playbook. Update this document whenever scope changes. Treat it as a living plan, not a contract.*
