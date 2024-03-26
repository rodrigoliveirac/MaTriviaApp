# MaTriviaApp
 - É um app desenvolvido utilizando Jetpack Compose para composição da UI.
 - As preferências do jogo são salvas com SharedPreferences.
 - A arquitetura foi baseada em MVI. E você pode checar a branch `redux-arch` para conferir a arquitetura utilizando REDUX.
- Na branch `Main`você vai encontrar o projeto sem REDUX.

## Sumário
* [Funcionalidades](#funcionalidades)
* [Estrutura responsável pelo Estado do jogo](#gamestate)
* [Reducer](#reducer)
* [Middleware](#middleware)
* [Entendendo Redux no MaTriviaApp](#redux)
* [Funções Assíncronas - Thunks](#thunks)
* [O papel do ViewModel](#triviagamevm)


# GameState

```
data class GameState(
    val questions: List<Question> = listOf(),
    val gameStatus: GameStatus = GameStatus.SETUP,
    val gameCriteriaUiModel: GameCriteriaUiModel = GameCriteriaUiModel(),
    val correctAnswers: Int = 0,
    val isCorrectOrIncorrect: Boolean? = null,
    val currentQuestion: Question? = null,
    val optionsAnswers: List<AnswerOptionUiModel> = listOf(),
    val timeIsFinished: Boolean = false,
    val confirmWithdrawal: Boolean = false,
    val disableSelection: Boolean = false,
    val timeState: Int? = null,
    val ranking: List<RankingExternal> = listOf(),
    val networkIsActive: Boolean? = null,
    val networkWarning: Boolean? = null
) {
    val numberQuestion = correctAnswers + 1
}
```

# Reducer

Função pura responsável por lidar com a gerenciamento do `GameState` 

Ações que mudam o `networkWarning` e `networkWarning` -  Exemplo

```
val reducer: Reducer<GameState> = { state, action ->
    when (action) {
    
        //======================
        // NETWORK GAME ACTIONS
        //======================
        is NetworkActions.ChangeNetworkState -> {
            state.copy(networkIsActive = action.network)
        }
        is NetworkActions.NetworkWarning -> {
            state.copy(networkWarning = true)
        }
        else -> {
            state.copy()
        }
    }
}
```

# MiddleWare

Responsável para disparar as funções assíncronas

Enquanto `gameStatus` == GameStatus.STARTED - Exemplos

```
fun uiMiddleware(
    timerThunk: TimerThunk,
    rankingThunks: GetRankingThunk,
    questionThunks: GetQuestionThunk,
    categoryThunks: PrefsAndCriteriaThunk
) = middleware<GameState> { store, next, action ->
    next(action)
    val dispatch = store.dispatch
    when (action) {
        //======================
        // PLAYING GAME ACTIONS
        //======================
        is PlayingGameActions.CheckAnswer -> {
            store.dispatch(PlayingGameActions.DisableSelection(action.answerId))
            when (action.answerId == CORRECT_ANSWER_ID) {
                true -> {
                    store.dispatch(PlayingGameActions.HandleCorrectAnswer)
                    store.dispatch(timerThunk.stopTimerJob())
                }

                else -> {
                    store.dispatch(PlayingGameActions.HandleIncorrectAnswer)
                    store.dispatch(timerThunk.stopTimerJob())
                }
            }
        }
        is PlayingGameActions.ContinueGame -> {
            when (action.isCorrectOrIncorrect) {
                true -> {
                    dispatch(timerThunk.getTimerThunk())
                }

                else -> {
                    dispatch(rankingThunks.getRanking())
                }
            }
        }
        is PlayingGameActions.GetNewQuestion -> {
            dispatch(questionThunks.getQuestionThunk())
        }
        is PlayingGameActions.GiveUpGameConfirm -> {
            dispatch(timerThunk.stopTimerJob())
            dispatch(rankingThunks.getRanking())
        }
        (...)
    }
}
```

 
# Funcionalidades
- [x] O usuário pode selecionar a dificuldade do jogo: Fácil, Média ou Difícil.
- [x] O usuário pode selecionar a categoria de perguntas que o mesmo deseja responder.
- [x] O usuário pode selecionar o tipo de pergunta que o mesmo pode responder: se **múltipla escolha**, **verdadeiro e falso** ou **ambos os tipos**.
- [x] O usuário pode responder somente 1 pergunta por vez.
- [x] As perguntas são escolhidas de forma aleatória, de acordo com os critérios pré-selecionados anteriormente.
- [x] Em casos de perguntas de múltipla escolha, as opções são apresentadas e o usuário pode dentre as opções.
- [x] Em casos de perguntas de verdadeiro ou falso, as opções são apresentadas como (Verdadeiro ou falso) e o usuário pode escolher dentre as opções.
- [x] Se o usuário errar a pergunta, uma mensagem é apresentada sobre o erro, a resposta certa é destacada e o jogo é encerrado.
- [x] Se o usuário acertar a pergunta, uma mensagem de sucesso é apresentada, a resposta é destacada e uma nova pergunta é apresentada.
- [x] O usuário pode responder quantas perguntas quiser até errar. O quantitativo de perguntas respondidas é mostrado.
- [x] O usuário tem apenas 10 segundos para responder cada pergunta.
- [x] O usuário pode desistir do jogo. O usuário pode confirmar se deseja desistir do jogo.
- [x] No final, o quantitativo de acertos do usuário é mostrado.
- [x] Um ranking com os 10 últimos jogos é apresentado (do maior para o menor).
- [x] Verificação de conexão com a Internet.

# Redux
<img width="204" alt="Screenshot 2024-03-25 at 19 53 54" src="https://github.com/rodrigoliveirac/MaTriviaApp/assets/72306040/c275ff32-f877-4d7d-a83b-921c30e0852a">

## Actions

**EndGameActions.kt** 

Responsável por todas as interações quando a variável `gameStatus` == GameStatus.END

```
sealed interface EndGameActions {

    data object PlayAgain : EndGameActions
    data object BackToGameSetup : EndGameActions
    
}
```
===================
**MenuGameActions.kt** 

Responsável por todas as interações quando a variável `gameStatus` == GameStatus.SETUP

```
sealed interface MenuGameActions {

    data object ExpandMenuCategoryField : MenuGameActions
    data object ExpandMenuTypeField : MenuGameActions
    data object ExpandMenuDifficultyField : MenuGameActions
    data class OnSelectCategoryField(val questionCategory: Category) :
        MenuGameActions
    data class OnSelectTypeField(val questionType: QuestionType) :
        MenuGameActions
    data class OnSelectDifficultyField(val questionDifficulty: QuestionDifficulty) :
        MenuGameActions
    data object PrepareGame : MenuGameActions
    data object StartGame : MenuGameActions
    data class UpdateCriteriaFieldsState(val gameCriteria: GameCriteriaUiModel) :
        MenuGameActions
    data object FetchCriteriaFields :
        MenuGameActions
}
```
===================
**NetworkActions.kt** 

Responsável por todas as interações quando a variável `networkWarning` != null

```
sealed interface NetworkActions {

    data object NetworkWarning : NetworkActions
    data object TryAgain : NetworkActions
    data class ChangeNetworkState(val network: Boolean?) : NetworkActions

}
```
===================
**PlayingGameActions.kt** 

Responsável por todas as interações quando a variável `gameStatus` == GameStatus.STARTED

```
sealed interface PlayingGameActions {

    data class UpdateQuestion(val triple: Triple<List<Question>, Question, List<AnswerOptionUiModel>>) :
        PlayingGameActions
    data class UpdateStatus(val gameStatus: GameStatus) : PlayingGameActions
    data object GetNewQuestion
    data class CheckAnswer(val answerId: Int) : PlayingGameActions
    data object HandleIncorrectAnswer : PlayingGameActions
    data object HandleCorrectAnswer : PlayingGameActions
    data class ContinueGame(val isCorrectOrIncorrect: Boolean) : PlayingGameActions
    data class EndOfTheGame(val ranking: List<RankingExternal>) : PlayingGameActions
    data object OnTopBarGiveUp : PlayingGameActions
    data class DisableSelection(val optionId: Int) : PlayingGameActions
    data object GiveUpGameConfirm : PlayingGameActions
    data object GiveUpGameGoBack : PlayingGameActions
}
```
===================
**TimerActions.kt** 

Responsável pelas interações que lidam com o estado do tempo

```
sealed interface TimerActions {

    data object Update : TimerActions
    data object Over : TimerActions
    data object TimerThunkDispatcher : TimerActions

}
```

# Thunks

**GetQuestionThunkImpl.kt** 

Thunk responsável pela função assíncrona que busca novas questões.

```
class GetQuestionThunkImpl(
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
    private val triviaRepository: TriviaRepository
) : GetQuestionThunk {
    private val scope = CoroutineScope(dispatcher + Job())

    override fun getQuestionThunk(): Thunk<GameState> = { dispatch, getState, _ ->
        scope.launch {
            val gameState = getState()
            delay(500L)
            if(gameState.questions.isEmpty()) {

                gameState.networkIsActive?.let {
                    var typePrefs = gameState.gameCriteriaUiModel.typeField.field?.selected?.id.toString()
                    var difficultyPrefs = gameState.gameCriteriaUiModel.difficultyField.field?.selected?.id.toString()
                    var categoryPrefs = gameState.gameCriteriaUiModel.categoryField.field?.selected?.id.toString()

                    typePrefs = if (typePrefs == ANY) {
                        DEFAULT
                    } else {
                        when (typePrefs) {
                            ID_MULTIPLE_TYPE -> MULTIPLE_TYPE
                            else -> {
                                BOOLEAN_TYPE
                            }
                        }
                    }
                    difficultyPrefs = if (difficultyPrefs == ANY) {
                        DEFAULT
                    } else {
                        when (difficultyPrefs) {
                            ID_EASY_DIFFICULT -> EASY_DIFFICULT
                            ID_MEDIUM_DIFFICULT -> MEDIUM_DIFFICULT
                            else -> {
                                HARD_DIFFICULT
                            }
                        }
                    }
                    if (categoryPrefs == ANY) {
                        categoryPrefs = DEFAULT
                    }
                    val newQuestions = async {
                        triviaRepository.getQuestions(
                            difficulty = difficultyPrefs,
                            type = typePrefs,
                            category = categoryPrefs
                        )
                            .map { triviaQuestion ->

                                val randomOptions = answerOptions(triviaQuestion)

                                val fromApi = triviaQuestion.question
                                val textFromHtmlFromApi = HtmlCompat.fromHtml(fromApi, HtmlCompat.FROM_HTML_MODE_LEGACY)

                                Question(
                                    type = triviaQuestion.type,
                                    difficulty = triviaQuestion.difficulty,
                                    category = triviaQuestion.category,
                                    question = textFromHtmlFromApi.toString(),
                                    correctAnswer = triviaQuestion.correctAnswer,
                                    incorrectAnswer = triviaQuestion.incorrectAnswer,
                                    answerOptions = randomOptions
                                )
                            }
                    }.await()
                    while(newQuestions.isEmpty()) {
                        delay(1)
                    }
                    val questions = newQuestions.toMutableList()
                    val currentQuestion = questions.last()
                    questions.remove(currentQuestion)
                    val optionsAnswers = currentQuestion.answerOptions.map { answerOption ->
                        AnswerOptionUiModel(
                            id = answerOption.id,
                            option = answerOption.answer,
                        )
                    }
                    dispatch(
                        PlayingGameActions.UpdateQuestion(
                            Triple(
                                questions,
                                currentQuestion,
                                optionsAnswers
                            )
                        )
                    )
                } ?: run { dispatch(NetworkActions.NetworkWarning) }
            } else {
                val triple = getQuestionsFromCache(gameState)
                dispatch(
                    PlayingGameActions.UpdateQuestion(
                        Triple(
                            triple.first,
                            triple.second,
                            triple.third
                        )
                    )
                )
            }
        }
    }

    private fun getQuestionsFromCache(
        gameState: GameState,
    ): Triple<List<Question>, Question,List<AnswerOptionUiModel>> {
        val questions = gameState.questions.toMutableList()
        val currentQuestion = questions.last()
        questions.remove(currentQuestion)
        val optionsAnswers = currentQuestion.answerOptions.map { answerOption ->
            AnswerOptionUiModel(
                id = answerOption.id,
                option = answerOption.answer,
            )
        }
        return Triple(questions, currentQuestion, optionsAnswers)
    }

    private suspend fun answerOptions(triviaQuestion: TriviaQuestion): MutableList<AnswerOption> {
        return coroutineScope {
            async {
                val answerOptions = mutableListOf<AnswerOption>()
                var id = 0
                triviaQuestion.incorrectAnswer.forEach { answer ->

                    if (id == 0) {
                        answerOptions.add(
                            AnswerOption(
                                id = id,
                                answer = HtmlCompat.fromHtml(triviaQuestion.correctAnswer, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
                            )
                        )
                        id++
                        answerOptions.add(AnswerOption(id = id, answer =  HtmlCompat.fromHtml(answer, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()))
                    } else {
                        answerOptions.add(AnswerOption(id = id, answer = HtmlCompat.fromHtml(answer, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()))
                    }
                    id++
                }
                answerOptions.shuffle()
                answerOptions
            }.await()
        }
    }

    companion object {
        const val ANY = "0"
        const val DEFAULT = ""

        const val EASY_DIFFICULT = "easy"
        const val MEDIUM_DIFFICULT = "medium"
        const val HARD_DIFFICULT = "hard"

        const val MULTIPLE_TYPE = "multiple"
        const val BOOLEAN_TYPE = "boolean"
        const val ID_MULTIPLE_TYPE = "1"

        const val ID_EASY_DIFFICULT = "1"
        const val ID_MEDIUM_DIFFICULT = "2"

    }
}
```

**GetRankingThunkImpl.kt** 

Thunk responsável pela função assíncrona que busca o top 10 dos últimos jogos e inserir o resultado do último jogo.

```
class GetRankingThunkImpl(@DefaultDispatcher dispatcher: CoroutineContext, private val rankingRepository: RankingRepository) :
    GetRankingThunk {

    private val scope = CoroutineScope(dispatcher)
    override fun getRanking(): Thunk<GameState> = { dispatch, getState, _->
        scope.launch {
            val ranking = rankingRepository.getRanking().map { rankingLocal->
                RankingExternal(
                    id = rankingLocal.id,
                    correctAnswers = rankingLocal.correctAnswers.toString(),
                    createdAt = DateUtils.getDateFormatted(rankingLocal.createdAt)
                )
            }
            val rankingLocal = RankingLocal(
                correctAnswers = getState().correctAnswers, createdAt = System.currentTimeMillis()
            )
            rankingRepository.insert(rankingLocal)
            dispatch(PlayingGameActions.EndOfTheGame(ranking))
        }
    }
}
```

**PrefsAndCriteriaThunkImpl.kt** 

Thunk responsável pela função assíncrona que busca o valor dos campos de critérios do jogo de acordo com as preferências salvas ou salvar novas preferências.

```
class PrefsAndCriteriaThunkImpl(
    networkContext: CoroutineDispatcher,
    private val preferences: Preferences,
    private val repository: TriviaRepository
) : PrefsAndCriteriaThunk {

    private val scope = CoroutineScope(networkContext)

    private val categories = mutableListOf<Category>()
    private val difficulties = repository.getQuestionDifficulties()
    private val types = repository.getQuestionTypes()

    override fun getCriteriaFields(): Thunk<GameState> = { dispatch, getState, _ ->
        scope.launch {

            val state = getState()
            state.networkIsActive?.let {
                categories.ifEmpty {
                    async {
                        repository.getCategories().forEach { category ->
                            categories.add(category)
                        }
                        if (categories.find { it.id == 0 } == null) {
                            categories.add(Category(id = 0, name = defaultValue))
                        }
                        categories
                    }.await()
                }

                val typeField = TypeFieldModel(
                    selected = getQuestionTypeFromIndex(preferences.getQuestionType()),
                    options = types
                )
                val difficulty = DifficultyFieldModel(
                    selected = getQuestionDifficultyFromIndex(preferences.getQuestionDifficulty()),
                    options = difficulties
                )
                val categories = CategoryFieldModel(
                    selected = getQuestionCategoryFromId(preferences.getQuestionCategory()),
                    options = categories
                )

                dispatch(
                    MenuGameActions.UpdateCriteriaFieldsState(
                        GameCriteriaUiModel(
                            typeField = DropDownMenu(field = typeField),
                            difficultyField = DropDownMenu(field = difficulty),
                            categoryField = DropDownMenu(field = categories)
                        )
                    )
                )
            } ?: run { dispatch(NetworkActions.NetworkWarning) }
        }
    }

    override fun updatePreferences(type: Int, difficulty: Int, category: Int): Thunk<GameState> =
        { dispatch, _, _ ->
            scope.launch {
                preferences.updateGamePrefs(type, difficulty, category)
                Log.d(
                    "PREFERENCES_LOGGER",
                    "PREFS_TYPE : ${preferences.getQuestionType()} " +
                            "\n PREFS_DIFFICULTY: ${preferences.getQuestionDifficulty()} " +
                            "\n PREFS_CATEGORY: ${preferences.getQuestionCategory()} "
                )
                dispatch(MenuGameActions.StartGame)
            }
        }

    private fun getQuestionDifficultyFromIndex(index: Int): QuestionDifficulty {
        return difficulties[index]
    }

    private fun getQuestionTypeFromIndex(index: Int): QuestionType {
        return types[index]
    }

    private fun getQuestionCategoryFromId(id: Int): Category {
        val category = categories.indexOfFirst { it.id == id }
        return categories[category]
    }

    companion object {
        private const val defaultValue = "Any Category"
    }
}
```


**TimerThunkImpl.kt**

Thunk responsável pelas funções assíncronas que lidam com o início do tempo ou sua pausa.

```
class TimerThunkImpl(@DefaultDispatcher dispatcher: CoroutineContext) : TimerThunk, CoroutineScope {

    override val coroutineContext: CoroutineContext = dispatcher + Job()
    private var countDownTimerJob: Job? = null
    override fun getTimerThunk(): Thunk<GameState> = { dispatch, getState, _ ->
        dispatch(PlayingGameActions.GetNewQuestion)
        getState().networkIsActive?.let {
            countDownTimerJob = CoroutineScope(coroutineContext).launch {
                var value = 10
                while (value > 0) {
                    delay(1000)
                    dispatch(TimerActions.Update)
                    value--
                }
                if(value == 0) {
                    dispatch(TimerActions.Over)
                    countDownTimerJob?.cancel()
                }
            }
        }
        countDownTimerJob as Job
    }

    override fun stopTimerJob() {
        countDownTimerJob?.cancel()
    }

}
```

# TriviaGameVm

O nosso TriviaGameVm, viewmodel, é responsável por lidar com eventos inesperados relacionados ao ciclo de vida da Activity. Além disso, ele intermedia as ações da UI com o nosso Store<GameState>.

```
@HiltViewModel
class TriviaGameVm @Inject constructor(
    private val gameUseCases: GameThunks
) : ViewModel() {

    val gameState = createStore(reducer, GameState(), applyMiddleware(
        createThunkMiddleware(), uiMiddleware(gameUseCases.timerThunk, gameUseCases.getRanking,gameUseCases.getQuestion,gameUseCases.getCategories)))

    fun onMenuGameAction(menuGameAction: MenuGameActions) {
        gameState.dispatch(menuGameAction)
    }

    fun onGamePlayingAction(gamePlayingActions: PlayingGameActions) {
        gameState.dispatch(gamePlayingActions)
    }

    fun onEndGameActions(endGameAction: EndGameActions) {
        gameState.dispatch(endGameAction)
    }

    fun changeNetworkState(state: Boolean?) {
        gameState.dispatch(NetworkActions.ChangeNetworkState(state))
        Log.d("networkState", state?.let { "available" } ?: run { "unavailable" })
    }

    fun onResume() {
        if(gameState.state.gameStatus == GameStatus.SETUP) {
            gameState.dispatch(MenuGameActions.FetchCriteriaFields)
        }
    }

    fun tryNetworkConnection() {
        gameState.dispatch(NetworkActions.TryAgain)
    }
}
```

## Previews
- Configurações do jogo
<img src ="https://github.com/rodrigoliveirac/MaTriviaApp/assets/72306040/45793102-e696-4387-b580-1263ac50c8c7" width="188" height="412">

- Quando o usuário marca a Resposta errada.
<img src ="https://github.com/rodrigoliveirac/MaTriviaApp/assets/72306040/f52e1fe0-dfbe-444c-9433-85947bc7fddb" width="188" height="412">

- Quando o usuário marca a Resposta certa.
<img src ="https://github.com/rodrigoliveirac/MaTriviaApp/assets/72306040/d4acd373-bfc9-4137-be1e-c9da8333cb90" width="188" height="412">

- Quando os 10 segundos acabam.
<img src ="https://github.com/rodrigoliveirac/MaTriviaApp/assets/72306040/76f044a0-5aa1-4356-8485-cd8a81495440" width="188" height="412">

- Quando o usuário clica em desistir do jogo no ícone inserido na top bar.
<img src ="https://github.com/rodrigoliveirac/MaTriviaApp/assets/72306040/43aa9b5f-26e1-4090-99e1-9807fcc75b55" width="188" height="412">

- Quando o jogo é encerrado depois do usuário confirmar a desistência do jogo ou após a mensagem sobre a resposta errada ou quando o tempo acaba.
<img src ="https://github.com/rodrigoliveirac/MaTriviaApp/assets/72306040/7ef9db9d-a2be-400f-a25a-182e3e97447e" width="188" height="412">

- Quando o usuário está sem internet.
<img src ="https://github.com/rodrigoliveirac/MaTriviaApp/assets/72306040/cd9ed9cd-a090-4104-b2cc-7d602057f453" width="188" height="412">

- Resumo do Fluxo do jogo

<img src="https://github.com/rodrigoliveirac/MaTriviaApp/assets/72306040/8058b550-61b9-4d34-bfc8-5194393e28cb" width="188" height="412"> 

  
