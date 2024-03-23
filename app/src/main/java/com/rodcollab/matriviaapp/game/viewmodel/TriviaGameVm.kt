package com.rodcollab.matriviaapp.game.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodcollab.matriviaapp.di.DefaultDispatcher
import com.rodcollab.matriviaapp.game.domain.use_case.GameUseCases
import com.rodcollab.matriviaapp.game.intent.EndGameActions
import com.rodcollab.matriviaapp.game.intent.GamePlayingActions
import com.rodcollab.matriviaapp.game.intent.GiveUpGameActions
import com.rodcollab.matriviaapp.game.intent.TimerActions
import com.rodcollab.matriviaapp.redux.Actions
import com.rodcollab.matriviaapp.redux.MenuGameAction
import com.rodcollab.matriviaapp.redux.GameState
import com.rodcollab.matriviaapp.redux.reducer
import com.rodcollab.matriviaapp.redux.uiMiddleware
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.reduxkotlin.applyMiddleware
import org.reduxkotlin.createStore
import org.reduxkotlin.thunk.createThunkMiddleware
import javax.inject.Inject

@HiltViewModel
class TriviaGameVm @Inject constructor(
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
    private val gameUseCases: GameUseCases
) : ViewModel() {



    val gameState = createStore(reducer, GameState(), applyMiddleware(
        createThunkMiddleware(), uiMiddleware(gameUseCases.timerThunk, gameUseCases.getRanking,gameUseCases.getQuestion,gameUseCases.getCategories)))

    init {
        viewModelScope.launch(dispatcher) {
            gameState.dispatch(Actions.FetchCriteriaFields)
        }
    }

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

    fun onTimeActions(timerActions: TimerActions) {
        gameState.dispatch(timerActions)
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

    companion object {
        private const val ID_CORRECT_ANSWER = 0
        private const val ONE_SECOND = 1000L
        private const val INITIAL_TIME_VALUE = 10
        private const val ZERO_TIME_VALUE = 0
    }
}