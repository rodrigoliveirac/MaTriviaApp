package com.rodcollab.matriviaapp.game.intent

sealed interface GiveUpGameActions {
    data object Confirm : GiveUpGameActions
    data object GoBack : GiveUpGameActions
}