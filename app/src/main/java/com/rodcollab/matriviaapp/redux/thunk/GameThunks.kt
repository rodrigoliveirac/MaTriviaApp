package com.rodcollab.matriviaapp.redux.thunk

data class GameThunks(
    val getQuestion: GetQuestionThunk,
    val getRanking: GetRankingThunk,
    val getCategories: PrefsAndCriteriaThunk,
    val timerThunk: TimerThunk
)
