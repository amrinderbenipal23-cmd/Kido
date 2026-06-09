package com.quickfix.kidszone.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * App-level Hilt module.
 *
 * [com.quickfix.kidszone.utils.KiddoAudioManager],
 * [com.quickfix.kidszone.utils.KiddoTextToSpeech] and
 * [com.quickfix.kidszone.utils.SpeechRecognitionManager] are provided directly
 * via their `@Inject` constructors, so no explicit `@Provides` methods are
 * needed here.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule
