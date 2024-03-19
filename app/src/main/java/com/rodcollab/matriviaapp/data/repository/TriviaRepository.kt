package com.rodcollab.matriviaapp.data.repository

import com.rodcollab.matriviaapp.data.model.Category
import com.rodcollab.matriviaapp.data.model.QuestionDifficulty
import com.rodcollab.matriviaapp.data.model.QuestionType
import com.rodcollab.matriviaapp.data.model.TriviaQuestion

interface TriviaRepository {
    suspend fun getCategories() : List<Category>
    suspend fun getQuestions(difficulty: String, category: String, type: String) : List<TriviaQuestion>
    fun getQuestionTypes() : List<QuestionType>
    fun getQuestionDifficulties() : List<QuestionDifficulty>
}