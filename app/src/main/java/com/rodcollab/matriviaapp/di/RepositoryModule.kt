package com.rodcollab.matriviaapp.di

import com.rodcollab.matriviaapp.data.repository.RankingRepository
import com.rodcollab.matriviaapp.data.repository.RankingRepositoryImpl
import com.rodcollab.matriviaapp.data.repository.TriviaRepository
import com.rodcollab.matriviaapp.data.repository.TriviaRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun providesTriviaRepository(impl: TriviaRepositoryImpl): TriviaRepository
    @Singleton
    @Binds
    abstract fun providesRankingRepository(impl: RankingRepositoryImpl) : RankingRepository
}