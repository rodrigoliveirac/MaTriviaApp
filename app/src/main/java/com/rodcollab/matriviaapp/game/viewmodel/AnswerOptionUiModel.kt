package com.rodcollab.matriviaapp.game.viewmodel

data class AnswerOptionsUiModel(
    val id: Int,
    val selected: Boolean = false,
    val option: String,
    val highlight: Boolean = false
)