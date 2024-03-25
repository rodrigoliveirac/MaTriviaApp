package com.rodcollab.matriviaapp.redux.actions

sealed interface NetworkActions {
    data object NetworkWarning : NetworkActions
    data object TryAgain : NetworkActions
    data class ChangeNetworkState(val network: Boolean?) : NetworkActions
}