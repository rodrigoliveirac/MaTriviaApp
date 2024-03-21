package com.rodcollab.matriviaapp.game.domain.use_case

import com.rodcollab.matriviaapp.data.model.Category
import com.rodcollab.matriviaapp.data.model.QuestionDifficulty
import com.rodcollab.matriviaapp.data.model.QuestionType
import com.rodcollab.matriviaapp.data.repository.TriviaRepository
import com.rodcollab.matriviaapp.game.domain.preferences.Preferences
import com.rodcollab.matriviaapp.game.viewmodel.CategoryFieldModel
import com.rodcollab.matriviaapp.game.viewmodel.DifficultyFieldModel
import com.rodcollab.matriviaapp.game.viewmodel.TypeFieldModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
class ShowPrefsAndCriteriaImpl(private val preferences: Preferences, private val triviaRepository: TriviaRepository) :
    ShowPrefsAndCriteria {

    private val categories = mutableListOf<Category>()
    private val difficulties = triviaRepository.getQuestionDifficulties()
    private val types = triviaRepository.getQuestionTypes()

    override suspend fun invoke(): Triple<TypeFieldModel,DifficultyFieldModel, CategoryFieldModel> {
        return withContext(Dispatchers.IO) {

            categories.ifEmpty {
                    async {
                        triviaRepository.getCategories().forEach { category ->
                            categories.add(category)
                        }
                        if(categories.find { it.id == 0 } == null) {
                            categories.add(Category(id = 0, name = defaultValue))
                        }
                        categories
                    }.await()
            }
            val typeField = TypeFieldModel(selected = getQuestionTypeFromIndex(preferences.getQuestionType()),options = types)
            val difficulty = DifficultyFieldModel(selected = getQuestionDifficultyFromIndex(preferences.getQuestionDifficulty()),options = difficulties)
            val categories = CategoryFieldModel(selected = getQuestionCategoryFromId(preferences.getQuestionCategory()), options = categories)

            Triple(
                first = typeField,
                second = difficulty,
                third = categories
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

    companion object {
        private const val defaultValue = "Any Category"
    }
}