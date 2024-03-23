package com.rodcollab.matriviaapp.game.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.sp
import com.rodcollab.matriviaapp.R
import com.rodcollab.matriviaapp.data.model.RankingExternal
import com.rodcollab.matriviaapp.game.intent.EndGameActions
import com.rodcollab.matriviaapp.game.viewmodel.TriviaGameState
import com.rodcollab.matriviaapp.redux.GameState

@Composable
fun EndOfGameDialog(uiState:GameState, onEndGameActions:(EndGameActions) -> Unit) {
        WidgetDialog(modifier = Modifier.clip(RoundedCornerShape(16.dp))) {
            Text(style = MaterialTheme.typography.headlineMedium,modifier = Modifier
                .align(Alignment.CenterHorizontally)
                ,text = stringResource(R.string.end_of_game))
            Spacer(modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier
                .height(1.dp)
                .background(Color.LightGray)
                .fillMaxWidth())
            Spacer(modifier = Modifier.size(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(R.string.correct_answers,uiState.correctAnswers.toString()),style = MaterialTheme.typography.titleLarge)
            }
            Spacer(modifier = Modifier.size(8.dp))
            Spacer(modifier = Modifier
                .height(1.dp)
                .background(Color.LightGray)
                .fillMaxWidth())
            Spacer(modifier = Modifier.size(8.dp))
            RankingItems(uiState.ranking)
            Spacer(modifier = Modifier.size(8.dp))
            Button(modifier = Modifier.fillMaxWidth(),onClick = { onEndGameActions(EndGameActions.PlayAgain) }) {
                Text(text = stringResource(R.string.play_again))
            }
            Spacer(modifier = Modifier.size(4.dp))
            OutlinedButton(modifier = Modifier.fillMaxWidth(),onClick = { onEndGameActions(EndGameActions.BackToGameSetup) }) {
                Text(text = stringResource(R.string.back_to_game_setup))
            }
        }
}

@Composable
private fun RankingItems(ranking: List<RankingExternal>) {
    Text(text = "Top 10 Ranking")
    Spacer(modifier = Modifier.size(1.dp))
    LazyColumn(modifier = Modifier.heightIn(max = 80.dp)) {
        items(ranking) {rankingItem ->
            Spacer(modifier = Modifier.size(7.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                RankingItem(Modifier.weight(1f), rankingItem.correctAnswers, rankingItem.createdAt)
            }
            Spacer(modifier = Modifier.size(8.dp))
            Spacer(modifier = Modifier
                .height(1.dp)
                .background(Color.LightGray)
                .fillMaxWidth())
        }
    }
}

@Composable
private fun RankingItem(modifier: Modifier, correctAnswers: String, date: String) {
    Text(fontSize = 14.sp, modifier = modifier, text = stringResource(id = R.string.correct_answers,correctAnswers).lowercase())
    Text(modifier = modifier,fontSize = 12.sp,text = date)
}