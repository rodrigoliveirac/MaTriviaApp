package com.rodcollab.matriviaapp.redux.thunk

import com.rodcollab.matriviaapp.game.GameState
import org.reduxkotlin.thunk.Thunk

interface PrefsAndCriteriaThunk {
    fun getCriteriaFields(): Thunk<GameState>
    fun updatePreferences(type: Int, difficulty: Int, category: Int): Thunk<GameState>
}