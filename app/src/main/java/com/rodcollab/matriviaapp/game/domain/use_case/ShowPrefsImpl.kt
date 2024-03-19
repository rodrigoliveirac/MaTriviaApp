package com.rodcollab.matriviaapp.game.domain.use_case

import com.rodcollab.matriviaapp.data.model.Category
import com.rodcollab.matriviaapp.data.model.QuestionDifficulty
import com.rodcollab.matriviaapp.data.model.QuestionType
import com.rodcollab.matriviaapp.data.model.difficulties
import com.rodcollab.matriviaapp.data.model.types
import com.rodcollab.matriviaapp.data.repository.TriviaRepository
import com.rodcollab.matriviaapp.game.domain.UserGamePrefs
import com.rodcollab.matriviaapp.game.domain.preferences.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ShowPrefsImpl(private val preferences: Preferences, private val triviaRepository: TriviaRepository) : ShowPrefs {
    override suspend fun invoke(): UserGamePrefs {
       return withContext(Dispatchers.IO) {
            UserGamePrefs(
                type = getQuestionTypeFromIndex(preferences.getQuestionType()),
                difficulty = getQuestionDifficultyFromIndex(preferences.getQuestionDifficulty()),
                category = getQuestionCategoryFromId(preferences.getQuestionCategory())
            )
        }
    }

    private fun getQuestionDifficultyFromIndex(index: Int) : QuestionDifficulty {
       return difficulties[index]
    }
    private fun getQuestionTypeFromIndex(index: Int) : QuestionType {
        return types[index]
    }
    private suspend fun getQuestionCategoryFromId(id: Int) : Category {
        return if(id == 0) {
            Category(id = id, name = defaultValue)
        } else {
            triviaRepository.getCategories().find { id == it.id }!!
        }
    }

    companion object {
        const val defaultValue = ""
    }
}