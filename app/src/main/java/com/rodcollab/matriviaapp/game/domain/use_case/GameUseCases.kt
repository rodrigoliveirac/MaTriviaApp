package com.rodcollab.matriviaapp.game.domain.use_case

import com.rodcollab.matriviaapp.redux.GetCategoriesThunk
import com.rodcollab.matriviaapp.redux.TimerThunk

data class GameUseCases(
    val getQuestion: GetQuestion,
    val getRanking: GetRanking,
    val getCategories: GetCategoriesThunk,
    val timerThunk: TimerThunk
)
