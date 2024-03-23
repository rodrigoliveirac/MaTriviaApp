package com.rodcollab.matriviaapp.game.domain.use_case

import com.rodcollab.matriviaapp.data.model.RankingExternal
import com.rodcollab.matriviaapp.data.model.local.RankingLocal
import com.rodcollab.matriviaapp.data.repository.RankingRepository
import com.rodcollab.matriviaapp.di.DefaultDispatcher
import com.rodcollab.matriviaapp.redux.Actions
import com.rodcollab.matriviaapp.redux.GameState
import com.rodcollab.matriviaapp.utils.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.reduxkotlin.thunk.Thunk
import kotlin.coroutines.CoroutineContext

class GetRankingImpl(@DefaultDispatcher dispatcher: CoroutineContext,private val rankingRepository: RankingRepository) : GetRanking {

    private val scope = CoroutineScope(dispatcher)
    override fun getRanking(): Thunk<GameState> = { dispatch,getState,_->
        scope.launch {
            val ranking = rankingRepository.getRanking().map { rankingLocal->
                RankingExternal(
                    id = rankingLocal.id,
                    correctAnswers = rankingLocal.correctAnswers.toString(),
                    createdAt = DateUtils.getDateFormatted(rankingLocal.createdAt)
                )
            }
            val rankingLocal = RankingLocal(
                correctAnswers = getState().correctAnswers, createdAt = System.currentTimeMillis()
            )
            rankingRepository.insert(rankingLocal)
            dispatch(Actions.EndOfTheGame(ranking))
        }
    }
}

