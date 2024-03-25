package com.rodcollab.matriviaapp.redux.actions

sealed interface GamePlayingActions {
    data class SelectOption(val optionId: Int) : GamePlayingActions
}