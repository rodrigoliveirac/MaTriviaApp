package com.rodcollab.matriviaapp.game.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rodcollab.matriviaapp.R
import com.rodcollab.matriviaapp.game.intent.MenuGameActions
import com.rodcollab.matriviaapp.game.theme.outlineFieldsCriteriaDisabled
import com.rodcollab.matriviaapp.game.viewmodel.DifficultyFieldModel
import com.rodcollab.matriviaapp.game.viewmodel.DropDownMenu
import com.rodcollab.matriviaapp.game.viewmodel.MenuFields

@Composable
fun DifficultyField(
    modifier: Modifier,
    difficultyField: DropDownMenu<DifficultyFieldModel?>,
    onActionField: (MenuGameActions) -> Unit
) {
    var width by remember { mutableStateOf<Dp?>(null) }
    val density = LocalDensity.current
    difficultyField.field?.let { field ->
        Column {
            OutlinedTextField(
                colors = outlineFieldsCriteriaDisabled(),
                enabled = false,
                modifier = modifier.onGloballyPositioned {
                    width = with(density) {
                        it.size.width.toDp()
                    }
                }, value = stringResource(id = field.selected.difficulty), onValueChange = { }, readOnly = true, trailingIcon = {
                    IconButton(onClick = { onActionField(MenuGameActions.ExpandMenu(menuField = MenuFields.DIFFICULTY)) }) {
                        Icon(painter = if(difficultyField.expanded) painterResource(id = R.drawable.arrow_up) else painterResource(id = R.drawable.arrow_drop_down), contentDescription = null)
                    }
                }, label = {
                    Text(text = "Difficulty")
                })
            width?.let {
                DropdownMenu(modifier = Modifier
                    .heightIn(max = 120.dp)
                    .width(it), expanded = difficultyField.expanded, onDismissRequest = { onActionField(
                    MenuGameActions.ExpandMenu(menuField = MenuFields.DIFFICULTY))  }) {
                    difficultyField.field.options.map { item ->
                        DropdownMenuItem(text = {
                            Text(text = stringResource(id = item.difficulty))
                        },
                            onClick = {
                                onActionField(MenuGameActions.SelectItem(menuField = MenuFields.DIFFICULTY,item = item))
                            })
                    }
                }
            }
        }
    }
}