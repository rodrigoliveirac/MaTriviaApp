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
import com.rodcollab.matriviaapp.game.theme.outlineFieldsCriteriaDisabled
import com.rodcollab.matriviaapp.game.viewmodel.DropDownMenu
import com.rodcollab.matriviaapp.game.viewmodel.TypeFieldModel
import com.rodcollab.matriviaapp.redux.ExpandMenuAction
import com.rodcollab.matriviaapp.redux.FieldAction
import com.rodcollab.matriviaapp.redux.SelectCriteriaAction

@Composable
fun TypeField(
    modifier: Modifier,
    typeField: DropDownMenu<TypeFieldModel?>,
    onActionField: (FieldAction) -> Unit
) {
    var width by remember { mutableStateOf<Dp?>(null) }
    val density = LocalDensity.current
    Column {
        typeField.field?.let { field ->
            OutlinedTextField(
                colors = outlineFieldsCriteriaDisabled(),
                enabled = false,
                modifier = modifier.onGloballyPositioned {
                    width = with(density) {
                        it.size.width.toDp()
                    }
                }, value = stringResource(id = field.selected.type), onValueChange = { }, readOnly = true, trailingIcon = {
                    IconButton(onClick = { onActionField(ExpandMenuAction.TypeField) }) {
                        Icon(painter = if(typeField.expanded) painterResource(id = R.drawable.arrow_up) else painterResource(id = R.drawable.arrow_drop_down), contentDescription = null)
                    }
                }, label = {
                    Text(text = "Type")
                })
            width?.let {
                DropdownMenu(modifier = Modifier
                    .heightIn(max = 120.dp)
                    .width(it), expanded = typeField.expanded, onDismissRequest = { onActionField(
                    ExpandMenuAction.TypeField) }) {
                    typeField.field.options.map { type ->
                        DropdownMenuItem(text = {
                            Text(text = stringResource(id = type.type))
                        },
                            onClick = {
                                onActionField(SelectCriteriaAction.TypeField(type))
                            })
                    }
                }
            }
        }
    }
}