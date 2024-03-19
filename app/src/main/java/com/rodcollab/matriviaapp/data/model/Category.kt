package com.rodcollab.matriviaapp.data.model

import com.google.gson.annotations.SerializedName

data class TriviaCategories(
    @SerializedName("trivia_categories")
    val categories: List<Category>
)

data class Category(
    val id: Int,
    val name: String
)