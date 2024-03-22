package com.rodcollab.matriviaapp.game.domain.use_case

import androidx.core.text.HtmlCompat
import com.rodcollab.matriviaapp.data.model.TriviaQuestion
import com.rodcollab.matriviaapp.data.repository.TriviaRepository
import com.rodcollab.matriviaapp.di.DefaultDispatcher
import com.rodcollab.matriviaapp.game.domain.Question
import com.rodcollab.matriviaapp.game.domain.preferences.Preferences
import com.rodcollab.matriviaapp.game.viewmodel.AnswerOptionsUiModel
import com.rodcollab.matriviaapp.redux.Actions
import com.rodcollab.matriviaapp.redux.GameState
import com.rodcollab.matriviaapp.redux.PlayingGameActions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.reduxkotlin.thunk.Thunk
import kotlin.coroutines.CoroutineContext

class GetQuestionImpl(
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
    private val sharedPreferences: Preferences,
    private val triviaRepository: TriviaRepository
) : GetQuestion {
    private val scope = CoroutineScope(dispatcher)
    override suspend fun invoke(
    ): List<Question> {

        var typePrefs: String = sharedPreferences.getQuestionType().toString()
        var difficultyPrefs: String = sharedPreferences.getQuestionDifficulty().toString()
        var categoryPrefs: String = sharedPreferences.getQuestionCategory().toString()

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

        return triviaRepository.getQuestions(
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
    }

    override fun getQuestionThunk(): Thunk<GameState> = { dispatch, getState, _ ->
        scope.launch {

            val gameState = getState()
            var typePrefs = gameState.gameCriteriaUiModel.typeField.toString()
            var difficultyPrefs = gameState.gameCriteriaUiModel.difficultyField.toString()
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
            val questions = newQuestions.toMutableList()
            val currentQuestion = questions.last()
            questions.remove(currentQuestion)
            val optionsAnswers = currentQuestion.answerOptions.map { answerOption ->
                AnswerOptionsUiModel(
                    id = answerOption.id,
                    option = answerOption.answer,
                )
            }
            dispatch(Actions.UpdateQuestion(Triple(questions, currentQuestion, optionsAnswers)))
        }
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