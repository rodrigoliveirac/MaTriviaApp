package com.rodcollab.matriviaapp.redux.actions

sealed interface TimerActions {
    data object Update : TimerActions
    data object Over : TimerActions
    data object TimerThunkDispatcher : TimerActions
}