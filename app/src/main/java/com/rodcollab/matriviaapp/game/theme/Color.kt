package com.rodcollab.matriviaapp.game.theme

import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

@Composable
fun outlineFieldsCriteriaDisabled() : TextFieldColors {
    return OutlinedTextFieldDefaults.colors(disabledLabelColor = Color.Black,disabledBorderColor = Color.Black, disabledTextColor = Color.Black, disabledTrailingIconColor = Color.Black)
}

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)