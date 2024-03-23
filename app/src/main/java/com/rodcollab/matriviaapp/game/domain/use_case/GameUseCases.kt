package com.rodcollab.matriviaapp.game.domain.use_case

import com.rodcollab.matriviaapp.redux.GetCategoriesThunk

data class GameUseCases(
    val getQuestion: GetQuestion,
    val getRanking: GetRanking,
    val getCategories: GetCategoriesThunk
)
