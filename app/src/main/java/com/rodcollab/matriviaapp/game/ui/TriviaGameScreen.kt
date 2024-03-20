package com.rodcollab.matriviaapp.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.rodcollab.matriviaapp.R
import com.rodcollab.matriviaapp.game.intent.MenuGameActions
import com.rodcollab.matriviaapp.game.theme.outlineFieldsCriteriaDisabled
import com.rodcollab.matriviaapp.game.viewmodel.CategoryFieldModel
import com.rodcollab.matriviaapp.game.viewmodel.DifficultyFieldModel
import com.rodcollab.matriviaapp.game.viewmodel.DropDownMenu
import com.rodcollab.matriviaapp.game.viewmodel.GameCriteriaUiModel
import com.rodcollab.matriviaapp.game.viewmodel.GameStatus
import com.rodcollab.matriviaapp.game.viewmodel.MenuFields
import com.rodcollab.matriviaapp.game.viewmodel.TriviaGameVm
import com.rodcollab.matriviaapp.game.viewmodel.TypeFieldModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TriviaGameScreen(viewModel: TriviaGameVm) {

    val uiState by viewModel.uiState.collectAsState()
    val timeState by viewModel.timeState.collectAsState()

    LaunchedEffect(uiState.timeIsFinished) {
        while (timeState > 0) {
            viewModel.updateTime()
        }
        if(uiState.timeIsFinished) {
            viewModel.updateStatusGameAfterTimeIsOver()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    var heightTopBar by remember { mutableStateOf<Dp>(0.dp) }
    Scaffold(
        topBar = {
            val density = LocalDensity.current
            CenterAlignedTopAppBar(modifier = Modifier
                .shadow(elevation = 6.dp)
                .onGloballyPositioned {
                    heightTopBar = with(density) {
                        it.size.height.toDp()
                    }
                },title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(textAlign = TextAlign.Center, modifier = Modifier.weight(1f),text = stringResource(id = R.string.app_name))
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(painter = painterResource(id = R.drawable.baseline_exit_to_app_24), contentDescription = null)
                        }
                    }
                })
        },
        snackbarHost = {
            Box(modifier = Modifier.fillMaxSize()) {
                SnackbarHost(modifier = Modifier.align(Alignment.TopCenter),hostState = snackbarHostState) { data ->
                    val isError = (data.visuals as? SnackbarVisualsWithError)?.isError ?: false
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
        }) {  paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            when(uiState.currentState) {
                GameStatus.PREP -> {
                    uiState.criteriaFields?.let { criteriaFields ->
                        PrepareGameDialog(criteriaFields) { viewModel.onActionMenuGame(it) }
                    }
                }
                GameStatus.STARTED -> {

                    Text(modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp),text = "${timeState} s",color = if(timeState <= 4) Color(255, 152, 152) else Color.Black)

                    Column(modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)) {

                        Text(style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally), text = "Question ${uiState.numberQuestion}")
                        Spacer(modifier = Modifier.size(8.dp))
                        uiState.currentQuestion?.let {
                            Text(
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .align(Alignment.CenterHorizontally), text = it.question)
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        Column(Modifier.padding(8.dp)) {
                            uiState.optionsAnswers.map {
                                Box(modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        1.dp,
                                        Color.LightGray.copy(alpha = 0.5f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clip(RoundedCornerShape(8.dp))) {
                                    Row(modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.LightGray.copy(if (it.highlight) 0.5f else 0.0f))
                                        .padding(8.dp),verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(selected = it.selected, onClick = { viewModel.selectOption(it.id) })
                                        Spacer(modifier = Modifier.size(8.dp))
                                        Text(style = MaterialTheme.typography.bodyMedium,text = it.option)
                                    }
                                }
                                Spacer(modifier = Modifier.size(8.dp))
                            }
                        }

                        uiState.currentOptionIdSelected?.let {
                            Button(modifier = Modifier.fillMaxWidth(),onClick = { viewModel.confirmAnswer() }) {
                                Text(text = stringResource(id = R.string.confirm_answer_button))
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
            uiState.isCorrectOrIncorrect?.let {
                val msg = if(it) stringResource(R.string.congratulations_you_got_it_right) else if(uiState.timeIsFinished) stringResource(
                    R.string.your_time_is_up
                ) else stringResource(R.string.oops_you_got_it_wrong)
                LaunchedEffect(Unit) {
                    val job = launch {
                        snackbarHostState.showSnackbar(
                            SnackbarVisualsWithError(
                                msg,
                                isError = !it
                            )
                        )
                    }
                    delay(1000L)
                    job.cancel()
                }
            }
        }
    }
}

class SnackbarVisualsWithError(
    override val message: String,
    val isError: Boolean
) : SnackbarVisuals {
    override val actionLabel: String
        get() = if (isError) "Wrong" else "Right"
    override val withDismissAction: Boolean
        get() = false
    override val duration: SnackbarDuration
        get() = SnackbarDuration.Indefinite
}

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
                colors = outlineFieldsCriteriaDisabled(),
                enabled = false,
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

@Composable
@Preview
fun PrepareGameDialogPreview() {
    WidgetDialog(Modifier.fillMaxSize()) {
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
    WidgetDialog(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxWidth()) {
            Text(modifier = Modifier.align(Alignment.Center), text = "Getting new question", fontSize = 24.sp)
        }
        Spacer(modifier = Modifier.size(16.dp))
        CircularProgressIndicator(strokeWidth = 2.dp)
    }
}

@Composable
fun WidgetDialog(modifier: Modifier, content: @Composable ColumnScope.() -> Unit) {
    Dialog(onDismissRequest = {}, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(modifier = modifier
            .background(Color.White)
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