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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rodcollab.matriviaapp.R
import com.rodcollab.matriviaapp.game.intent.MenuGameActions
import com.rodcollab.matriviaapp.game.theme.outlineFieldsCriteriaDisabled
import com.rodcollab.matriviaapp.game.viewmodel.CategoryFieldModel
import com.rodcollab.matriviaapp.game.viewmodel.DropDownMenu
import com.rodcollab.matriviaapp.game.viewmodel.MenuFields

@Composable
fun CategoryField(
    modifier: Modifier,
    categoryField: DropDownMenu<CategoryFieldModel?>,
    onActionField: (MenuGameActions) -> Unit
) {

    categoryField.field?.let {field ->
        var width by remember { mutableStateOf<Dp?>(null) }
        val density = LocalDensity.current
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedTextField(
                colors = outlineFieldsCriteriaDisabled(),
                enabled = false,
                modifier = modifier.onGloballyPositioned {
                    width = with(density) {
                        it.size.width.toDp()
                    }
                }, value = field.selected.name.ifEmpty { "Any Category" } , onValueChange = { }, readOnly = true, trailingIcon = {
                    IconButton(onClick = { onActionField(MenuGameActions.ExpandMenu(menuField = MenuFields.CATEGORY)) }) {
                        Icon(painter = if(categoryField.expanded) painterResource(id = R.drawable.arrow_up) else painterResource(id = R.drawable.arrow_drop_down), contentDescription = null)
                    }
                }, label = {
                    Text(text = "Category")
                })
            width?.let {
                DropdownMenu(modifier = Modifier
                    .heightIn(max = 120.dp)
                    .width(it), expanded = categoryField.expanded, onDismissRequest = { onActionField(
                    MenuGameActions.ExpandMenu(menuField = MenuFields.CATEGORY)) }) {
                    field.options.map { category ->
                        DropdownMenuItem(text = {
                            Text(text = category.name)
                        },
                            onClick = {
                                onActionField(MenuGameActions.SelectItem(menuField = MenuFields.CATEGORY,item = category))
                            })
                    }
                }
            }
        }
    }

}