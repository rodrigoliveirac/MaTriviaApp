package com.rodcollab.matriviaapp.redux

import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.rodcollab.matriviaapp.data.model.Category
import com.rodcollab.matriviaapp.data.model.QuestionDifficulty
import com.rodcollab.matriviaapp.data.model.QuestionType
import com.rodcollab.matriviaapp.data.model.RankingExternal
import com.rodcollab.matriviaapp.data.repository.TriviaRepository
import com.rodcollab.matriviaapp.game.domain.Question
import com.rodcollab.matriviaapp.game.domain.preferences.Preferences
import com.rodcollab.matriviaapp.game.domain.use_case.GetQuestion
import com.rodcollab.matriviaapp.game.domain.use_case.GetRanking
import com.rodcollab.matriviaapp.game.intent.EndGameActions
import com.rodcollab.matriviaapp.game.intent.GiveUpGameActions
import com.rodcollab.matriviaapp.game.intent.TimerActions
import com.rodcollab.matriviaapp.game.viewmodel.AnswerOptionsUiModel
import com.rodcollab.matriviaapp.game.viewmodel.CategoryFieldModel
import com.rodcollab.matriviaapp.game.viewmodel.DifficultyFieldModel
import com.rodcollab.matriviaapp.game.viewmodel.DropDownMenu
import com.rodcollab.matriviaapp.game.viewmodel.GameCriteriaUiModel
import com.rodcollab.matriviaapp.game.viewmodel.GameStatus
import com.rodcollab.matriviaapp.game.viewmodel.TypeFieldModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.reduxkotlin.Reducer
import org.reduxkotlin.middleware
import org.reduxkotlin.thunk.Thunk

data class GameState(
    val questions: List<Question> = listOf(),
    val gameStatus: GameStatus = GameStatus.SETUP,
    val gameCriteriaUiModel: GameCriteriaUiModel = GameCriteriaUiModel(),
    val correctAnswers: Int = 0,
    val isCorrectOrIncorrect: Boolean? = null,
    val currentQuestion: Question? = null,
    val optionsAnswers: List<AnswerOptionsUiModel> = listOf(),
    val timeIsFinished: Boolean = false,
    val confirmWithdrawal: Boolean = false,
    val disableSelection: Boolean = false,
    val timeState: Int? = null,
    val ranking: List<RankingExternal> = listOf(),
    val networkIsActive: Boolean? = null,
    val networkWarning: Boolean? = null
) {
    val numberQuestion = correctAnswers + 1
}
sealed interface NetworkActions {

    data object NetworkWarning : NetworkActions
    data object TryAgain : NetworkActions
    data class ChangeNetworkState(val network: Boolean?) : NetworkActions
}

