package com.rodcollab.matriviaapp.data.model

import com.rodcollab.matriviaapp.R

data class QuestionType(
    val id: Int,
    val type: Int
)

internal val types = arrayListOf(
    QuestionType(
        id = 0,
        type = R.string.default_value
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