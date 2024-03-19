package com.rodcollab.matriviaapp.game.domain.preferences

interface Preferences {
    suspend fun updateGamePrefs(type: Int,difficulty: Int, category: Int)
    suspend fun getQuestionType(): Int
    suspend fun getQuestionDifficulty() : Int
    suspend fun getQuestionCategory() : Int
}