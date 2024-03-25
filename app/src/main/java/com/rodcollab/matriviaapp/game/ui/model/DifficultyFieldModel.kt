package com.rodcollab.matriviaapp.game.ui.model

import com.rodcollab.matriviaapp.data.model.QuestionDifficulty

data class DifficultyFieldModel(
    val selected: QuestionDifficulty,
    val options: List<QuestionDifficulty>
)