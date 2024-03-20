package com.rodcollab.matriviaapp.game.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rodcollab.matriviaapp.R
import com.rodcollab.matriviaapp.game.intent.MenuGameActions
import com.rodcollab.matriviaapp.game.viewmodel.GameCriteriaUiModel

@Composable
fun PrepareGameDialog(criteriaFields: GameCriteriaUiModel, onActionMenuGame: (MenuGameActions) -> Unit) {
    WidgetDialog(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxWidth()) {
            Text(modifier = Modifier.align(Alignment.Center), text = "Prepare the game", fontSize = 24.sp)
        }
        Spacer(modifier = Modifier.size(8.dp))
        CategoryField(
            Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally), criteriaFields.categoryField
        ) { onActionMenuGame(it) }
        Spacer(modifier = Modifier.size(8.dp))
        TypeField(
            Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally), criteriaFields.typeField
        ) {
            onActionMenuGame(it)
        }
        Spacer(modifier = Modifier.size(8.dp))
        DifficultyField(
            Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally), criteriaFields.difficultyField
        ) {
            onActionMenuGame(it)
        }
        Spacer(modifier = Modifier.size(8.dp))
        Button(modifier = Modifier.fillMaxWidth(),onClick = { onActionMenuGame(MenuGameActions.StartGame) }) {
            Text(text= stringResource(R.string.lets_play))
        }
    }
}