class PrefsAndCriteriaThunkImpl(
    networkContext: CoroutineDispatcher,
    private val preferences: Preferences,
    private val repository: TriviaRepository
) : GetCategoriesThunk {

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
                    Actions.UpdateCriteriaFieldsState(
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
                dispatch(Actions.StartGame)
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

sealed interface Actions {
    data class UpdateCriteriaFieldsState(val gameCriteria: GameCriteriaUiModel) : Actions
    data object StartGame : Actions
    data object FetchCriteriaFields : Actions
    data class UpdateQuestion(val triple: Triple<List<Question>, Question, List<AnswerOptionsUiModel>>) :
        Actions

    data class CheckAnswer(val answerId: Int) : Actions
    data object HandleIncorrectAnswer : Actions
    data object HandleCorrectAnswer : Actions
    data class ContinueGame(val isCorrectOrIncorrect: Boolean) : Actions
    data class EndOfTheGame(val ranking: List<RankingExternal>) : Actions
    data object OnTopBarGiveUp : Actions

    data class DisableSelection(val optionId: Int) : Actions

}

sealed interface ExpandMenuAction : MenuGameAction {
    data object CategoryField : ExpandMenuAction
    data object TypeField : ExpandMenuAction
    data object DifficultyField : ExpandMenuAction
}

sealed interface SelectCriteriaAction : MenuGameAction {
    data class CategoryField(val questionCategory: Category) : ExpandMenuAction
    data class TypeField(val questionType: QuestionType) : ExpandMenuAction
    data class DifficultyField(val questionDifficulty: QuestionDifficulty) : ExpandMenuAction
}

data object PrepareGame : MenuGameAction

interface MenuGameAction

interface GetCategoriesThunk {
    fun getCriteriaFields(): Thunk<GameState>
    fun updatePreferences(type: Int, difficulty: Int, category: Int): Thunk<GameState>
}

sealed interface CriteriaFieldsActions {
    data object ExpandMenu : CriteriaFieldsActions
}

const val CORRECT_ANSWER_ID = 0

val reducer: Reducer<GameState> = { state, action ->
    when (action) {
        is Actions.UpdateCriteriaFieldsState -> state.copy(gameCriteriaUiModel = action.gameCriteria, networkWarning = null)
        is ExpandMenuAction.CategoryField -> {
            state.copy(
                gameCriteriaUiModel = state.gameCriteriaUiModel.copy(
                    categoryField = state.gameCriteriaUiModel.categoryField.copy(
                        expanded = !state.gameCriteriaUiModel.categoryField.expanded
                    )
                )
            )
        }

        is ExpandMenuAction.TypeField -> {
            state.copy(
                gameCriteriaUiModel = state.gameCriteriaUiModel.copy(
                    typeField = state.gameCriteriaUiModel.typeField.copy(
                        expanded = !state.gameCriteriaUiModel.typeField.expanded
                    )
                )
            )
        }

        is ExpandMenuAction.DifficultyField -> {
            state.copy(
                gameCriteriaUiModel = state.gameCriteriaUiModel.copy(
                    difficultyField = state.gameCriteriaUiModel.difficultyField.copy(
                        expanded = !state.gameCriteriaUiModel.difficultyField.expanded
                    )
                )
            )
        }

        is SelectCriteriaAction.CategoryField -> {

            val criteriaFields = state.gameCriteriaUiModel
            val categoryField = criteriaFields.categoryField

            val fieldProperty = categoryField.field
            val expandedProperty = categoryField.expanded

            state.copy(
                gameCriteriaUiModel = criteriaFields.copy(
                    categoryField = categoryField
                        .copy(
                            expanded = !expandedProperty,
                            field = fieldProperty?.copy(selected = action.questionCategory)
                        )
                )
            )
        }

        is SelectCriteriaAction.TypeField -> {

            val criteriaFields = state.gameCriteriaUiModel
            val typeField = criteriaFields.typeField

            val fieldProperty = typeField.field
            val expandedProperty = typeField.expanded

            state.copy(
                gameCriteriaUiModel = criteriaFields.copy(
                    typeField = typeField
                        .copy(
                            expanded = !expandedProperty,
                            field = fieldProperty?.copy(selected = action.questionType)
                        )
                )
            )
        }

        is SelectCriteriaAction.DifficultyField -> {

            val criteriaFields = state.gameCriteriaUiModel
            val difficultyField = criteriaFields.difficultyField

            val fieldProperty = difficultyField.field
            val expandedProperty = difficultyField.expanded

            state.copy(
                gameCriteriaUiModel = criteriaFields.copy(
                    difficultyField = difficultyField
                        .copy(
                            expanded = !expandedProperty,
                            field = fieldProperty?.copy(selected = action.questionDifficulty)
                        )
                )
            )
        }

        is PlayingGameActions.UpdateStatus -> {
            state.copy(gameStatus = action.gameStatus)
        }

        is Actions.UpdateQuestion -> {
            val questions = action.triple.first
            val currentQuestion = action.triple.second
            val optionsAnswers = action.triple.third
            val correctAnswersUpdated = incrementCorrectAnswers(state.correctAnswers)
            state.copy(
                correctAnswers = if (state.gameStatus == GameStatus.ENDED || state.gameStatus == GameStatus.SETUP) 0 else correctAnswersUpdated,
                isCorrectOrIncorrect = null,
                questions = questions,
                gameStatus = GameStatus.STARTED,
                currentQuestion = currentQuestion,
                optionsAnswers = optionsAnswers,
                timeIsFinished = false,
                confirmWithdrawal = false,
                timeState = 10,
                disableSelection = false,
                networkWarning = null
            )
        }

        is Actions.HandleCorrectAnswer -> {
            val optionsUpdated = highlightCorrectAnswer(state.optionsAnswers)
            state.copy(isCorrectOrIncorrect = true, optionsAnswers = optionsUpdated)
        }

        is Actions.HandleIncorrectAnswer -> {
            val optionsUpdated = highlightCorrectAnswer(state.optionsAnswers)
            state.copy(isCorrectOrIncorrect = false, optionsAnswers = optionsUpdated)
        }

        is Actions.DisableSelection -> {
            var optionsAnswersUiModelUpdated = state.optionsAnswers
            if (!state.disableSelection) {
                optionsAnswersUiModelUpdated =
                    optionsAnswersUiModelUpdated.map { answerOptionUiModel ->
                        if (action.optionId == answerOptionUiModel.id) {
                            answerOptionUiModel.copy(selected = !answerOptionUiModel.selected)
                        } else {
                            answerOptionUiModel.copy(selected = false)
                        }
                    }.toMutableList()
            }
            state.copy(disableSelection = true, optionsAnswers = optionsAnswersUiModelUpdated)
        }

        is Actions.EndOfTheGame -> {
            state.copy(
                gameStatus = GameStatus.ENDED,
                questions = listOf(),
                currentQuestion = null,
                isCorrectOrIncorrect = null,
                optionsAnswers = listOf(),
                timeIsFinished = false,
                disableSelection = false,
                timeState = null,
                ranking = action.ranking
            )
        }

        is EndGameActions.BackToGameSetup -> {
            state.copy(gameStatus = GameStatus.SETUP)
        }

        is Actions.OnTopBarGiveUp -> {
            state.copy(confirmWithdrawal = !state.confirmWithdrawal)
        }

        is GiveUpGameActions.GoBack -> {
            state.copy(confirmWithdrawal = !state.confirmWithdrawal)
        }

        is TimerActions.Update -> {
            state.copy(timeState = state.timeState?.minus(1))
        }

        is TimerActions.Over -> {
            val optionsUpdated = highlightCorrectAnswer(state.optionsAnswers)
            state.copy(
                isCorrectOrIncorrect = false,
                optionsAnswers = optionsUpdated,
                timeIsFinished = true
            )
        }
        is NetworkActions.ChangeNetworkState -> {
            state.copy(networkIsActive = action.network)
        }
        is NetworkActions.NetworkWarning -> {
            state.copy(networkWarning = true)
        }
        else -> {
            state.copy()
        }
    }
}

private fun incrementCorrectAnswers(correctAnswers: Int): Int {
    return correctAnswers + 1
}

sealed interface PlayingGameActions {
    data class UpdateStatus(val gameStatus: GameStatus) : PlayingGameActions
    data object GetNewQuestion
}

fun uiMiddleware(
    timerThunk: TimerThunk,
    rankingThunks: GetRanking,
    questionThunks: GetQuestion,
    categoryThunks: GetCategoriesThunk
) = middleware<GameState> { store, next, action ->
    next(action)
    val dispatch = store.dispatch
    when (action) {
        is Actions.FetchCriteriaFields -> {
            dispatch(categoryThunks.getCriteriaFields())
        }

        is PrepareGame -> {
            dispatch(
                categoryThunks.updatePreferences(
                    store.state.gameCriteriaUiModel.typeField.field?.selected?.id ?: 0,
                    store.state.gameCriteriaUiModel.difficultyField.field?.selected?.id ?: 0,
                    store.state.gameCriteriaUiModel.categoryField.field?.selected?.id ?: 0
                )
            )
        }

        is PlayingGameActions.GetNewQuestion -> {
            dispatch(questionThunks.getQuestionThunk())
        }

        is Actions.StartGame -> {
            dispatch(timerThunk.getTimerThunk())
        }

        is Actions.CheckAnswer -> {
            store.dispatch(Actions.DisableSelection(action.answerId))
            when (action.answerId == CORRECT_ANSWER_ID) {
                true -> {
                    store.dispatch(Actions.HandleCorrectAnswer)
                    store.dispatch(timerThunk.stopTimerJob())
                }

                else -> {
                    store.dispatch(Actions.HandleIncorrectAnswer)
                    store.dispatch(timerThunk.stopTimerJob())
                }
            }
        }

        is Actions.ContinueGame -> {
            when (action.isCorrectOrIncorrect) {
                true -> {
                    dispatch(timerThunk.getTimerThunk())
                }

                else -> {
                    dispatch(rankingThunks.getRanking())
                }
            }
        }

        is EndGameActions.PlayAgain -> {
            dispatch(timerThunk.getTimerThunk())
        }

        is GiveUpGameActions.Confirm -> {
            dispatch(timerThunk.stopTimerJob())
            dispatch(rankingThunks.getRanking())
        }

        is TimerActions.TimerThunkDispatcher -> {
            dispatch(timerThunk.getTimerThunk())
        }

        is NetworkActions.TryAgain -> {
            if(store.state.gameStatus == GameStatus.SETUP) {
                dispatch(Actions.FetchCriteriaFields)
            } else {
                dispatch(timerThunk.getTimerThunk())
            }
        }
        else -> {}
    }
}

private fun highlightCorrectAnswer(options: List<AnswerOptionsUiModel>): MutableList<AnswerOptionsUiModel> {
    val optionsUpdated = options.map { option ->
        if (CORRECT_ANSWER_ID == option.id) {
            option.copy(highlight = true)
        } else {
            option.copy()
        }
    }.toMutableList()
    return optionsUpdated
}
