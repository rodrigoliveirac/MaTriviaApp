package com.rodcollab.matriviaapp.game.domain.use_case

import com.rodcollab.matriviaapp.data.model.Category
import com.rodcollab.matriviaapp.data.model.QuestionDifficulty
import com.rodcollab.matriviaapp.data.model.QuestionType
import com.rodcollab.matriviaapp.data.repository.TriviaRepository
import com.rodcollab.matriviaapp.game.domain.GameCriteria
import com.rodcollab.matriviaapp.game.domain.UserGamePrefs
import com.rodcollab.matriviaapp.game.domain.preferences.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
class ShowPrefsAndCriteriaImpl(private val preferences: Preferences, private val triviaRepository: TriviaRepository) : ShowPrefsAndCriteria {

    private val categories = mutableListOf<Category>()
    private val difficulties = triviaRepository.getQuestionDifficulties()
    private val types = triviaRepository.getQuestionTypes()

    override suspend fun invoke(): Pair<UserGamePrefs, GameCriteria> {
        return withContext(Dispatchers.IO) {

            categories.ifEmpty {
                    async {
                        triviaRepository.getCategories().forEach { category ->
                            categories.add(category)
                        }
                        if(categories.find { it.id == 0 } == null) {
                            categories.add(Category(id = 0, name = GetCategoriesImpl.defaultValue))
                        }
                        categories
                    }.await()
            }
            
            Pair(
                first = UserGamePrefs(
                    type = getQuestionTypeFromIndex(preferences.getQuestionType()),
                    difficulty = getQuestionDifficultyFromIndex(preferences.getQuestionDifficulty()),
                    category = getQuestionCategoryFromId(preferences.getQuestionCategory())
                ), 
                second = GameCriteria(
                    types = types,
                    difficulties = difficulties,
                    categories = categories
                )
            )
        }
    }

    private fun getQuestionDifficultyFromIndex(index: Int) : QuestionDifficulty {
       return difficulties[index]
    }
    private fun getQuestionTypeFromIndex(index: Int) : QuestionType {
        return types[index]
    }
    private fun getQuestionCategoryFromId(id: Int) : Category {
         val category = categories.indexOfFirst { it.id == id }
         return categories[category]
    }
}