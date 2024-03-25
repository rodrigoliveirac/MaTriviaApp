package com.rodcollab.matriviaapp.game

import android.util.Log
import androidx.lifecycle.ViewModel
import com.rodcollab.matriviaapp.redux.thunk.GameThunks
import com.rodcollab.matriviaapp.redux.actions.EndGameActions
import com.rodcollab.matriviaapp.redux.actions.NetworkActions
import com.rodcollab.matriviaapp.redux.actions.PlayingGameActions
import com.rodcollab.matriviaapp.redux.actions.MenuGameActions
import com.rodcollab.matriviaapp.redux.reducer
import com.rodcollab.matriviaapp.redux.uiMiddleware
import dagger.hilt.android.lifecycle.HiltViewModel
import org.reduxkotlin.applyMiddleware
import org.reduxkotlin.createStore
import org.reduxkotlin.thunk.createThunkMiddleware
import javax.inject.Inject

@HiltViewModel
class TriviaGameVm @Inject constructor(
    private val gameUseCases: GameThunks
) : ViewModel() {

    val gameState = createStore(reducer, GameState(), applyMiddleware(
        createThunkMiddleware(), uiMiddleware(gameUseCases.timerThunk, gameUseCases.getRanking,gameUseCases.getQuestion,gameUseCases.getCategories)))

    fun onMenuGameAction(menuGameAction: MenuGameActions) {
        gameState.dispatch(menuGameAction)
    }

    fun onGamePlayingAction(gamePlayingActions: PlayingGameActions) {
        gameState.dispatch(gamePlayingActions)
    }

    fun onEndGameActions(endGameAction: EndGameActions) {
        gameState.dispatch(endGameAction)
    }

    fun changeNetworkState(state: Boolean?) {
        gameState.dispatch(NetworkActions.ChangeNetworkState(state))
        Log.d("networkState", state?.let { "available" } ?: run { "unavailable" })
    }

    fun onResume() {
        if(gameState.state.gameStatus == GameStatus.SETUP) {
            gameState.dispatch(MenuGameActions.FetchCriteriaFields)
        }
    }

    fun tryNetworkConnection() {
        gameState.dispatch(NetworkActions.TryAgain)
    }
}