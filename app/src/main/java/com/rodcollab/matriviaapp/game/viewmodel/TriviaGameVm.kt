package com.rodcollab.matriviaapp.game.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodcollab.matriviaapp.data.model.Category
import com.rodcollab.matriviaapp.data.model.QuestionDifficulty
import com.rodcollab.matriviaapp.data.model.QuestionType
import com.rodcollab.matriviaapp.game.domain.Question
import com.rodcollab.matriviaapp.game.domain.preferences.Preferences
import com.rodcollab.matriviaapp.game.domain.use_case.GameUseCases
import com.rodcollab.matriviaapp.game.intent.EndGameActions
import com.rodcollab.matriviaapp.game.intent.GamePlayingActions
import com.rodcollab.matriviaapp.game.intent.GiveUpGameActions
import com.rodcollab.matriviaapp.game.intent.MenuGameActions
import com.rodcollab.matriviaapp.game.intent.TimerActions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TriviaGameVm @Inject constructor(
    private val preferences: Preferences,
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


    init {
        viewModelScope.launch {
            _uiState.update {
                val fields = gameUseCases.showPrefsAndCriteria.invoke()
                it.copy(criteriaFields = GameCriteriaUiModel(DropDownMenu(field = fields.first), DropDownMenu(field = fields.second), DropDownMenu(field =fields.third)))
            }
        }
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

    fun onActionMenuGame(menuField: MenuGameActions) {
        viewModelScope.launch {
            when(menuField) {
                is MenuGameActions.ExpandMenu -> {
                    when(menuField.menuField) {
                        MenuFields.CATEGORY -> {
                            _uiState.update {
                                it.copy(criteriaFields = it.criteriaFields?.copy(categoryField = it.criteriaFields.categoryField.copy(expanded = !it.criteriaFields.categoryField.expanded)))
                            }
                        }
                        MenuFields.DIFFICULTY -> {
                            _uiState.update {
                                it.copy(criteriaFields = it.criteriaFields?.copy(difficultyField = it.criteriaFields.difficultyField.copy(expanded = !it.criteriaFields.difficultyField.expanded)))
                            }
                        }
                        else -> {
                            _uiState.update {
                                it.copy(criteriaFields = it.criteriaFields?.copy(typeField = it.criteriaFields.typeField.copy(expanded = !it.criteriaFields.typeField.expanded)))
                            }
                        }
                    }
                }
                is MenuGameActions.SelectItem<*> -> {
                    when(menuField.menuField) {
                        MenuFields.CATEGORY -> {
                            val category = menuField.item as Category
                            _uiState.update { gameState ->

                                val criteriaFields = gameState.criteriaFields

                                gameState
                                    .copy(
                                        criteriaFields = criteriaFields?.
                                        copy(
                                            categoryField = criteriaFields
                                                .categoryField
                                                .copy(
                                                expanded = !criteriaFields.categoryField.expanded,
                                                field = criteriaFields.categoryField.field?.copy(selected = category)
                                        )
                                    )
                                )
                            }
                        }
                        MenuFields.DIFFICULTY -> {
                            val difficulty = menuField.item as QuestionDifficulty
                            _uiState.update { gameState ->

                                val criteriaFields = gameState.criteriaFields

                                gameState.copy(criteriaFields = criteriaFields?.copy(difficultyField =
                                    criteriaFields.difficultyField.copy(
                                        expanded = !criteriaFields.difficultyField.expanded,
                                        field = criteriaFields.difficultyField.field?.copy(selected = difficulty))))
                            }
                        }
                        else -> {
                            val type = menuField.item as QuestionType
                            _uiState.update { gameState ->

                                val criteriaFields = gameState.criteriaFields

                                gameState.copy(criteriaFields = criteriaFields?.copy(typeField =
                                criteriaFields.typeField.copy(
                                    expanded = !criteriaFields.typeField.expanded,
                                    field = criteriaFields.typeField.field?.copy(selected = type))))
                            }
                        }
                    }
                }
                is MenuGameActions.StartGame -> {
                    preferences.updateGamePrefs(
                        type = _uiState.value.criteriaFields?.typeField?.field?.selected?.id ?: 0,
                        difficulty = _uiState.value.criteriaFields?.difficultyField?.field?.selected?.id ?: 0,
                        category = _uiState.value.criteriaFields?.categoryField?.field?.selected?.id ?: 0)
                    initGameOrContinueWithNewQuestions()
                }
            }
        }
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
                is GamePlayingActions.ConfirmAnswer -> {
                    confirmAnswer()
                }
                is GamePlayingActions.SelectOption -> {
                    selectOption(gamePlayingActions.optionId)
                }
            }
        }
    }

    private suspend fun confirmAnswer() {
        when(gameUseCases.questionValidator.invoke(ID_CORRECT_ANSWER,_uiState.value.currentOptionIdSelected!!)) {
            true -> {
                _uiState.update { triviaGameState ->
                    val optionsUpdated = highlightCorrectAnswer(triviaGameState.optionsAnswers)
                    val correctAnswersUpdated = incrementCorrectAnswers(triviaGameState)
                    triviaGameState.copy(correctAnswers = correctAnswersUpdated,isCorrectOrIncorrect = true, optionsAnswers = optionsUpdated)
                }
                delay(ONE_SECOND)
                initGameOrContinueWithNewQuestions()
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
                        currentCorrectAnswerId = null,
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
        }
    }

    private suspend fun insertRanking() {
        val correctAnswers = _uiState.value.correctAnswers
        gameUseCases.insertRanking(correctAnswers)
    }

    private fun selectOption(selectedId: Int) {
        _uiState.update { gameState ->
            var optionsAnswersUiModelUpdated = gameState.optionsAnswers
            var currentCorrectAnswerId: Int? = gameState.currentOptionIdSelected
            optionsAnswersUiModelUpdated = optionsAnswersUiModelUpdated.map { answerOptionUiModel ->
                if(selectedId == answerOptionUiModel.id) {
                    currentCorrectAnswerId = if(currentCorrectAnswerId == answerOptionUiModel.id) null else answerOptionUiModel.id
                    answerOptionUiModel.copy(selected = !answerOptionUiModel.selected)
                } else {
                    answerOptionUiModel.copy(selected = false)
                }
            }.toMutableList()

            gameState.copy(optionsAnswers = optionsAnswersUiModelUpdated, currentOptionIdSelected = currentCorrectAnswerId)
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
        val ranking = gameUseCases.getRanking()
        insertRanking()
        delay(ONE_SECOND)
        _uiState.update {
            it.copy(
                currentState = GameStatus.ENDED,
                questions = listOf(),
                currentQuestion = null,
                currentCorrectAnswerId = null,
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