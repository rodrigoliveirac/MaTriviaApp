package com.rodcollab.matriviaapp.game.intent

sealed interface EndGameActions {
    data object PlayAgain : EndGameActions
    data object BackToGameSetup : EndGameActions
}