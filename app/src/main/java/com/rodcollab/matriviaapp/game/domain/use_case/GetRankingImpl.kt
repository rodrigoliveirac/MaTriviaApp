package com.rodcollab.matriviaapp.game.domain.use_case

import com.rodcollab.matriviaapp.data.model.RankingExternal
import com.rodcollab.matriviaapp.data.repository.RankingRepository
import com.rodcollab.matriviaapp.utils.DateUtils

class GetRankingImpl(private val rankingRepository: RankingRepository) : GetRanking {
    override suspend fun invoke(): List<RankingExternal> {
       return rankingRepository.getRanking().map { rankingLocal ->
            RankingExternal(
                id = rankingLocal.id,
                correctAnswers = rankingLocal.correctAnswers.toString(),
                createdAt = DateUtils.getDateFormatted(rankingLocal.createdAt)
            )
        }
    }
}

