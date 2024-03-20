package com.rodcollab.matriviaapp.data.repository

import com.rodcollab.matriviaapp.R
import com.rodcollab.matriviaapp.data.api.TriviaApi
import com.rodcollab.matriviaapp.data.model.Category
import com.rodcollab.matriviaapp.data.model.QuestionDifficulty
import com.rodcollab.matriviaapp.data.model.QuestionType
import com.rodcollab.matriviaapp.data.model.TriviaQuestion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TriviaRepositoryImpl @Inject constructor(private val triviaApi: TriviaApi) :
    TriviaRepository {

    override suspend fun getCategories(): List<Category> {
       return withContext(Dispatchers.IO) {
            var categories = listOf<Category>()
            val categoryFromApi = triviaApi.getCategories()
            if(categoryFromApi.isSuccessful) {
                categoryFromApi.body()?.let { triviaCategories ->
                   categories = triviaCategories.categories
                }
            }
            categories
        }
    }

    override suspend fun getQuestions(difficulty: String, category: String, type: String): List<TriviaQuestion> {
        return withContext(Dispatchers.IO) {
            var questions = listOf<TriviaQuestion>()
            val questionFromApi = triviaApi.getQuestion(difficulty = difficulty, category = category,type = type)
            if(questionFromApi.isSuccessful) {
                questionFromApi.body()?.let { questionResults ->
                    questions = questionResults.results
                }
            }
            questions
        }
    }

    override fun getQuestionDifficulties(): List<QuestionDifficulty> = difficulties

    private val difficulties = arrayListOf(
        QuestionDifficulty(
            id = 0,
            difficulty = R.string.default_difficulty_value
        ),
        QuestionDifficulty(
            id = 1,
            difficulty = R.string.difficulty_easy
        ),
        QuestionDifficulty(
            id = 2,
            difficulty = R.string.difficulty_medium
        ),
        QuestionDifficulty(
            id = 3,
            difficulty = R.string.difficulty_hard
        )
    )

    override fun getQuestionTypes(): List<QuestionType> = types

    private val types = arrayListOf(
            QuestionType(
                id = 0,
                type = R.string.default_type_value
            ),
            QuestionType(
                id = 1,
                type = R.string.type_multiple
            ),
            QuestionType(
                id = 2,
                type = R.string.type_boolean
            )
        )

}