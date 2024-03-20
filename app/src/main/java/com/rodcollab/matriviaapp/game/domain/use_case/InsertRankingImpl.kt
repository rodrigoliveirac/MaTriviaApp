package com.rodcollab.matriviaapp.game.domain.use_case

import com.rodcollab.matriviaapp.data.model.local.RankingLocal
import com.rodcollab.matriviaapp.data.repository.RankingRepository

class InsertRankingImpl(private val rankingRepository: RankingRepository) : InsertRanking {
    override suspend fun invoke(correctAnswers: Int) {
        val rankingLocal = RankingLocal(
            correctAnswers = correctAnswers, createdAt = System.currentTimeMillis()
        )
        rankingRepository.insert(rankingLocal)
    }
}