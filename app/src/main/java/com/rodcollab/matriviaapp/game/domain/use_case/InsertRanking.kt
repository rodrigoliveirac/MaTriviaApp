package com.rodcollab.matriviaapp.game.domain.use_case

interface InsertRanking {
    suspend operator fun invoke(correctAnswers: Int)
}