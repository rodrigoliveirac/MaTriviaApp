package com.rodcollab.matriviaapp.redux.actions

import com.rodcollab.matriviaapp.data.model.Category
import com.rodcollab.matriviaapp.data.model.QuestionDifficulty
import com.rodcollab.matriviaapp.data.model.QuestionType
import com.rodcollab.matriviaapp.game.viewmodel.GameCriteriaUiModel

sealed interface MenuGameActions {
    data object ExpandMenuCategoryField : MenuGameActions
    data object ExpandMenuTypeField : MenuGameActions
    data object ExpandMenuDifficultyField : MenuGameActions
    data class OnSelectCategoryField(val questionCategory: Category) :
        MenuGameActions
    data class OnSelectTypeField(val questionType: QuestionType) :
        MenuGameActions
    data class OnSelectDifficultyField(val questionDifficulty: QuestionDifficulty) :
        MenuGameActions
    data object PrepareGame : MenuGameActions
    data object StartGame : MenuGameActions
    data class UpdateCriteriaFieldsState(val gameCriteria: GameCriteriaUiModel) :
        MenuGameActions
    data object FetchCriteriaFields :
        MenuGameActions
    data object GiveUpGameConfirm : MenuGameActions
    data object GiveUpGameGoBack : MenuGameActions
}