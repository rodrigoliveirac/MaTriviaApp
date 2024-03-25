package com.rodcollab.matriviaapp.redux

import com.rodcollab.matriviaapp.game.GameState
import com.rodcollab.matriviaapp.redux.actions.EndGameActions
import com.rodcollab.matriviaapp.redux.actions.MenuGameActions
import com.rodcollab.matriviaapp.redux.actions.NetworkActions
import com.rodcollab.matriviaapp.redux.actions.PlayingGameActions
import com.rodcollab.matriviaapp.redux.actions.TimerActions
import com.rodcollab.matriviaapp.game.ui.model.AnswerOptionUiModel
import com.rodcollab.matriviaapp.game.GameStatus
import org.reduxkotlin.Reducer

const val CORRECT_ANSWER_ID = 0

val reducer: Reducer<GameState> = { state, action ->
    when (action) {
        //======================
        // MENU GAME ACTIONS
        //======================
        is MenuGameActions.UpdateCriteriaFieldsState -> state.copy(gameCriteriaUiModel = action.gameCriteria, networkWarning = null)
        is MenuGameActions.ExpandMenuCategoryField -> {
            state.copy(
                gameCriteriaUiModel = state.gameCriteriaUiModel.copy(
                    categoryField = state.gameCriteriaUiModel.categoryField.copy(
                        expanded = !state.gameCriteriaUiModel.categoryField.expanded
                    )
                )
            )
        }
        is MenuGameActions.ExpandMenuTypeField -> {
            state.copy(
                gameCriteriaUiModel = state.gameCriteriaUiModel.copy(
                    typeField = state.gameCriteriaUiModel.typeField.copy(
                        expanded = !state.gameCriteriaUiModel.typeField.expanded
                    )
                )
            )
        }
        is MenuGameActions.ExpandMenuDifficultyField -> {
            state.copy(
                gameCriteriaUiModel = state.gameCriteriaUiModel.copy(
                    difficultyField = state.gameCriteriaUiModel.difficultyField.copy(
                        expanded = !state.gameCriteriaUiModel.difficultyField.expanded
                    )
                )
            )
        }
        is MenuGameActions.OnSelectCategoryField -> {

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
        is MenuGameActions.OnSelectTypeField -> {

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
        is MenuGameActions.OnSelectDifficultyField -> {

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
        //======================
        // PLAYING GAME ACTIONS
        //======================
        is PlayingGameActions.UpdateStatus -> {
            state.copy(gameStatus = action.gameStatus)
        }
        is PlayingGameActions.UpdateQuestion -> {
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
        is PlayingGameActions.HandleCorrectAnswer -> {
            val optionsUpdated = highlightCorrectAnswer(state.optionsAnswers)
            state.copy(isCorrectOrIncorrect = true, optionsAnswers = optionsUpdated)
        }
        is PlayingGameActions.HandleIncorrectAnswer -> {
            val optionsUpdated = highlightCorrectAnswer(state.optionsAnswers)
            state.copy(isCorrectOrIncorrect = false, optionsAnswers = optionsUpdated)
        }
        is PlayingGameActions.DisableSelection -> {
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
        is PlayingGameActions.EndOfTheGame -> {
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
        is PlayingGameActions.OnTopBarGiveUp -> {
            state.copy(confirmWithdrawal = !state.confirmWithdrawal)
        }
        is PlayingGameActions.GiveUpGameGoBack -> {
            state.copy(confirmWithdrawal = !state.confirmWithdrawal)
        }
        //======================
        // END GAME ACTIONS
        //======================
        is EndGameActions.BackToGameSetup -> {
            state.copy(gameStatus = GameStatus.SETUP)
        }
        //======================
        // TIMER GAME ACTIONS
        //======================
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
        //======================
        // NETWORK GAME ACTIONS
        //======================
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

private fun highlightCorrectAnswer(options: List<AnswerOptionUiModel>): MutableList<AnswerOptionUiModel> {
    val optionsUpdated = options.map { option ->
        if (CORRECT_ANSWER_ID == option.id) {
            option.copy(highlight = true)
        } else {
            option.copy()
        }
    }.toMutableList()
    return optionsUpdated
}
