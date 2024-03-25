package com.rodcollab.matriviaapp.game.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rodcollab.matriviaapp.R
import com.rodcollab.matriviaapp.redux.actions.PlayingGameActions
import com.rodcollab.matriviaapp.game.ui.components.ConfirmWithdrawalDialog
import com.rodcollab.matriviaapp.game.ui.components.EndOfGameDialog
import com.rodcollab.matriviaapp.game.ui.components.PlayingScreen
import com.rodcollab.matriviaapp.game.ui.components.PrepareGameDialog
import com.rodcollab.matriviaapp.game.ui.components.SnackBar
import com.rodcollab.matriviaapp.game.ui.components.SnackBarVisualsWithError
import com.rodcollab.matriviaapp.game.ui.components.TopBarGame
import com.rodcollab.matriviaapp.game.ui.components.WidgetDialog
import com.rodcollab.matriviaapp.game.GameStatus
import com.rodcollab.matriviaapp.game.TriviaGameVm
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TriviaGameScreen(viewModel: TriviaGameVm) {

    val snackbarHostState = remember { SnackbarHostState() }
    var heightTopBar by remember { mutableStateOf<Dp>(0.dp) }

    var game by remember { mutableStateOf(viewModel.gameState.state) }

    viewModel.gameState.subscribe { game = viewModel.gameState.state }

    game.networkWarning?.let {
        WidgetDialog(Modifier.fillMaxSize()) {
            Icon(modifier = Modifier.size(64.dp),painter = painterResource(id = R.drawable.wifi_off), contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = "No network connection")
            Spacer(modifier = Modifier.size(8.dp))
            OutlinedButton(onClick = { viewModel.tryNetworkConnection() }) {
                Text(text = "Try Again")
                Spacer(modifier = Modifier.size(8.dp))
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
            }
        }
    } ?: run {
        Scaffold(
            topBar = {
                TopBarGame(onHeightValue = { heightTopBar = it }) { viewModel.onGamePlayingAction(it) }
            },
            snackbarHost = { SnackBar(
                heightTopBar = heightTopBar,
                modifier = Modifier.fillMaxSize(),
                snackbarHostState = snackbarHostState)
            }) {  paddingValues ->
            when(game.gameStatus) {
                GameStatus.SETUP -> {
                    PrepareGameDialog(game.gameCriteriaUiModel) { viewModel.onMenuGameAction(it) }
                }
                GameStatus.STARTED -> {
                    PlayingScreen(paddingValues = paddingValues, timeState = game.timeState, uiState = game) { gamePlayingActions ->
                        viewModel.onGamePlayingAction(gamePlayingActions)
                    }
                }
                else -> {
                    EndOfGameDialog(
                        uiState = game
                    ) { endGameAction ->
                        viewModel.onEndGameActions(endGameAction)
                    }
                }
            }
            if (game.confirmWithdrawal) {
                ConfirmWithdrawalDialog { viewModel.onGamePlayingAction(it) }
            }
        }
    }

    game.isCorrectOrIncorrect?.let { isCorrectAnswer ->
        LaunchSnackBar(isCorrectAnswer,game.timeIsFinished, snackbarHostState) { viewModel.gameState.dispatch(
            PlayingGameActions.ContinueGame(isCorrectAnswer)
        ) }
    }

}

@Composable
private fun LaunchSnackBar(
    isCorrectAnswer: Boolean,
    timeIsFinished: Boolean,
    snackbarHostState: SnackbarHostState,
    onContinueGame: () -> Unit,
) {
        var msg =
            if (isCorrectAnswer) stringResource(R.string.congratulations_you_got_it_right) else stringResource(
                R.string.oops_you_got_it_wrong
            )
       if (timeIsFinished) {
           msg = stringResource(R.string.your_time_is_up)
       }
        LaunchedEffect(isCorrectAnswer) {
            val job = launch {
                snackbarHostState.showSnackbar(
                    SnackBarVisualsWithError(
                        msg,
                        isError = !isCorrectAnswer
                    )
                )
            }
            delay(1000L)
            job.cancel()
            onContinueGame()
        }
}