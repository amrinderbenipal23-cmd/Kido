package com.quickfix.kidszone.di

import android.content.Context
import com.quickfix.kidszone.utils.KiddoAudioManager
import com.quickfix.kidszone.utils.KiddoTextToSpeech
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAudioManager(@ApplicationContext context: Context): KiddoAudioManager =
        KiddoAudioManager(context)

    @Provides
    @Singleton
    fun provideTextToSpeech(@ApplicationContext context: Context): KiddoTextToSpeech =
        KiddoTextToSpeech(context)
}
