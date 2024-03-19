package com.rodcollab.matriviaapp.game.domain

import com.rodcollab.matriviaapp.data.model.Category
import com.rodcollab.matriviaapp.data.model.QuestionDifficulty
import com.rodcollab.matriviaapp.data.model.QuestionType

data class GameCriteria(
    val types: List<QuestionType>,
    val difficulties: List<QuestionDifficulty>,
    val categories: List<Category>
)
