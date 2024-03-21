package com.rodcollab.matriviaapp.game.domain.preferences

import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PreferencesImpl (private val sharedPreferences: SharedPreferences) : Preferences {

    override suspend fun updateGamePrefs(type: Int, difficulty: Int, category: Int) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit()
                .putInt(QUESTION_TYPE, type)
                .putInt(QUESTION_DIFFICULTY, difficulty)
                .putInt(QUESTION_CATEGORY, category)
                .commit()
        }
    }

    override suspend fun getQuestionType(): Int {
        return withContext(Dispatchers.Default) {
            sharedPreferences.getInt(QUESTION_TYPE,0) ?: 0
        }
    }

    override suspend fun getQuestionDifficulty(): Int {
        return withContext(Dispatchers.Default) {
            sharedPreferences.getInt(QUESTION_DIFFICULTY,0) ?: 0
        }
    }

    override suspend fun getQuestionCategory(): Int {
        return withContext(Dispatchers.Default) {
            sharedPreferences.getInt(QUESTION_CATEGORY,0) ?: 0
        }
    }

    companion object {
        const val QUESTION_TYPE = "question_type"
        const val QUESTION_DIFFICULTY = "question_difficulty"
        const val QUESTION_CATEGORY = "question_category"
    }
}