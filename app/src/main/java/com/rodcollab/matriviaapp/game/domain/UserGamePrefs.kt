package com.rodcollab.matriviaapp.game.domain

import com.rodcollab.matriviaapp.data.model.Category
import com.rodcollab.matriviaapp.data.model.QuestionDifficulty
import com.rodcollab.matriviaapp.data.model.QuestionType

data class UserGamePrefs(
    val difficulty: QuestionDifficulty,
    val type: QuestionType,
    val category: Category
)
