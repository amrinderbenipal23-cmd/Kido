package com.quickfix.kidszone.di

import android.content.Context
import androidx.room.Room
import com.quickfix.kidszone.data.local.database.KiddoDatabase
import com.quickfix.kidszone.data.local.database.dao.ProgressDao
import com.quickfix.kidszone.data.local.database.dao.RewardDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KiddoDatabase =
        Room.databaseBuilder(context, KiddoDatabase::class.java, "kiddo_database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideProgressDao(db: KiddoDatabase): ProgressDao = db.progressDao()

    @Provides
    fun provideRewardDao(db: KiddoDatabase): RewardDao = db.rewardDao()
}
