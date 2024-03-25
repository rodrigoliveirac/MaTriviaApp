package com.rodcollab.matriviaapp.redux.thunk

import com.rodcollab.matriviaapp.game.GameState
import org.reduxkotlin.thunk.Thunk

interface GetQuestionThunk {
    fun getQuestionThunk() : Thunk<GameState>
}