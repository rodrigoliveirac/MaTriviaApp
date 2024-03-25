package com.rodcollab.matriviaapp.game

import com.rodcollab.matriviaapp.data.model.RankingExternal
import com.rodcollab.matriviaapp.game.domain.Question
import com.rodcollab.matriviaapp.game.ui.model.AnswerOptionUiModel
import com.rodcollab.matriviaapp.game.ui.model.GameCriteriaUiModel

data class GameState(
    val questions: List<Question> = listOf(),
    val gameStatus: GameStatus = GameStatus.SETUP,
    val gameCriteriaUiModel: GameCriteriaUiModel = GameCriteriaUiModel(),
    val correctAnswers: Int = 0,
    val isCorrectOrIncorrect: Boolean? = null,
    val currentQuestion: Question? = null,
    val optionsAnswers: List<AnswerOptionUiModel> = listOf(),
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
