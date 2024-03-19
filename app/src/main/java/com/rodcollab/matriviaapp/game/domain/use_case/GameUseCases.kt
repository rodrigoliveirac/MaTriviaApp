package com.rodcollab.matriviaapp.game.domain.use_case

data class GameUseCases(
    val getQuestion: GetQuestion,
    val showPrefs: ShowPrefs,
    val questionValidator : QuestionValidator
)
