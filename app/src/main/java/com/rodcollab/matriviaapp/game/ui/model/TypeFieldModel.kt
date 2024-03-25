package com.rodcollab.matriviaapp.game.ui.model

import com.rodcollab.matriviaapp.data.model.QuestionType

data class TypeFieldModel(
    val selected: QuestionType,
    val options: List<QuestionType>
)