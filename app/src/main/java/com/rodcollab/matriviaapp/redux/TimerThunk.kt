package com.rodcollab.matriviaapp.redux

import com.rodcollab.matriviaapp.di.DefaultDispatcher
import com.rodcollab.matriviaapp.game.intent.TimerActions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.reduxkotlin.thunk.Thunk
import kotlin.coroutines.CoroutineContext

class TimerThunkImpl(@DefaultDispatcher dispatcher: CoroutineContext) : TimerThunk, CoroutineScope {
    override val coroutineContext: CoroutineContext = dispatcher + Job()
    private var countDownTimerJob: Job? = null
    override fun getTimerThunk(): Thunk<GameState> = { dispatch,getState,_ ->
        dispatch(PlayingGameActions.GetNewQuestion)
        countDownTimerJob = CoroutineScope(coroutineContext).launch {
            var value = 10
            while (value > 0) {
                delay(1000)
                dispatch(TimerActions.Update)
                value--
            }
            if(value == 0) {
                dispatch(TimerActions.Over)
                countDownTimerJob?.cancel()
            }
        }
        countDownTimerJob as Job
    }

    override fun stopTimerJob() {
        countDownTimerJob?.cancel()
    }

}
interface TimerThunk {
    fun getTimerThunk(): Thunk<GameState>
    fun stopTimerJob()
}