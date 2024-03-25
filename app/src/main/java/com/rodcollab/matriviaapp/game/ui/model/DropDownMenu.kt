package com.rodcollab.matriviaapp.game.ui.model

data class DropDownMenu<T>(
    val expanded: Boolean = false,
    val field: T
)