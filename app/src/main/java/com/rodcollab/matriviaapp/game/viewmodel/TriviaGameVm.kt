package com.rodcollab.matriviaapp.game.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodcollab.matriviaapp.game.domain.Question
import com.rodcollab.matriviaapp.game.domain.use_case.GameUseCases
import com.rodcollab.matriviaapp.game.intent.EndGameActions
import com.rodcollab.matriviaapp.game.intent.GamePlayingActions
import com.rodcollab.matriviaapp.game.intent.GiveUpGameActions
import com.rodcollab.matriviaapp.game.intent.TimerActions
import com.rodcollab.matriviaapp.redux.Actions
import com.rodcollab.matriviaapp.redux.ExpandMenuAction
import com.rodcollab.matriviaapp.redux.MenuGameAction
import com.rodcollab.matriviaapp.redux.GameState
import com.rodcollab.matriviaapp.redux.PrepareGame
import com.rodcollab.matriviaapp.redux.SelectCriteriaAction
import com.rodcollab.matriviaapp.redux.reducer
import com.rodcollab.matriviaapp.redux.uiMiddleware
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

    private val disableSelection: MutableStateFlow<Boolean> by lazy {
        MutableStateFlow(false)
    }
    val gameState = createStore(reducer, GameState(), applyMiddleware(
        createThunkMiddleware(), uiMiddleware(gameUseCases.getQuestion,gameUseCases.getCategories)))

    init {
        gameState.dispatch(Actions.FetchCriteriaFields)
    }

    private suspend fun initGameOrContinueWithNewQuestions() {
        _uiState.update {
            it.copy(isLoading = true)
        }
        val (questions, currentQuestion, optionsAnswers) = getNewQuestion()
        _uiState.update { triviaGameState ->
            triviaGameState.copy(
                correctAnswers = if(triviaGameState.currentState == GameStatus.ENDED || triviaGameState.currentState == GameStatus.SETUP) 0 else triviaGameState.correctAnswers,
                isCorrectOrIncorrect = null,
                questions = questions,
                currentQuestion = currentQuestion,
                optionsAnswers = optionsAnswers,
                isLoading = false,
                currentState = GameStatus.STARTED,
                currentOptionIdSelected = null,
                timeIsFinished = false,
                confirmWithdrawal = false
            )
        }
        _timeState.update { INITIAL_TIME_VALUE }
    }

    fun onActionMenuGame(menuGameAction: MenuGameAction) {
        gameState.dispatch(menuGameAction)
    }

    private suspend fun getNewQuestion(): Triple<List<Question>, Question, List<AnswerOptionsUiModel>> {
        return if(_uiState.value.questions.isEmpty()) {
                val questions = gameUseCases.getQuestion.invoke().toMutableList()
                while (questions.isEmpty()) {
                    delay(1L)
                }
                val currentQuestion = questions.last()
                questions.remove(currentQuestion)
                val optionsAnswers = currentQuestion.answerOptions.map { answerOption ->
                    AnswerOptionsUiModel(
                        id = answerOption.id,
                        option = answerOption.answer,
                    )
                }
                Triple(questions, currentQuestion, optionsAnswers)
        } else {
            val questions = _uiState.value.questions.toMutableList()
            val currentQuestion = questions.last()
            questions.remove(currentQuestion)
            val optionsAnswers = currentQuestion.answerOptions.map { answerOption ->
                AnswerOptionsUiModel(
                    id = answerOption.id,
                    option = answerOption.answer,
                )
            }
            Triple(questions, currentQuestion, optionsAnswers)
        }
    }

    private fun incrementCorrectAnswers(triviaGameState: TriviaGameState): Int {
        var correctAnswers = triviaGameState.correctAnswers
        correctAnswers++
        return correctAnswers
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
                    confirmAnswer(gamePlayingActions.optionId)
                }
            }
        }
    }

    private suspend fun confirmAnswer(selectedId: Int) {
        if(!disableSelection.value) {
            disableSelection.update { true }
            _uiState.update { gameState ->
                var optionsAnswersUiModelUpdated = gameState.optionsAnswers
                optionsAnswersUiModelUpdated = optionsAnswersUiModelUpdated.map { answerOptionUiModel ->
                    if(selectedId == answerOptionUiModel.id) {
                        answerOptionUiModel.copy(selected = !answerOptionUiModel.selected)
                    } else {
                        answerOptionUiModel.copy(selected = false)
                    }
                }.toMutableList()
                gameState.copy(optionsAnswers = optionsAnswersUiModelUpdated)
            }
        }
        delay(100L)
        when(gameUseCases.questionValidator.invoke(ID_CORRECT_ANSWER,selectedId)) {
            true -> {
                _uiState.update { triviaGameState ->
                    val optionsUpdated = highlightCorrectAnswer(triviaGameState.optionsAnswers)
                    val correctAnswersUpdated = incrementCorrectAnswers(triviaGameState)
                    triviaGameState.copy(correctAnswers = correctAnswersUpdated,isCorrectOrIncorrect = true, optionsAnswers = optionsUpdated)
                }
                delay(ONE_SECOND)
                initGameOrContinueWithNewQuestions()
                disableSelection.update { false }
            }
            else -> {

                val ranking = gameUseCases.getRanking()
                insertRanking()
                _uiState.update { triviaGameState ->
                    val optionsUpdated = highlightCorrectAnswer(triviaGameState.optionsAnswers)
                    triviaGameState.copy(isCorrectOrIncorrect = false, optionsAnswers = optionsUpdated)
                }
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
                        ranking = ranking
                    )
                }
                disableSelection.update { false }
                _timeState.update { null }
            }
        }
    }

    private suspend fun insertRanking() {
        val correctAnswers = _uiState.value.correctAnswers
        gameUseCases.insertRanking(correctAnswers)
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
        val ranking = gameUseCases.getRanking()
        insertRanking()
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
                ranking = ranking
            )
        }
        _timeState.update { null }
    }

    fun onEndGameActions(endGameAction: EndGameActions) {
        viewModelScope.launch {
            when(endGameAction) {
                is EndGameActions.BackToGameSetup -> {
                    _uiState.update { gameState ->
                        gameState.copy(currentState = GameStatus.SETUP)
                    }
                }
                is EndGameActions.PlayAgain -> {
                    initGameOrContinueWithNewQuestions()
                }
            }
        }
    }

    fun onTopBarGiveUpGame() {
        viewModelScope.launch {
            _uiState.update { triviaGameState ->
                triviaGameState.copy(confirmWithdrawal = !triviaGameState.confirmWithdrawal)
            }
        }
    }

    fun onGiveUpGameAction(giveUpGameActions: GiveUpGameActions) {
        viewModelScope.launch {
            when(giveUpGameActions) {
                is GiveUpGameActions.Confirm -> {
                    updateStatusGamerOver()
                }
                is GiveUpGameActions.GoBack -> {
                    _uiState.update { triviaGameState ->
                        triviaGameState.copy(confirmWithdrawal = !triviaGameState.confirmWithdrawal)
                    }
                }
            }
        }
    }

    companion object {
        private const val ID_CORRECT_ANSWER = 0
        private const val ONE_SECOND = 1000L
        private const val INITIAL_TIME_VALUE = 10
        private const val ZERO_TIME_VALUE = 0
    }
}