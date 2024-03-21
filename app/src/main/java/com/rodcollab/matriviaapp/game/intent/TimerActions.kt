package com.rodcollab.matriviaapp.game.intent

sealed interface TimerActions {
    data object Update : TimerActions
    data object Over : TimerActions
}