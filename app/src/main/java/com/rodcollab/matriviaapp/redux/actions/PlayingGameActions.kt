package com.rodcollab.matriviaapp.redux.actions

import com.rodcollab.matriviaapp.data.model.RankingExternal
import com.rodcollab.matriviaapp.game.domain.Question
import com.rodcollab.matriviaapp.game.ui.model.AnswerOptionUiModel
import com.rodcollab.matriviaapp.game.GameStatus

sealed interface PlayingGameActions {
    data class UpdateQuestion(val triple: Triple<List<Question>, Question, List<AnswerOptionUiModel>>) :
        PlayingGameActions
    data class UpdateStatus(val gameStatus: GameStatus) : PlayingGameActions
    data object GetNewQuestion
    data class CheckAnswer(val answerId: Int) : PlayingGameActions
    data object HandleIncorrectAnswer : PlayingGameActions
    data object HandleCorrectAnswer : PlayingGameActions
    data class ContinueGame(val isCorrectOrIncorrect: Boolean) : PlayingGameActions
    data class EndOfTheGame(val ranking: List<RankingExternal>) : PlayingGameActions
    data object OnTopBarGiveUp : PlayingGameActions
    data class DisableSelection(val optionId: Int) : PlayingGameActions
    data class SelectOption(val optionId: Int) : PlayingGameActions
    data object GiveUpGameConfirm : PlayingGameActions
    data object GiveUpGameGoBack : PlayingGameActions
}