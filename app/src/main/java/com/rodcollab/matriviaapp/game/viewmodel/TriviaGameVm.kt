package com.rodcollab.matriviaapp.game.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodcollab.matriviaapp.data.model.Category
import com.rodcollab.matriviaapp.data.model.QuestionDifficulty
import com.rodcollab.matriviaapp.data.model.QuestionType
import com.rodcollab.matriviaapp.game.domain.Question
import com.rodcollab.matriviaapp.game.domain.preferences.Preferences
import com.rodcollab.matriviaapp.game.domain.use_case.GameUseCases
import com.rodcollab.matriviaapp.game.ui.ActionsField
import dagger.hilt.android.lifecycle.HiltViewModel
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


    init {
        viewModelScope.launch {
            _uiState.update {
                val fields = gameUseCases.showPrefsAndCriteria.invoke()
                it.copy(criteriaFields = GameCriteriaUiModel(DropDownMenu(field = fields.first), DropDownMenu(field = fields.second), DropDownMenu(field =fields.third)))
            }
        }
    }
    fun startGame() {
        viewModelScope.launch {
            preferences.updateGamePrefs(
                type = _uiState.value.criteriaFields?.typeField?.field?.selected?.id ?: 0,
                difficulty = _uiState.value.criteriaFields?.difficultyField?.field?.selected?.id ?: 0,
                category = _uiState.value.criteriaFields?.categoryField?.field?.selected?.id ?: 0)

            initGameOrContinueWithNewQuestions()
        }
    }

    private suspend fun TriviaGameVm.initGameOrContinueWithNewQuestions() {
        _uiState.update {
            it.copy(isLoading = true)
        }
        val (questions, currentQuestion, optionsAnswers) = getNewQuestion()
        _uiState.update { triviaGameState ->
            triviaGameState.copy(
                isCorrectOrIncorrect = null,
                questions = questions,
                currentQuestion = currentQuestion,
                optionsAnswers = optionsAnswers,
                isLoading = false,
                currentState = GameStatus.STARTED
            )
        }
    }

    fun onActionField(menuField:ActionsField) {
        viewModelScope.launch {
            when(menuField) {
                is ActionsField.ExpandMenu -> {
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
                is ActionsField.SelectItem<*> -> {
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

            }
        }
    }

    private suspend fun getNewQuestion(): Triple<List<Question>, Question, List<AnswerOptionsUiModel>> {
        return if(_uiState.value.questions.isEmpty()) {
            val questions = gameUseCases.getQuestion.invoke()
            val currentQuestion = questions.last()
            val optionsAnswers = currentQuestion.answerOptions.map { answerOption ->
                AnswerOptionsUiModel(
                    id = answerOption.id,
                    option = answerOption.answer,
                )
            }
            Triple(questions, currentQuestion, optionsAnswers)
        } else {
            val questions = _uiState.value.questions.toMutableList()
            questions.remove(_uiState.value.currentQuestion)
            val currentQuestion = questions.last()
            val optionsAnswers = currentQuestion.answerOptions.map { answerOption ->
                AnswerOptionsUiModel(
                    id = answerOption.id,
                    option = answerOption.answer,
                )
            }
            Triple(questions, questions.last(), optionsAnswers)
        }
    }

    fun confirmAnswer(answerId:Int) {
        viewModelScope.launch {

            when(gameUseCases.questionValidator.invoke(ID_CORRECT_ANSWER,answerId)) {
                true -> _uiState.update { triviaGameState ->
                    val optionsUpdated = highlightCorrectAnswer(triviaGameState.optionsAnswers)
                    val correctAnswersUpdated = incrementCorrectAnswers(triviaGameState)
                    triviaGameState.copy(correctAnswers = correctAnswersUpdated,isCorrectOrIncorrect = true, optionsAnswers = optionsUpdated)
                }
                else -> {
                    _uiState.update { triviaGameState ->
                        val optionsUpdated = highlightCorrectAnswer(triviaGameState.optionsAnswers)
                        triviaGameState.copy(isCorrectOrIncorrect = false, optionsAnswers = optionsUpdated)
                    }
                }
            }
            initGameOrContinueWithNewQuestions()
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

    companion object {
        private const val ID_CORRECT_ANSWER = 0
        private const val ONE_SECOND = 1000L
    }
}

enum class MenuFields {
    CATEGORY,
    TYPE,
    DIFFICULTY
}