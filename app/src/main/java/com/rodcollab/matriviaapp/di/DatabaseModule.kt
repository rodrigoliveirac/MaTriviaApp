package com.rodcollab.matriviaapp.di

import android.app.Application
import com.rodcollab.matriviaapp.data.local.AppDatabase
import com.rodcollab.matriviaapp.data.local.RankingDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class RoomModule {
    @Singleton
    @Provides
    fun providesRoomDatabase(application: Application): AppDatabase {
        return AppDatabase.getInstance(application)
    }

    @Singleton
    @Provides
    fun providesRankingDao(database: AppDatabase): RankingDao {
        return database.rankingDao()
    }
}