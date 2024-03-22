package com.rodcollab.matriviaapp.game.intent

import com.rodcollab.matriviaapp.game.viewmodel.MenuFields
import com.rodcollab.matriviaapp.redux.ExpandMenuAction

sealed interface MenuGameActions {
    data object StartGame : MenuGameActions
}
