package com.rodcollab.matriviaapp.redux

import com.rodcollab.matriviaapp.game.GameState
import com.rodcollab.matriviaapp.redux.thunk.GetQuestionThunk
import com.rodcollab.matriviaapp.redux.thunk.GetRankingThunk
import com.rodcollab.matriviaapp.redux.actions.EndGameActions
import com.rodcollab.matriviaapp.redux.thunk.PrefsAndCriteriaThunk
import com.rodcollab.matriviaapp.redux.actions.MenuGameActions
import com.rodcollab.matriviaapp.redux.actions.NetworkActions
import com.rodcollab.matriviaapp.redux.actions.PlayingGameActions
import com.rodcollab.matriviaapp.redux.actions.TimerActions
import com.rodcollab.matriviaapp.game.GameStatus
import com.rodcollab.matriviaapp.redux.thunk.TimerThunk
import org.reduxkotlin.middleware

fun uiMiddleware(
    timerThunk: TimerThunk,
    rankingThunks: GetRankingThunk,
    questionThunks: GetQuestionThunk,
    categoryThunks: PrefsAndCriteriaThunk
) = middleware<GameState> { store, next, action ->
    next(action)
    val dispatch = store.dispatch
    when (action) {
        //======================
        // MENU GAME ACTIONS
        //======================
        is MenuGameActions.PrepareGame -> {
            dispatch(
                categoryThunks.updatePreferences(
                    store.state.gameCriteriaUiModel.typeField.field?.selected?.id ?: 0,
                    store.state.gameCriteriaUiModel.difficultyField.field?.selected?.id ?: 0,
                    store.state.gameCriteriaUiModel.categoryField.field?.selected?.id ?: 0
                )
            )
        }
        is MenuGameActions.FetchCriteriaFields -> {
            dispatch(categoryThunks.getCriteriaFields())
        }
        is MenuGameActions.StartGame -> {
            dispatch(timerThunk.getTimerThunk())
        }
        //======================
        // PLAYING GAME ACTIONS
        //======================
        is PlayingGameActions.CheckAnswer -> {
            store.dispatch(PlayingGameActions.DisableSelection(action.answerId))
            when (action.answerId == CORRECT_ANSWER_ID) {
                true -> {
                    store.dispatch(PlayingGameActions.HandleCorrectAnswer)
                    store.dispatch(timerThunk.stopTimerJob())
                }

                else -> {
                    store.dispatch(PlayingGameActions.HandleIncorrectAnswer)
                    store.dispatch(timerThunk.stopTimerJob())
                }
            }
        }
        is PlayingGameActions.ContinueGame -> {
            when (action.isCorrectOrIncorrect) {
                true -> {
                    dispatch(timerThunk.getTimerThunk())
                }

                else -> {
                    dispatch(rankingThunks.getRanking())
                }
            }
        }
        is PlayingGameActions.GetNewQuestion -> {
            dispatch(questionThunks.getQuestionThunk())
        }
        is PlayingGameActions.GiveUpGameConfirm -> {
            dispatch(timerThunk.stopTimerJob())
            dispatch(rankingThunks.getRanking())
        }
        //======================
        // END GAME ACTIONS
        //======================
        is EndGameActions.PlayAgain -> {
            dispatch(timerThunk.getTimerThunk())
        }
        //======================
        // TIME GAME ACTIONS
        //======================
        is TimerActions.TimerThunkDispatcher -> {
            dispatch(timerThunk.getTimerThunk())
        }
        //======================
        // NETWORK GAME ACTIONS
        //======================
        is NetworkActions.TryAgain -> {
            if(store.state.gameStatus == GameStatus.SETUP) {
                dispatch(MenuGameActions.FetchCriteriaFields)
            } else {
                dispatch(timerThunk.getTimerThunk())
            }
        }
        else -> {}
    }
}