package com.rodcollab.matriviaapp.game.ui.components

import androidx.compose.foundation.background
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
import com.rodcollab.matriviaapp.game.intent.GiveUpGameActions

@Composable
fun ConfirmWithdrawalDialog(onGiveUpGameActions: (GiveUpGameActions)-> Unit) {
    WidgetDialog(modifier = Modifier.clip(RoundedCornerShape(16.dp))) {
        Text(style = MaterialTheme.typography.titleLarge,modifier = Modifier
            .align(Alignment.CenterHorizontally)
            ,text = stringResource(R.string.are_you_sure_that_you_want_to_give_up)
        )
        Spacer(modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier
            .height(1.dp)
            .background(Color.LightGray)
            .fillMaxWidth())
        Spacer(modifier = Modifier.size(24.dp))
        Button(modifier = Modifier.fillMaxWidth(),onClick = { onGiveUpGameActions(GiveUpGameActions.Confirm) }) {
            Text(text = stringResource(R.string.yes))
        }
        Spacer(modifier = Modifier.size(8.dp))
        OutlinedButton(modifier = Modifier.fillMaxWidth(),onClick = { onGiveUpGameActions(
            GiveUpGameActions.GoBack) }) {
            Text(text = stringResource(R.string.no))
        }
    }
}