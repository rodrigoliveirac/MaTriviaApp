package com.rodcollab.matriviaapp.data.model

import com.rodcollab.matriviaapp.R

data class QuestionDifficulty(
    val id: Int,
    val difficulty: Int
)

internal val difficulties = listOf(
    QuestionDifficulty(
        id = 0,
        difficulty = R.string.difficulty_easy
    ),
    QuestionDifficulty(
        id = 1,
        difficulty = R.string.difficulty_medium
    ),
    QuestionDifficulty(
        id = 2,
        difficulty = R.string.difficulty_hard
    )
)
