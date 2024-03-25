package com.rodcollab.matriviaapp.game.ui.model

data class GameCriteriaUiModel(
    val typeField: DropDownMenu<TypeFieldModel?> = DropDownMenu(field = null),
    val difficultyField: DropDownMenu<DifficultyFieldModel?> = DropDownMenu(field = null),
    val categoryField: DropDownMenu<CategoryFieldModel?> = DropDownMenu(field = null)
)
