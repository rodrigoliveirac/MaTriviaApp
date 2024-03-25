package com.rodcollab.matriviaapp.redux.thunk

import com.rodcollab.matriviaapp.data.model.RankingExternal
import com.rodcollab.matriviaapp.data.model.local.RankingLocal
import com.rodcollab.matriviaapp.data.repository.RankingRepository
import com.rodcollab.matriviaapp.di.DefaultDispatcher
import com.rodcollab.matriviaapp.game.GameState
import com.rodcollab.matriviaapp.redux.actions.PlayingGameActions
import com.rodcollab.matriviaapp.utils.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.reduxkotlin.thunk.Thunk
import kotlin.coroutines.CoroutineContext

class GetRankingThunkImpl(@DefaultDispatcher dispatcher: CoroutineContext, private val rankingRepository: RankingRepository) :
    GetRankingThunk {

    private val scope = CoroutineScope(dispatcher)
    override fun getRanking(): Thunk<GameState> = { dispatch, getState, _->
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
            dispatch(PlayingGameActions.EndOfTheGame(ranking))
        }
    }
}

