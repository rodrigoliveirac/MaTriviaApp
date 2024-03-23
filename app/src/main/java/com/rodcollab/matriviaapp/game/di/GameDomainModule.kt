package com.rodcollab.matriviaapp.game.di

import com.rodcollab.matriviaapp.data.repository.RankingRepository
import com.rodcollab.matriviaapp.data.repository.TriviaRepository
import com.rodcollab.matriviaapp.di.DefaultDispatcher
import com.rodcollab.matriviaapp.game.domain.preferences.Preferences
import com.rodcollab.matriviaapp.game.domain.use_case.GameUseCases
import com.rodcollab.matriviaapp.game.domain.use_case.GetQuestionImpl
import com.rodcollab.matriviaapp.game.domain.use_case.GetRankingImpl
import com.rodcollab.matriviaapp.game.domain.use_case.InsertRankingImpl
import com.rodcollab.matriviaapp.game.domain.use_case.QuestionValidatorImpl
import com.rodcollab.matriviaapp.game.domain.use_case.ShowPrefsAndCriteriaImpl
import com.rodcollab.matriviaapp.redux.PrefsAndCriteriaThunkImpl
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
    ): GameUseCases {
        return GameUseCases(
            getQuestion = GetQuestionImpl(dispatcher,preferences,repository),
            getRanking = GetRankingImpl(dispatcher,rankingRepository),
            getCategories = PrefsAndCriteriaThunkImpl(dispatcher,preferences,repository)
        )
    }
}