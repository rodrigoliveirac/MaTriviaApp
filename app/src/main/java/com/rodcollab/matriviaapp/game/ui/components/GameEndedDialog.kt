package com.rodcollab.matriviaapp.game.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rodcollab.matriviaapp.R
import com.rodcollab.matriviaapp.game.intent.EndGameActions
import com.rodcollab.matriviaapp.game.viewmodel.TriviaGameState

@Composable
fun GameEndedDialog(uiState:TriviaGameState, onEndGameActions:(EndGameActions) -> Unit) {
        WidgetDialog(modifier = Modifier.clip(RoundedCornerShape(16.dp))) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Correct Answers:",style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = uiState.correctAnswers.toString(),style = MaterialTheme.typography.headlineSmall)
            }
            Spacer(modifier = Modifier.size(8.dp))
            Button(modifier = Modifier.fillMaxWidth(),onClick = { onEndGameActions(EndGameActions.PlayAgain) }) {
                Text(text = stringResource(R.string.play_again))
            }
            Spacer(modifier = Modifier.size(8.dp))
            OutlinedButton(modifier = Modifier.fillMaxWidth(),onClick = { onEndGameActions(EndGameActions.BackToGameSetup) }) {
                Text(text = stringResource(R.string.back_to_game_setup))
            }
        }
}