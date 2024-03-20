package com.rodcollab.matriviaapp.game.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rodcollab.matriviaapp.R

@Composable
fun SnackBar(heightTopBar: Dp, modifier:Modifier, snackbarHostState:SnackbarHostState) {
    Box(modifier = modifier) {
        SnackbarHost(modifier = Modifier.align(Alignment.TopCenter),hostState = snackbarHostState) { data ->
            val isError = (data.visuals as? SnackBarVisualsWithError)?.isError ?: false
            val textColor = if (isError) {
                Color(204, 0, 0)
            } else {
                Color(48, 103, 84)
            }
            Snackbar(
                containerColor = if(isError) Color(255, 152, 152) else Color(152, 255, 152),
                modifier = Modifier
                    .padding(top = heightTopBar)
                    .height(100.dp),
                shape = RectangleShape
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(modifier = Modifier.aspectRatio(0.5f),tint = textColor,painter = painterResource(id = if(isError) R.drawable.baseline_sentiment_very_dissatisfied_24 else R.drawable.baseline_sentiment_satisfied_alt_24), contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text(fontWeight = FontWeight.ExtraBold, modifier = Modifier.fillMaxWidth(),text = data.visuals.message.uppercase(), color = textColor)
                }
            }
        }
    }
}