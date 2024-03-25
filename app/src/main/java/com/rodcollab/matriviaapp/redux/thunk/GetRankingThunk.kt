package com.rodcollab.matriviaapp.redux.thunk

import com.rodcollab.matriviaapp.game.GameState
import org.reduxkotlin.thunk.Thunk

interface GetRankingThunk {
    fun getRanking() : Thunk<GameState>
}