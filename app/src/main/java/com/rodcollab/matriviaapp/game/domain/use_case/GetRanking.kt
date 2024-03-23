package com.rodcollab.matriviaapp.game.domain.use_case

import com.rodcollab.matriviaapp.redux.GameState
import org.reduxkotlin.thunk.Thunk

interface GetRanking {
    fun getRanking() : Thunk<GameState>
}