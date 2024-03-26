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

    val gameStore = createStore(reducer, GameState(), applyMiddleware(
        createThunkMiddleware(), uiMiddleware(gameUseCases.timerThunk, gameUseCases.getRanking,gameUseCases.getQuestion,gameUseCases.getCategories)))

    fun onMenuGameAction(menuGameAction: MenuGameActions) {
        gameStore.dispatch(menuGameAction)
    }

    fun onGamePlayingAction(gamePlayingActions: PlayingGameActions) {
        gameStore.dispatch(gamePlayingActions)
    }

    fun onEndGameActions(endGameAction: EndGameActions) {
        gameStore.dispatch(endGameAction)
    }

    fun changeNetworkState(state: Boolean?) {
        gameStore.dispatch(NetworkActions.ChangeNetworkState(state))
        Log.d("networkState", state?.let { "available" } ?: run { "unavailable" })
    }

    fun onResume() {
        if(gameStore.state.gameStatus == GameStatus.SETUP) {
            gameStore.dispatch(MenuGameActions.FetchCriteriaFields)
        }
    }

    fun tryNetworkConnection() {
        gameStore.dispatch(NetworkActions.TryAgain)
    }
}