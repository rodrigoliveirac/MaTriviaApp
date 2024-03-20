package com.rodcollab.matriviaapp.game.domain.use_case

import com.rodcollab.matriviaapp.data.model.TriviaQuestion
import com.rodcollab.matriviaapp.data.repository.TriviaRepository
import com.rodcollab.matriviaapp.game.domain.Question
import com.rodcollab.matriviaapp.game.domain.preferences.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GetQuestionImpl(
    private val sharedPreferences: Preferences,
    private val triviaRepository: TriviaRepository
) : GetQuestion {

    override suspend fun invoke(
    ): List<Question> {

            var typePrefs: String = sharedPreferences.getQuestionType().toString()
            var difficultyPrefs: String = sharedPreferences.getQuestionDifficulty().toString()
            var categoryPrefs: String = sharedPreferences.getQuestionCategory().toString()

            if (typePrefs == ANY) {
                typePrefs = DEFAULT
            }

            if(difficultyPrefs == ANY) {
                difficultyPrefs = DEFAULT
            }

            if(categoryPrefs == ANY) {
                categoryPrefs = DEFAULT
            }

        return triviaRepository.getQuestions(
                    difficulty = difficultyPrefs,
                    type = typePrefs,
                    category = categoryPrefs)
                    .map { triviaQuestion ->

                    val randomOptions = answerOptions(triviaQuestion)

                    Question(
                        type = triviaQuestion.type,
                        difficulty = triviaQuestion.difficulty,
                        category = triviaQuestion.category,
                        question = triviaQuestion.question,
                        correctAnswer = triviaQuestion.correctAnswer,
                        incorrectAnswer = triviaQuestion.incorrectAnswer,
                        answerOptions = randomOptions
                    )
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
                                answer = triviaQuestion.correctAnswer
                            )
                        )
                        id++
                        answerOptions.add(AnswerOption(id = id, answer = answer))
                    } else {
                        answerOptions.add(AnswerOption(id = id, answer = answer))
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
    }
}

data class AnswerOption(
    val id: Int,
    val answer: String
)