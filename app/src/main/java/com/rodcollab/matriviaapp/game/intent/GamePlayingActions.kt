package com.rodcollab.matriviaapp.game.intent

sealed interface GamePlayingActions {
    data class SelectOption(val optionId: Int) : GamePlayingActions
}