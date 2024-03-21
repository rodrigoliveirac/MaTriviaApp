package com.rodcollab.matriviaapp.data.model

import com.google.gson.annotations.SerializedName

data class QuestionResults(
    @SerializedName("response_code") val responseCode: Int,
    val results: List<TriviaQuestion>
)

data class TriviaQuestion(
    val type: String,
    val difficulty: String,
    val category: String,
    val question: String,
    @SerializedName("correct_answer") val correctAnswer: String,
    @SerializedName("incorrect_answers") val incorrectAnswer: List<String>
)