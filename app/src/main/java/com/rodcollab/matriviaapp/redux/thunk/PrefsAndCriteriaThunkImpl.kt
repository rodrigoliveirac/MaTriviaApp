package com.rodcollab.matriviaapp.redux.thunk

import android.util.Log
import com.rodcollab.matriviaapp.data.model.Category
import com.rodcollab.matriviaapp.data.model.QuestionDifficulty
import com.rodcollab.matriviaapp.data.model.QuestionType
import com.rodcollab.matriviaapp.data.repository.TriviaRepository
import com.rodcollab.matriviaapp.game.GameState
import com.rodcollab.matriviaapp.game.domain.preferences.Preferences
import com.rodcollab.matriviaapp.redux.actions.MenuGameActions
import com.rodcollab.matriviaapp.redux.actions.NetworkActions
import com.rodcollab.matriviaapp.game.viewmodel.CategoryFieldModel
import com.rodcollab.matriviaapp.game.viewmodel.DifficultyFieldModel
import com.rodcollab.matriviaapp.game.viewmodel.DropDownMenu
import com.rodcollab.matriviaapp.game.viewmodel.GameCriteriaUiModel
import com.rodcollab.matriviaapp.game.viewmodel.TypeFieldModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.reduxkotlin.thunk.Thunk

class PrefsAndCriteriaThunkImpl(
    networkContext: CoroutineDispatcher,
    private val preferences: Preferences,
    private val repository: TriviaRepository
) : PrefsAndCriteriaThunk {

    private val scope = CoroutineScope(networkContext)

    private val categories = mutableListOf<Category>()
    private val difficulties = repository.getQuestionDifficulties()
    private val types = repository.getQuestionTypes()

    override fun getCriteriaFields(): Thunk<GameState> = { dispatch, getState, _ ->
        scope.launch {

            val state = getState()
            state.networkIsActive?.let {
                categories.ifEmpty {
                    async {
                        repository.getCategories().forEach { category ->
                            categories.add(category)
                        }
                        if (categories.find { it.id == 0 } == null) {
                            categories.add(Category(id = 0, name = defaultValue))
                        }
                        categories
                    }.await()
                }

                val typeField = TypeFieldModel(
                    selected = getQuestionTypeFromIndex(preferences.getQuestionType()),
                    options = types
                )
                val difficulty = DifficultyFieldModel(
                    selected = getQuestionDifficultyFromIndex(preferences.getQuestionDifficulty()),
                    options = difficulties
                )
                val categories = CategoryFieldModel(
                    selected = getQuestionCategoryFromId(preferences.getQuestionCategory()),
                    options = categories
                )

                dispatch(
                    MenuGameActions.UpdateCriteriaFieldsState(
                        GameCriteriaUiModel(
                            typeField = DropDownMenu(field = typeField),
                            difficultyField = DropDownMenu(field = difficulty),
                            categoryField = DropDownMenu(field = categories)
                        )
                    )
                )
            } ?: run { dispatch(NetworkActions.NetworkWarning) }
        }
    }

    override fun updatePreferences(type: Int, difficulty: Int, category: Int): Thunk<GameState> =
        { dispatch, _, _ ->
            scope.launch {
                preferences.updateGamePrefs(type, difficulty, category)
                Log.d(
                    "PREFERENCES_LOGGER",
                    "PREFS_TYPE : ${preferences.getQuestionType()} " +
                            "\n PREFS_DIFFICULTY: ${preferences.getQuestionDifficulty()} " +
                            "\n PREFS_CATEGORY: ${preferences.getQuestionCategory()} "
                )
                dispatch(MenuGameActions.StartGame)
            }
        }

    private fun getQuestionDifficultyFromIndex(index: Int): QuestionDifficulty {
        return difficulties[index]
    }

    private fun getQuestionTypeFromIndex(index: Int): QuestionType {
        return types[index]
    }

    private fun getQuestionCategoryFromId(id: Int): Category {
        val category = categories.indexOfFirst { it.id == id }
        return categories[category]
    }

    companion object {
        private const val defaultValue = "Any Category"
    }
}