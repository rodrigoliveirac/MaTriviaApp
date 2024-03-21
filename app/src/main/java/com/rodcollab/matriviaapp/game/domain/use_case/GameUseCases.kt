package com.rodcollab.matriviaapp.game.domain.use_case

data class GameUseCases(
    val getQuestion: GetQuestion,
    val showPrefsAndCriteria: ShowPrefsAndCriteria,
    val questionValidator : QuestionValidator,
    val insertRanking: InsertRanking,
    val getRanking: GetRanking
)
