# Kido

A native Android app for **preschool kids (ages 3–5)** to learn while playing games. Built with Kotlin + Jetpack Compose, Material 3, multi-language ready from day one.

## Status — v0 (alphabet recognition slice)

What's playable today:

- **Home screen** with mascot and star count
- **Alphabet game**: "Tap the letter you hear" — mascot narrates a letter, kid taps it from a 2×2 grid, gets a star
- **Parent gate** (math challenge) → **Parent settings** (rename child, switch alphabet language, reset progress)
- **Offline only**: local progress in DataStore; English alphabet bundled in `assets/`
- **Multi-language platform**: drop a new alphabet pack into `assets/content/alphabets/{code}.json` and it shows up automatically in the parent settings language picker

### Placeholders to swap before launch

| Asset | Today | Swap with |
|---|---|---|
| Mascot character | Procedural purple blob with eyes ([Mascot.kt](app/src/main/kotlin/com/kido/app/ui/components/Mascot.kt)) | Commissioned cartoon mascot |
| Letter narration | Android on-device TextToSpeech ([NarrationService.kt](app/src/main/kotlin/com/kido/app/core/audio/NarrationService.kt)) | Recorded native-speaker `.wav` files in `assets/audio/{languageCode}/` |
| Letter icons | Emoji from the JSON content pack | Custom illustrations |

The asset layer is designed for clean swaps — UI callsites don't change.

## Requirements

- **Android Studio** Ladybug (2024.2) or newer
- **JDK** 17
- **Android SDK** 35 (compile) / 26+ (runtime, Android 8.0)

## Getting started

### From Android Studio

1. Open the project root. It will sync Gradle, generate the wrapper, and download dependencies.
2. Pick an emulator or device running Android 8.0 (API 26) or later.
3. Press **Run** (`Shift+F10`).

### From the command line

The Gradle wrapper JAR isn't checked in. Generate it once with a locally installed Gradle:

```bash
gradle wrapper --gradle-version 8.11.1
```

Then:

- `./gradlew assembleDebug` — build a debug APK
- `./gradlew installDebug` — install on a connected device
- `./gradlew test` — JVM unit tests
- `./gradlew connectedAndroidTest` — instrumented tests on a device

## Project layout

```
app/
  build.gradle.kts
  src/main/
    AndroidManifest.xml
    assets/content/alphabets/
      en.json                                 alphabet content pack (drop new languages here)
    kotlin/com/kido/app/
      KidoApp.kt                              Application class — owns service-locator singletons
      MainActivity.kt                         single-activity entry point
      core/
        audio/NarrationService.kt             TTS narration (swap point for recorded audio)
        content/AlphabetPack.kt               content data model (Letter, AlphabetPack)
        content/ContentRepository.kt          loads content packs from assets
        profile/ChildProfile.kt
        profile/ChildProfileStore.kt          DataStore-backed progress + settings
      feature/
        home/HomeScreen.kt + ViewModel
        game/GameScreen.kt + ViewModel        "Tap the letter you hear"
        parent/ParentGateScreen.kt + ViewModel
        parent/ParentSettingsScreen.kt + ViewModel
      navigation/KidoNavHost.kt               routes + NavHost
      ui/
        components/                           BigButton, LetterTile, Mascot, Star — kid-sized design system
        theme/                                Material 3 theme + KidoColors palette
    res/                                      strings, themes, launcher icon
gradle/libs.versions.toml                     version catalog
```

## Stack

- **Kotlin** 2.1 + **Jetpack Compose** (Compose BOM 2024.12.01, Material 3)
- **Navigation** via `androidx.navigation:navigation-compose`
- **State** via `lifecycle-viewmodel-compose` + Kotlin Flow / StateFlow
- **Persistence** via `androidx.datastore:datastore-preferences`
- **Content** via `kotlinx-serialization-json` over bundled assets
- **Audio** via Android `TextToSpeech` (v0 placeholder)
- **Android Gradle Plugin** 8.7

## Roadmap

- **v0 (current)** — alphabet slice, single subject, English content, offline, placeholder mascot + TTS
- **v0.X** — real mascot art and recorded narration; additional language packs (Hindi, Spanish, French, German, Dutch, Arabic, Chinese, Japanese, Canadian French)
- **v1** — additional subject modules (math, poems, word plays, simple logic)
- **v1.X** — hybrid cloud layer: parent account, cross-device progress sync, downloadable content packs (gated behind parent gate)
- **v2** — Google Play Families launch
