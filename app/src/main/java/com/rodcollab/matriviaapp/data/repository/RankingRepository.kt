package com.rodcollab.matriviaapp.data.repository

import com.rodcollab.matriviaapp.data.model.local.RankingLocal

interface RankingRepository {
    suspend fun insert(itemRanking: RankingLocal)
    suspend fun getRanking() : List<RankingLocal>
}