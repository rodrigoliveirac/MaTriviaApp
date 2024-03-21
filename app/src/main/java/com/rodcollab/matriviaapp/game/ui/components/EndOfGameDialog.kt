package com.rodcollab.matriviaapp.game.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rodcollab.matriviaapp.R
import com.rodcollab.matriviaapp.game.intent.EndGameActions
import com.rodcollab.matriviaapp.game.viewmodel.TriviaGameState

@Composable
fun EndOfGameDialog(uiState:TriviaGameState, onEndGameActions:(EndGameActions) -> Unit) {
        WidgetDialog(modifier = Modifier.clip(RoundedCornerShape(16.dp))) {
            Text(style = MaterialTheme.typography.headlineMedium,modifier = Modifier
                .align(Alignment.CenterHorizontally)
                ,text = stringResource(R.string.end_of_game))
            Spacer(modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.height(1.dp).background(Color.LightGray).fillMaxWidth())
            Spacer(modifier = Modifier.size(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(R.string.correct_answers),style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = uiState.correctAnswers.toString(),style = MaterialTheme.typography.headlineSmall)
            }
            Spacer(modifier = Modifier.size(16.dp))
            Button(modifier = Modifier.fillMaxWidth(),onClick = { onEndGameActions(EndGameActions.PlayAgain) }) {
                Text(text = stringResource(R.string.play_again))
            }
            Spacer(modifier = Modifier.size(8.dp))
            OutlinedButton(modifier = Modifier.fillMaxWidth(),onClick = { onEndGameActions(EndGameActions.BackToGameSetup) }) {
                Text(text = stringResource(R.string.back_to_game_setup))
            }
        }
}