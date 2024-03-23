package com.rodcollab.matriviaapp.game.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodcollab.matriviaapp.di.DefaultDispatcher
import com.rodcollab.matriviaapp.game.domain.Question
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
import kotlinx.coroutines.flow.update
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

    private val _uiState: MutableStateFlow<TriviaGameState> by lazy {
        MutableStateFlow(TriviaGameState())
    }
    val uiState: StateFlow<TriviaGameState> = _uiState.asStateFlow()

    private val _timeState: MutableStateFlow<Int?> by lazy {
        MutableStateFlow(null)
    }
    val timeState: StateFlow<Int?> = _timeState.asStateFlow()

    val gameState = createStore(reducer, GameState(), applyMiddleware(
        createThunkMiddleware(), uiMiddleware(gameUseCases.getRanking,gameUseCases.getQuestion,gameUseCases.getCategories)))

    init {
        viewModelScope.launch(dispatcher) {
            gameState.dispatch(Actions.FetchCriteriaFields)
        }
    }

    fun onActionMenuGame(menuGameAction: MenuGameAction) {
        gameState.dispatch(menuGameAction)
    }

    private fun highlightCorrectAnswer(options: List<AnswerOptionsUiModel>): MutableList<AnswerOptionsUiModel> {
        val optionsUpdated = options.map { option ->
            if (ID_CORRECT_ANSWER == option.id) {
                option.copy(highlight = true)
            } else {
                option.copy()
            }
        }.toMutableList()
        return optionsUpdated
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

    suspend fun onTimeActions(timerActions: TimerActions) {
        when(timerActions) {
            is TimerActions.Over -> {
                updateStatusGamerOver()
            }
            is TimerActions.Update -> {
                updateTime()
            }
        }
    }

    private suspend fun updateTime() {
        delay(ONE_SECOND)
        _timeState.update { timeState ->
            timeState?.minus(1)
        }
        if (_timeState.value == ZERO_TIME_VALUE) {
            _uiState.update { triviaGameState ->
                val optionsUpdated = highlightCorrectAnswer(triviaGameState.optionsAnswers)
                triviaGameState.copy(
                    isCorrectOrIncorrect = false,
                    optionsAnswers = optionsUpdated,
                    timeIsFinished = true
                )
            }
        }
    }

    private suspend fun updateStatusGamerOver() {
        //val ranking = gameUseCases.getRanking()
        //insertRanking()
        delay(ONE_SECOND)
        _uiState.update {
            it.copy(
                currentState = GameStatus.ENDED,
                questions = listOf(),
                currentQuestion = null,
                isCorrectOrIncorrect = null,
                optionsAnswers = listOf(),
                isLoading = false,
                currentOptionIdSelected = null,
                timeIsFinished = false,
            )
        }
        _timeState.update { null }
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