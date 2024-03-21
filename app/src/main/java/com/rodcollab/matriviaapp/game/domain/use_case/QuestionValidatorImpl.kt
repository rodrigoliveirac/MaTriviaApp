package com.rodcollab.matriviaapp.game.domain.use_case

class QuestionValidatorImpl : QuestionValidator {
    override suspend fun invoke(correctAnswer: Int, answer: Int): Boolean {
        return correctAnswer == answer
    }
}