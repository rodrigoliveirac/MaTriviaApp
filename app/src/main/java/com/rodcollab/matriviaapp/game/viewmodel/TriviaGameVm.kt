package com.rodcollab.matriviaapp.game.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodcollab.matriviaapp.game.domain.use_case.GameUseCases
import com.rodcollab.matriviaapp.game.intent.EndGameActions
import com.rodcollab.matriviaapp.game.intent.GamePlayingActions
import com.rodcollab.matriviaapp.game.intent.GiveUpGameActions
import com.rodcollab.matriviaapp.redux.Actions
import com.rodcollab.matriviaapp.redux.MenuGameAction
import com.rodcollab.matriviaapp.redux.GameState
import com.rodcollab.matriviaapp.redux.NetworkActions
import com.rodcollab.matriviaapp.redux.reducer
import com.rodcollab.matriviaapp.redux.uiMiddleware
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.reduxkotlin.applyMiddleware
import org.reduxkotlin.createStore
import org.reduxkotlin.thunk.createThunkMiddleware
import javax.inject.Inject

@HiltViewModel
class TriviaGameVm @Inject constructor(
    private val gameUseCases: GameUseCases
) : ViewModel() {

    val gameState = createStore(reducer, GameState(), applyMiddleware(
        createThunkMiddleware(), uiMiddleware(gameUseCases.timerThunk, gameUseCases.getRanking,gameUseCases.getQuestion,gameUseCases.getCategories)))

    fun onActionMenuGame(menuGameAction: MenuGameAction) {
        gameState.dispatch(menuGameAction)
    }

    fun onGamePlayingAction(gamePlayingActions: GamePlayingActions) {
        viewModelScope.launch {
            when(gamePlayingActions) {
                is GamePlayingActions.SelectOption -> {
                    gameState.dispatch(Actions.CheckAnswer(gamePlayingActions.optionId))
                }
            }
        }
    }

    fun onEndGameActions(endGameAction: EndGameActions) {
        gameState.dispatch(endGameAction)
    }

    fun onTopBarGiveUpGame() {
        gameState.dispatch(Actions.OnTopBarGiveUp)
    }

    fun onGiveUpGameAction(giveUpGameActions: GiveUpGameActions) {
        gameState.dispatch(giveUpGameActions)
    }

    fun changeNetworkState(state: Boolean?) {
        gameState.dispatch(NetworkActions.ChangeNetworkState(state))
        Log.d("networkState", state?.let { "available" } ?: run { "unavailable" })
    }

    fun onResume() {
        if(gameState.state.gameStatus == GameStatus.SETUP) {
            gameState.dispatch(Actions.FetchCriteriaFields)
        }
    }

    fun tryNetworkConnection() {
        gameState.dispatch(NetworkActions.TryAgain)
    }
}