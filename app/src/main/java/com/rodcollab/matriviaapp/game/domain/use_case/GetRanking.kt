package com.rodcollab.matriviaapp.game.domain.use_case

import com.rodcollab.matriviaapp.data.model.RankingExternal

interface GetRanking {
    suspend operator fun invoke() : List<RankingExternal>
}