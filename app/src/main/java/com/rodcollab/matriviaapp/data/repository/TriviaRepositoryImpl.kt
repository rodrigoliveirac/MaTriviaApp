package com.rodcollab.matriviaapp.data.repository

import com.rodcollab.matriviaapp.data.api.TriviaApi
import com.rodcollab.matriviaapp.data.model.Category
import com.rodcollab.matriviaapp.data.model.QuestionDifficulty
import com.rodcollab.matriviaapp.data.model.QuestionType
import com.rodcollab.matriviaapp.data.model.TriviaQuestion
import com.rodcollab.matriviaapp.data.model.difficulties
import com.rodcollab.matriviaapp.data.model.types
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

    override fun getQuestionTypes(): List<QuestionType> = types

}