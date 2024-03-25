package com.rodcollab.matriviaapp.game.ui.model

import com.rodcollab.matriviaapp.data.model.Category

data class CategoryFieldModel(
    val selected: Category,
    val options: List<Category>
)