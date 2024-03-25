package com.rodcollab.matriviaapp.game.di

import com.rodcollab.matriviaapp.data.repository.RankingRepository
import com.rodcollab.matriviaapp.data.repository.TriviaRepository
import com.rodcollab.matriviaapp.di.DefaultDispatcher
import com.rodcollab.matriviaapp.data.preferences.Preferences
import com.rodcollab.matriviaapp.redux.thunk.GameThunks
import com.rodcollab.matriviaapp.redux.thunk.GetQuestionThunkImpl
import com.rodcollab.matriviaapp.redux.thunk.GetRankingThunkImpl
import com.rodcollab.matriviaapp.redux.thunk.PrefsAndCriteriaThunkImpl
import com.rodcollab.matriviaapp.redux.thunk.TimerThunkImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher

@InstallIn(ViewModelComponent::class)
@Module
object GameDomainModule {

    @ViewModelScoped
    @Provides
    fun providesGameUseCases(
        @DefaultDispatcher dispatcher: CoroutineDispatcher,
        rankingRepository: RankingRepository,
        repository: TriviaRepository,
        preferences: Preferences
    ): GameThunks {
        return GameThunks(
            getQuestion = GetQuestionThunkImpl(dispatcher,repository),
            getRanking = GetRankingThunkImpl(dispatcher,rankingRepository),
            getCategories = PrefsAndCriteriaThunkImpl(dispatcher,preferences,repository),
            timerThunk = TimerThunkImpl(dispatcher)
        )
    }
}