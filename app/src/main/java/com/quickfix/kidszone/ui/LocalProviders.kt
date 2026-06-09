package com.quickfix.kidszone.ui

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Whether ads should be shown, driven by the parent "Show Ads" setting.
 * Consumed by ad surfaces (banner + interstitial) so the toggle has real effect.
 */
val LocalAdsEnabled = compositionLocalOf { true }

/**
 * Current app language code ("en" or "hi"), driven by the Settings language
 * selector. Used for language-dependent UI text. Spoken (TTS) language is
 * handled in the relevant ViewModels which read the same preference.
 */
val LocalAppLanguage = staticCompositionLocalOf { "en" }
