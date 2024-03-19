package com.rodcollab.matriviaapp.game.domain.use_case

interface QuestionValidator {
    suspend operator fun invoke(correctAnswer: Int, answer: Int): Boolean
}