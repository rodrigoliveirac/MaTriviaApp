package com.rodcollab.matriviaapp.game.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rodcollab.matriviaapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarGame(onHeightValue:(Dp)-> Unit, onTopBarAskToGiveUp: () -> Unit) {
    val density = LocalDensity.current
    CenterAlignedTopAppBar(modifier = Modifier
        .shadow(elevation = 6.dp)
        .onGloballyPositioned {
            with(density) {
                onHeightValue(it.size.height.toDp())
            }
        },title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(textAlign = TextAlign.Center, modifier = Modifier.weight(1f),text = stringResource(id = R.string.app_name))
            IconButton(onClick = { onTopBarAskToGiveUp() }) {
                Icon(painter = painterResource(id = R.drawable.baseline_exit_to_app_24), contentDescription = null)
            }
        }
    })
}