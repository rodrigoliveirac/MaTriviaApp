package com.rodcollab.matriviaapp.game.viewmodel

import com.rodcollab.matriviaapp.data.model.Category
import com.rodcollab.matriviaapp.data.model.QuestionDifficulty
import com.rodcollab.matriviaapp.data.model.QuestionType
import com.rodcollab.matriviaapp.game.domain.Question

data class TriviaGameState(
    val correctAnswers: Int = 0,
    val currentState: GameStatus = GameStatus.PREP,
    val questions: List<Question> = listOf(),
    val currentQuestion: Question? = null,
    val currentCorrectAnswerId: Int? = null,
    val isCorrectOrIncorrect: Boolean? = null,
    val optionsAnswers: List<AnswerOptionsUiModel> = listOf(),
    val isLoading: Boolean = false,
    val criteriaFields: GameCriteriaUiModel? = GameCriteriaUiModel(),
    val currentOptionIdSelected: Int? = null
)

data class GameCriteriaUiModel(
    val typeField: DropDownMenu<TypeFieldModel?> = DropDownMenu(field = null),
    val difficultyField: DropDownMenu<DifficultyFieldModel?> = DropDownMenu(field = null),
    val categoryField: DropDownMenu<CategoryFieldModel?> = DropDownMenu(field = null)
)

data class DropDownMenu<T>(
    val expanded: Boolean = false,
    val field: T
)

data class TypeFieldModel(
    val selected: QuestionType,
    val options: List<QuestionType>
)

data class DifficultyFieldModel(
    val selected: QuestionDifficulty,
    val options: List<QuestionDifficulty>
)

data class CategoryFieldModel(
    val selected: Category,
    val options: List<Category>
)

