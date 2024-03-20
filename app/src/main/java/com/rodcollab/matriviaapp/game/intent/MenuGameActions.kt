package com.rodcollab.matriviaapp.game.intent

import com.rodcollab.matriviaapp.game.viewmodel.MenuFields

sealed interface MenuGameActions {
    data class ExpandMenu(val menuField: MenuFields) : MenuGameActions
    data class SelectItem<T>(val menuField: MenuFields, val item:T) : MenuGameActions
    data object StartGame : MenuGameActions
}