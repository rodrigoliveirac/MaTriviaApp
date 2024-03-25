package com.rodcollab.matriviaapp.game.ui.model

data class AnswerOptionUiModel(
    val id: Int,
    val selected: Boolean = false,
    val option: String,
    val highlight: Boolean = false
)