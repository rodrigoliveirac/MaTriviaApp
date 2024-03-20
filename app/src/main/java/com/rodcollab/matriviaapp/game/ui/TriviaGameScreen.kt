package com.rodcollab.matriviaapp.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.rodcollab.matriviaapp.R
import com.rodcollab.matriviaapp.game.intent.MenuGameActions
import com.rodcollab.matriviaapp.game.viewmodel.CategoryFieldModel
import com.rodcollab.matriviaapp.game.viewmodel.DifficultyFieldModel
import com.rodcollab.matriviaapp.game.viewmodel.DropDownMenu
import com.rodcollab.matriviaapp.game.viewmodel.GameCriteriaUiModel
import com.rodcollab.matriviaapp.game.viewmodel.GameStatus
import com.rodcollab.matriviaapp.game.viewmodel.MenuFields
import com.rodcollab.matriviaapp.game.viewmodel.TriviaGameVm
import com.rodcollab.matriviaapp.game.viewmodel.TypeFieldModel

@Composable
fun TriviaGameScreen(viewModel: TriviaGameVm) {
    val uiState by viewModel.uiState.collectAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        when(uiState.currentState) {
            GameStatus.PREP -> {
                uiState.criteriaFields?.let { criteriaFields ->
                    PrepareGameDialog(criteriaFields) { viewModel.onActionMenuGame(it) }
                }
            }
            GameStatus.STARTED -> {
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)) {

                        Text(modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally), text = "Question")

                        uiState.currentQuestion?.let {
                            Text(modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally), text = it.question)
                        }
                        Column {
                            uiState.optionsAnswers.map {
                                Box(modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(CircleShape)
                                    .border(1.dp, Color.LightGray.copy(alpha = 0.5f))) {
                                    Row(modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.LightGray.copy(if (it.highlight) 0.5f else 0.0f))
                                        .padding(8.dp),verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(selected = it.selected, onClick = { viewModel.selectOption(it.id) })
                                        Spacer(modifier = Modifier.size(8.dp))
                                        Text(it.option)
                                    }
                                }
                            }
                        }

                        uiState.currentOptionIdSelected?.let {
                            Button(onClick = { viewModel.confirmAnswer() }) {
                                Text(text = "Confirmar")
                            }
                        }
                    }
            }
            else -> {

            }
        }
        if(uiState.isLoading) {
            Box(Modifier.align(Alignment.Center)) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun PrepareGameDialog(criteriaFields: GameCriteriaUiModel, onActionMenuGame: (MenuGameActions) -> Unit) {
    WidgetDialog {
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
        Button(onClick = { onActionMenuGame(MenuGameActions.StartGame) }) {
            Text(text="Começar")
        }
    }
}

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

@Composable
fun TypeField(
    modifier: Modifier,
    typeField: DropDownMenu<TypeFieldModel?>,
    onActionField: (MenuGameActions) -> Unit
) {
    var width by remember { mutableStateOf<Dp?>(null) }
    val density = LocalDensity.current
    Column {
        typeField.field?.let { field ->
            OutlinedTextField(
                modifier = modifier.onGloballyPositioned {
                    width = with(density) {
                        it.size.width.toDp()
                    }
                }, value = stringResource(id = field.selected.type), onValueChange = { }, readOnly = true, trailingIcon = {
                    IconButton(onClick = { onActionField(MenuGameActions.ExpandMenu(menuField = MenuFields.TYPE)) }) {
                        Icon(painter = if(typeField.expanded) painterResource(id = R.drawable.arrow_up) else painterResource(id = R.drawable.arrow_drop_down), contentDescription = null)
                    }
                }, label = {
                    Text(text = "Type")
                })
            width?.let {
                Spacer(modifier = Modifier.size(8.dp))
                DropdownMenu(modifier = Modifier
                    .heightIn(max = 120.dp)
                    .width(it), expanded = typeField.expanded, onDismissRequest = { onActionField(
                    MenuGameActions.ExpandMenu(menuField = MenuFields.TYPE)) }) {
                    typeField.field.options.map { type ->
                        DropdownMenuItem(text = {
                            Text(text = stringResource(id = type.type))
                        },
                            onClick = {
                                onActionField(MenuGameActions.SelectItem(menuField = MenuFields.TYPE, item = type))
                            })
                    }
                }
            }
        }
    }
}


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

@Composable
@Preview
fun PrepareGameDialogPreview() {
    WidgetDialog {
        Box(Modifier.fillMaxWidth()) {
            Text(modifier = Modifier.align(Alignment.Center), text = "Prepare the game", fontSize = 24.sp)
        }
        var width by remember { mutableStateOf<Dp?>(null) }
        val density = LocalDensity.current
        OutlinedTextField(
            modifier = Modifier.onGloballyPositioned {
                width = with(density) {
                    it.size.width.toDp()
                }
            },value = "Selecione a categoria" , onValueChange = { }, readOnly = true, trailingIcon = {
                IconButton(onClick = {  }) {
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                }
            },label = {
                Text(text = "Oficina de costura")
            })
        val scrollState = rememberScrollState()
//        width?.let {
//            DropdownMenu(scrollState = scrollState, modifier = Modifier
//                .align(Alignment.CenterHorizontally)
//                .heightIn(max = 120.dp)
//                .width(it), expanded = value.expanded, onDismissRequest = {  }) {
//                value.items.toList().map {
//                    DropdownMenuItem(text = {
//                        Text(text = it.second.name.toString())
//                    },
//                        onClick = {  })
//                }
//            }
//        }
        Spacer(modifier = Modifier.size(16.dp))
        CircularProgressIndicator(strokeWidth = 2.dp)
    }
}

@Composable
@Preview
fun GettingNewQuestionDialogPreview() {
    WidgetDialog {
        Box(Modifier.fillMaxWidth()) {
            Text(modifier = Modifier.align(Alignment.Center), text = "Getting new question", fontSize = 24.sp)
        }
        Spacer(modifier = Modifier.size(16.dp))
        CircularProgressIndicator(strokeWidth = 2.dp)
    }
}

@Composable
fun WidgetDialog(content: @Composable ColumnScope.() -> Unit) {
    Dialog(onDismissRequest = {}, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.White, RoundedCornerShape(24.dp))
            .width(300.dp)) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                content()
            }
        }
    }
}