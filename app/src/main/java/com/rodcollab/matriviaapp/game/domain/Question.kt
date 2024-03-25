package com.rodcollab.matriviaapp.game.domain

import com.rodcollab.matriviaapp.redux.thunk.AnswerOption
import java.util.UUID

data class Question(
    val id: UUID = UUID.randomUUID(),
    val type: String,
    val difficulty: String,
    val category: String,
    val question: String,
    val correctAnswer: String,
    val incorrectAnswer: List<String>,
    val answerOptions: List<AnswerOption>
)
