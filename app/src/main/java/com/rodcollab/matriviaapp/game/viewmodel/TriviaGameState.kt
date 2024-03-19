package com.rodcollab.matriviaapp.game.viewmodel

import com.rodcollab.matriviaapp.game.domain.Question

data class TriviaGameState(
    val correctAnswers: Int = 0,
    val currentState: GameStatus = GameStatus.PREP,
    val questions: List<Question> = listOf(),
    val currentQuestion: Question? = null,
    val currentCorrectAnswerId: Int? = null,
    val isCorrectOrIncorrect: Boolean? = null,
    val optionsAnswers: List<AnswerOptionsUiModel> = listOf(),
    val isLoading: Boolean = false
)