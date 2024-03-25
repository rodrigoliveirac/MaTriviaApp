package com.rodcollab.matriviaapp.redux.actions

sealed interface EndGameActions {
    data object PlayAgain : EndGameActions
    data object BackToGameSetup : EndGameActions
}