package com.rodcollab.matriviaapp.game.domain.use_case

import com.rodcollab.matriviaapp.game.domain.Question
import com.rodcollab.matriviaapp.redux.GameState
import org.reduxkotlin.thunk.Thunk

interface GetQuestion {
    fun getQuestionThunk() : Thunk<GameState>
}