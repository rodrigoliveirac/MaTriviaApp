package com.rodcollab.matriviaapp.game.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rodcollab.matriviaapp.R
import com.rodcollab.matriviaapp.game.intent.GamePlayingActions
import com.rodcollab.matriviaapp.game.viewmodel.TriviaGameState

@Composable
fun PlayingScreen(paddingValues: PaddingValues, timeState:Int, uiState: TriviaGameState, onActionGamePlaying:(GamePlayingActions) -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        Text(modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = 16.dp),text = "${timeState} s",color = if(timeState <= 4) Color(255, 152, 152) else Color.Black)

        Column(modifier = Modifier
            .align(Alignment.Center)
            .padding(16.dp)) {

            Text(style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally), text = "Question ${uiState.numberQuestion}")
            Spacer(modifier = Modifier.size(8.dp))
            uiState.currentQuestion?.let {
                Text(
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally), text = it.question)
            }
            Spacer(modifier = Modifier.size(8.dp))
            Column(Modifier.padding(8.dp)) {
                uiState.optionsAnswers.map {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            Color.LightGray.copy(alpha = 0.5f),
                            RoundedCornerShape(8.dp)
                        )
                        .clip(RoundedCornerShape(8.dp))) {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray.copy(if (it.highlight) 0.5f else 0.0f))
                            .padding(8.dp),verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = it.selected, onClick = { onActionGamePlaying(GamePlayingActions.SelectOption(optionId = it.id)) })
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(style = MaterialTheme.typography.bodyMedium,text = it.option)
                        }
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                }
            }

            uiState.currentOptionIdSelected?.let {
                Button(modifier = Modifier.fillMaxWidth(),onClick = { onActionGamePlaying(GamePlayingActions.ConfirmAnswer) }) {
                    Text(text = stringResource(id = R.string.confirm_answer_button))
                }
            }
        }
    }
}
