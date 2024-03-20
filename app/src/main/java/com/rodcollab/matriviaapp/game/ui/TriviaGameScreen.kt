package com.rodcollab.matriviaapp.game.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rodcollab.matriviaapp.R
import com.rodcollab.matriviaapp.game.intent.TimerActions
import com.rodcollab.matriviaapp.game.ui.components.PlayingScreen
import com.rodcollab.matriviaapp.game.ui.components.PrepareGameDialog
import com.rodcollab.matriviaapp.game.ui.components.SnackBar
import com.rodcollab.matriviaapp.game.ui.components.SnackBarVisualsWithError
import com.rodcollab.matriviaapp.game.ui.components.TopBarGame
import com.rodcollab.matriviaapp.game.ui.components.WidgetDialog
import com.rodcollab.matriviaapp.game.viewmodel.GameStatus
import com.rodcollab.matriviaapp.game.viewmodel.TriviaGameState
import com.rodcollab.matriviaapp.game.viewmodel.TriviaGameVm
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TriviaGameScreen(viewModel: TriviaGameVm) {

    val uiState by viewModel.uiState.collectAsState()
    val timeState by viewModel.timeState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var heightTopBar by remember { mutableStateOf<Dp>(0.dp) }

    Scaffold(
        topBar = {
            TopBarGame(onHeightValue = { heightTopBar = it })
        },
        snackbarHost = { SnackBar(
                heightTopBar = heightTopBar,
                modifier = Modifier.fillMaxSize(),
                snackbarHostState = snackbarHostState)
        }) {  paddingValues ->
            when(uiState.currentState) {
                GameStatus.PREP -> {
                    uiState.criteriaFields?.let { criteriaFields ->
                        PrepareGameDialog(criteriaFields) { viewModel.onActionMenuGame(it) }
                    }
                }
                GameStatus.STARTED -> {
                    PlayingScreen(paddingValues = paddingValues, timeState = timeState, uiState = uiState) { gamePlayingActions ->
                        viewModel.onGamePlayingAction(gamePlayingActions)
                    }
                }
                else -> {

                }
            }
            if(uiState.isLoading) {
                Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }

    }

    LaunchSnackBar(uiState, snackbarHostState)
    LaunchCounterTime(uiState, timeState) { viewModel.onTimeActions(it) }
}

@Composable
private fun LaunchCounterTime(
    uiState: TriviaGameState,
    timeState: Int,
    onTimeActions: suspend (TimerActions) -> Unit
) {
    LaunchedEffect(uiState.timeIsFinished) {
        while (timeState > 0) {
            onTimeActions(TimerActions.Update)
        }
        if (uiState.timeIsFinished) {
            onTimeActions(TimerActions.Over)
        }
    }
}

@Composable
private fun LaunchSnackBar(
    uiState: TriviaGameState,
    snackbarHostState: SnackbarHostState
) {
    uiState.isCorrectOrIncorrect?.let {
        val msg =
            if (it) stringResource(R.string.congratulations_you_got_it_right) else if (uiState.timeIsFinished) stringResource(
                R.string.your_time_is_up
            ) else stringResource(R.string.oops_you_got_it_wrong)
        LaunchedEffect(Unit) {
            val job = launch {
                snackbarHostState.showSnackbar(
                    SnackBarVisualsWithError(
                        msg,
                        isError = !it
                    )
                )
            }
            delay(1000L)
            job.cancel()
        }
    }
}

@Composable
@Preview
fun PrepareGameDialogPreview() {
    WidgetDialog(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxWidth()) {
            Text(modifier = Modifier.align(Alignment.Center), text = "Prepare the game", fontSize = 24.sp)
        }
        var width by remember { mutableStateOf<Dp?>(null) }
        val density = LocalDensity.current
        OutlinedTextField(
            modifier = Modifier.onGloballyPositioned {
                width = with(density) {
                    it.size.width.toDp()
                }
            },value = "Selecione a categoria" , onValueChange = { }, readOnly = true, trailingIcon = {
                IconButton(onClick = {  }) {
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                }
            },label = {
                Text(text = "Oficina de costura")
            })
        val scrollState = rememberScrollState()
//        width?.let {
//            DropdownMenu(scrollState = scrollState, modifier = Modifier
//                .align(Alignment.CenterHorizontally)
//                .heightIn(max = 120.dp)
//                .width(it), expanded = value.expanded, onDismissRequest = {  }) {
//                value.items.toList().map {
//                    DropdownMenuItem(text = {
//                        Text(text = it.second.name.toString())
//                    },
//                        onClick = {  })
//                }
//            }
//        }
        Spacer(modifier = Modifier.size(16.dp))
        CircularProgressIndicator(strokeWidth = 2.dp)
    }
}

@Composable
@Preview
fun GettingNewQuestionDialogPreview() {
    WidgetDialog(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxWidth()) {
            Text(modifier = Modifier.align(Alignment.Center), text = "Getting new question", fontSize = 24.sp)
        }
        Spacer(modifier = Modifier.size(16.dp))
        CircularProgressIndicator(strokeWidth = 2.dp)
    }
}