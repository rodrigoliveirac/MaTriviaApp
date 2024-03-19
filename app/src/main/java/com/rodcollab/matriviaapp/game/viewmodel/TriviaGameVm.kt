package com.rodcollab.matriviaapp.game.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodcollab.matriviaapp.game.domain.Question
import com.rodcollab.matriviaapp.game.domain.preferences.Preferences
import com.rodcollab.matriviaapp.game.domain.use_case.GameUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class GameStatus {
    PREP,
    STARTED,
    ENDED
}
data class TriviaGameState(
    val correctAnswers: Int = 0,
    val currentState: GameStatus = GameStatus.PREP,
    val questions: List<Question> = listOf(),
    val currentQuestion: Question? = null,
    val currentCorrectAnswerId: Int? = null,
    val isCorrectOrIncorrect: Boolean? = null,
    val optionsAnswers: List<AnswerOptionsUiModel> = listOf(),
    val isLoading: Boolean = false
)

data class AnswerOptionsUiModel(
    val id: Int,
    val option: String,
    val highlight: Boolean = false
)
@HiltViewModel
class TriviaGameVm @Inject constructor(
    private val preferences: Preferences,
    private val gameUseCases: GameUseCases
) : ViewModel() {

    private val _uiState: MutableStateFlow<TriviaGameState> by lazy {
        MutableStateFlow(TriviaGameState())
    }
    val uiState: StateFlow<TriviaGameState> = _uiState.asStateFlow()

    fun startGame() {
        viewModelScope.launch {
            initGameOrContinueWithNewQuestions()
        }
    }

    private suspend fun TriviaGameVm.initGameOrContinueWithNewQuestions() {
        _uiState.update {
            it.copy(isLoading = true)
        }
        val (questions, currentQuestion, optionsAnswers) = getNewQuestions()
        _uiState.update { triviaGameState ->
            triviaGameState.copy(
                questions = questions,
                currentQuestion = currentQuestion,
                optionsAnswers = optionsAnswers,
                isLoading = false
            )
        }
    }

    private suspend fun getNewQuestions(): Triple<List<Question>, Question, List<AnswerOptionsUiModel>> {
        val questions = gameUseCases.getQuestion.invoke()
        val currentQuestion = questions.last()
        val optionsAnswers = currentQuestion.answerOptions.map { answerOption ->
            AnswerOptionsUiModel(
                id = answerOption.id,
                option = answerOption.answer,
            )
        }
        return Triple(questions, currentQuestion, optionsAnswers)
    }

    fun confirmAnswer(answerId:Int) {
        viewModelScope.launch {
            val questions = _uiState.value.questions.toMutableList()
            questions.remove(_uiState.value.currentQuestion)
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
            if (questions.isEmpty()) {
                initGameOrContinueWithNewQuestions()
            } else {
                delay(ONE_SECOND)
                _uiState.update { triviaGameState ->
                    triviaGameState.copy(isCorrectOrIncorrect = null, currentQuestion = questions.last())
                }
            }
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
        const val ID_CORRECT_ANSWER = 0
        const val ONE_SECOND = 1000L
    }
}