package com.rodcollab.matriviaapp.data.repository

import com.rodcollab.matriviaapp.data.local.RankingDao
import com.rodcollab.matriviaapp.data.model.local.RankingLocal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RankingRepositoryImpl @Inject constructor(private val rankingDao: RankingDao) : RankingRepository {
    override suspend fun insert(itemRanking: RankingLocal) {
        withContext(Dispatchers.IO) {
            rankingDao.insert(itemRanking)
        }
    }

    override suspend fun getRanking(): List<RankingLocal> {
       return withContext(Dispatchers.IO) {
            rankingDao.getRanking()
        }
    }
}