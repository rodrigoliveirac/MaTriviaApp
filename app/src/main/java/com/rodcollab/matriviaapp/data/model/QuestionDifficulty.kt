package com.rodcollab.matriviaapp.data.model

import com.rodcollab.matriviaapp.R

data class QuestionDifficulty(
    val id: Int,
    val difficulty: Int
)

internal val difficulties = arrayListOf(
    QuestionDifficulty(
        id = 0,
        difficulty = R.string.default_value
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
