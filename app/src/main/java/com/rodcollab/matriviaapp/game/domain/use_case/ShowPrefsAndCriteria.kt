package com.rodcollab.matriviaapp.game.domain.use_case

import com.rodcollab.matriviaapp.game.viewmodel.CategoryFieldModel
import com.rodcollab.matriviaapp.game.viewmodel.DifficultyFieldModel
import com.rodcollab.matriviaapp.game.viewmodel.TypeFieldModel

interface ShowPrefsAndCriteria {
    suspend operator fun invoke(): Triple<TypeFieldModel, DifficultyFieldModel, CategoryFieldModel>
}