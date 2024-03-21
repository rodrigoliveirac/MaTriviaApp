package com.rodcollab.matriviaapp.game.di

import com.rodcollab.matriviaapp.data.repository.RankingRepository
import com.rodcollab.matriviaapp.data.repository.TriviaRepository
import com.rodcollab.matriviaapp.game.domain.preferences.Preferences
import com.rodcollab.matriviaapp.game.domain.use_case.GameUseCases
import com.rodcollab.matriviaapp.game.domain.use_case.GetQuestionImpl
import com.rodcollab.matriviaapp.game.domain.use_case.GetRankingImpl
import com.rodcollab.matriviaapp.game.domain.use_case.InsertRankingImpl
import com.rodcollab.matriviaapp.game.domain.use_case.QuestionValidatorImpl
import com.rodcollab.matriviaapp.game.domain.use_case.ShowPrefsAndCriteriaImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@InstallIn(ViewModelComponent::class)
@Module
object GameDomainModule {

    @ViewModelScoped
    @Provides
    fun providesGameUseCases(
        rankingRepository: RankingRepository,
        repository: TriviaRepository,
        preferences: Preferences
    ): GameUseCases {
        return GameUseCases(
            getQuestion = GetQuestionImpl(preferences,repository),
            showPrefsAndCriteria = ShowPrefsAndCriteriaImpl(preferences, repository),
            questionValidator = QuestionValidatorImpl(),
            insertRanking = InsertRankingImpl(rankingRepository),
            getRanking = GetRankingImpl(rankingRepository)
        )
    }
}