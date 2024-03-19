package com.rodcollab.matriviaapp.data.api

import com.rodcollab.matriviaapp.data.model.QuestionResults
import com.rodcollab.matriviaapp.data.model.TriviaCategories
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaApi {

    @GET("api_category.php")
    suspend fun getCategories() : Response<TriviaCategories>
    @GET("api.php?")
    suspend fun getQuestion(
        @Query("amount") amount: String = "2",
        @Query("difficulty") difficulty: String = "",
        @Query("category") category: String = "",
        @Query("type") type: String = ""
    ) : Response<QuestionResults>
}