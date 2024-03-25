package com.rodcollab.matriviaapp.redux.thunk

import androidx.core.text.HtmlCompat
import com.rodcollab.matriviaapp.data.model.TriviaQuestion
import com.rodcollab.matriviaapp.data.repository.TriviaRepository
import com.rodcollab.matriviaapp.di.DefaultDispatcher
import com.rodcollab.matriviaapp.game.GameState
import com.rodcollab.matriviaapp.game.domain.Question
import com.rodcollab.matriviaapp.redux.actions.NetworkActions
import com.rodcollab.matriviaapp.redux.actions.PlayingGameActions
import com.rodcollab.matriviaapp.game.ui.model.AnswerOptionUiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.reduxkotlin.thunk.Thunk

class GetQuestionThunkImpl(
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
    private val triviaRepository: TriviaRepository
) : GetQuestionThunk {
    private val scope = CoroutineScope(dispatcher + Job())

    override fun getQuestionThunk(): Thunk<GameState> = { dispatch, getState, _ ->
        scope.launch {
            val gameState = getState()
            delay(500L)
            if(gameState.questions.isEmpty()) {

                gameState.networkIsActive?.let {
                    var typePrefs = gameState.gameCriteriaUiModel.typeField.field?.selected?.id.toString()
                    var difficultyPrefs = gameState.gameCriteriaUiModel.difficultyField.field?.selected?.id.toString()
                    var categoryPrefs = gameState.gameCriteriaUiModel.categoryField.field?.selected?.id.toString()

                    typePrefs = if (typePrefs == ANY) {
                        DEFAULT
                    } else {
                        when (typePrefs) {
                            ID_MULTIPLE_TYPE -> MULTIPLE_TYPE
                            else -> {
                                BOOLEAN_TYPE
                            }
                        }
                    }
                    difficultyPrefs = if (difficultyPrefs == ANY) {
                        DEFAULT
                    } else {
                        when (difficultyPrefs) {
                            ID_EASY_DIFFICULT -> EASY_DIFFICULT
                            ID_MEDIUM_DIFFICULT -> MEDIUM_DIFFICULT
                            else -> {
                                HARD_DIFFICULT
                            }
                        }
                    }
                    if (categoryPrefs == ANY) {
                        categoryPrefs = DEFAULT
                    }
                    val newQuestions = async {
                        triviaRepository.getQuestions(
                            difficulty = difficultyPrefs,
                            type = typePrefs,
                            category = categoryPrefs
                        )
                            .map { triviaQuestion ->

                                val randomOptions = answerOptions(triviaQuestion)

                                val fromApi = triviaQuestion.question
                                val textFromHtmlFromApi = HtmlCompat.fromHtml(fromApi, HtmlCompat.FROM_HTML_MODE_LEGACY)

                                Question(
                                    type = triviaQuestion.type,
                                    difficulty = triviaQuestion.difficulty,
                                    category = triviaQuestion.category,
                                    question = textFromHtmlFromApi.toString(),
                                    correctAnswer = triviaQuestion.correctAnswer,
                                    incorrectAnswer = triviaQuestion.incorrectAnswer,
                                    answerOptions = randomOptions
                                )
                            }
                    }.await()
                    while(newQuestions.isEmpty()) {
                        delay(1)
                    }
                    val questions = newQuestions.toMutableList()
                    val currentQuestion = questions.last()
                    questions.remove(currentQuestion)
                    val optionsAnswers = currentQuestion.answerOptions.map { answerOption ->
                        AnswerOptionUiModel(
                            id = answerOption.id,
                            option = answerOption.answer,
                        )
                    }
                    dispatch(
                        PlayingGameActions.UpdateQuestion(
                            Triple(
                                questions,
                                currentQuestion,
                                optionsAnswers
                            )
                        )
                    )
                } ?: run { dispatch(NetworkActions.NetworkWarning) }
            } else {
                val triple = getQuestionsFromCache(gameState)
                dispatch(
                    PlayingGameActions.UpdateQuestion(
                        Triple(
                            triple.first,
                            triple.second,
                            triple.third
                        )
                    )
                )
            }
        }
    }

    private fun getQuestionsFromCache(
        gameState: GameState,
    ): Triple<List<Question>,Question,List<AnswerOptionUiModel>> {
        val questions = gameState.questions.toMutableList()
        val currentQuestion = questions.last()
        questions.remove(currentQuestion)
        val optionsAnswers = currentQuestion.answerOptions.map { answerOption ->
            AnswerOptionUiModel(
                id = answerOption.id,
                option = answerOption.answer,
            )
        }
        return Triple(questions, currentQuestion, optionsAnswers)
    }

    private suspend fun answerOptions(triviaQuestion: TriviaQuestion): MutableList<AnswerOption> {
        return coroutineScope {
            async {
                val answerOptions = mutableListOf<AnswerOption>()
                var id = 0
                triviaQuestion.incorrectAnswer.forEach { answer ->

                    if (id == 0) {
                        answerOptions.add(
                            AnswerOption(
                                id = id,
                                answer = HtmlCompat.fromHtml(triviaQuestion.correctAnswer, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                            )
                        )
                        id++
                        answerOptions.add(AnswerOption(id = id, answer =  HtmlCompat.fromHtml(answer, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()))
                    } else {
                        answerOptions.add(AnswerOption(id = id, answer = HtmlCompat.fromHtml(answer, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()))
                    }
                    id++
                }
                answerOptions.shuffle()
                answerOptions
            }.await()
        }
    }

    companion object {
        const val ANY = "0"
        const val DEFAULT = ""

        const val EASY_DIFFICULT = "easy"
        const val MEDIUM_DIFFICULT = "medium"
        const val HARD_DIFFICULT = "hard"

        const val MULTIPLE_TYPE = "multiple"
        const val BOOLEAN_TYPE = "boolean"
        const val ID_MULTIPLE_TYPE = "1"

        const val ID_EASY_DIFFICULT = "1"
        const val ID_MEDIUM_DIFFICULT = "2"

    }
}

data class AnswerOption(
    val id: Int,
    val answer: String
)