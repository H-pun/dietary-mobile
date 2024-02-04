package dev.cisnux.dietary.presentation.scannerresult

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import dev.cisnux.dietary.R
import dev.cisnux.dietary.domain.models.Food
import dev.cisnux.dietary.domain.models.FoodDiaryDetail
import dev.cisnux.dietary.domain.models.Question
import dev.cisnux.dietary.presentation.ui.components.ScannerResultBody
import dev.cisnux.dietary.presentation.ui.components.ScannerResultShimmer
import dev.cisnux.dietary.presentation.ui.theme.DietaryTheme
import dev.cisnux.dietary.utils.QuestionType
import dev.cisnux.dietary.utils.UiState
import dev.cisnux.dietary.utils.isQuestionNotEmpty
import dev.cisnux.dietary.utils.isFloatAnswerValid
import dev.cisnux.dietary.utils.isIntAnswerValid
import java.io.File

@Composable
fun ScannerResultScreen(
    onNavigateUp: () -> Unit,
    foodPicture: File?,
    modifier: Modifier = Modifier,
    viewModel: ScannerResultViewModel = hiltViewModel(),
) {
    val oneTimeAddFoodDiary by rememberUpdatedState(viewModel::addFoodDiary)
    LaunchedEffect(Unit) {
        foodPicture?.let { oneTimeAddFoodDiary(it) }
    }
    val scannerResultState by viewModel.scannerResultState.collectAsState()
    val removeState by viewModel.removeState.collectAsState()
    var isQuestionDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    when {
        scannerResultState is UiState.Error -> {
            (scannerResultState as UiState.Error).error?.let { exception ->
                LaunchedEffect(snackbarHostState) {
                    exception.message?.let {
                        snackbarHostState.showSnackbar(
                            message = it,
                            withDismissAction = true,
                            duration = SnackbarDuration.Long
                        )
                    }
                }
            }
        }

        removeState is UiState.Error -> {
            (removeState as UiState.Error).error?.let { exception ->
                LaunchedEffect(snackbarHostState) {
                    exception.message?.let {
                        snackbarHostState.showSnackbar(
                            message = it,
                            withDismissAction = true,
                            duration = SnackbarDuration.Long
                        )
                    }
                }
            }
        }

        removeState is UiState.Success -> {
            onNavigateUp()
        }
    }

    ScannerResultContent(
        onQuestion = { isQuestionDialogOpen = true },
        onRemove = {
            if (scannerResultState is UiState.Success) {
                (scannerResultState as UiState.Success<FoodDiaryDetail>).data?.let { foodScannerResult ->
                    viewModel.deleteFoodDiaryById(foodDiaryId = foodScannerResult.foodDiaryId)
                }
            }
        },
        navigateUp = onNavigateUp,
        body = {
            when (scannerResultState) {
                is UiState.Success -> {
                    (scannerResultState as UiState.Success<FoodDiaryDetail>).data?.let { foodScannerResult ->
                        ScannerResultBody(
                            totalUserCaloriesToday = foodScannerResult.totalUserCaloriesToday,
                            userDailyBmiCalorie = foodScannerResult.maxDailyBmiCalorie,
                            foods = foodScannerResult.foods,
                            totalFoodCalories = foodScannerResult.totalFoodCalories,
                            status = foodScannerResult.status,
                            feedback = foodScannerResult.feedback,
                            modifier = Modifier.padding(it)
                        )

                        val foodQuestions =
                            foodScannerResult.foods.filter { food -> food.questions?.isNotEmpty() == true }

                        val answeredQuestions = remember {
                            mutableStateListOf(
                                *foodQuestions
                                    .map { food ->
                                        val answers =
                                            food.questions?.map { question ->
                                                AnsweredQuestion(
                                                    questionId = question.id,
                                                    question = question.question,
                                                    type = question.type,
                                                    label = question.label,
                                                    answer = "",
                                                    unit = question.unit
                                                )
                                            }?.toTypedArray() ?: arrayOf()
                                        mutableStateListOf(*answers)
                                    }.toTypedArray()
                            )
                        }

                        val isEnable by derivedStateOf {
                            answeredQuestions.all { foodQuestion ->
                                foodQuestion.all { answeredQuestion ->
                                    when (answeredQuestion.type) {
                                        QuestionType.INTEGER -> answeredQuestion.answer.isIntAnswerValid()
                                        QuestionType.FLOAT -> answeredQuestion.answer.isFloatAnswerValid()
                                        else -> answeredQuestion.answer.isNotBlank()
                                    }
                                }
                            }
                        }

                        QuestionDialog(
                            onSave = {
                                isQuestionDialogOpen = false
                                viewModel.updateFoodDiaryBaseOnQuestion(
                                    foodDiaryDetail = foodScannerResult,
                                    foodQuestions = foodQuestions,
                                    answeredQuestions = answeredQuestions
                                )
                            },
                            onCancel = { isQuestionDialogOpen = false },
                            isDialogOpen = isQuestionDialogOpen,
                            onDismissRequest = { isQuestionDialogOpen = false },
                            foods = foodQuestions,
                            isEnable = isEnable,
                            answeredQuestions = answeredQuestions,
                            onAnswerChange = { newAnswer, foodIndex, answerIndex ->
                                answeredQuestions[foodIndex][answerIndex] =
                                    answeredQuestions[foodIndex][answerIndex].copy(answer = newAnswer)
                            },
                        )
                    }
                }

                else -> {
                    ScannerResultShimmer(modifier = Modifier.padding(it))
                }
            }
        },
        modifier = modifier,
        isQuestionVisible = scannerResultState is UiState.Success && (scannerResultState as UiState.Success<FoodDiaryDetail>).data?.isQuestionNotEmpty() ?: false,
        isRemoveVisible = scannerResultState is UiState.Success,
        isRemoveEnable = removeState !is UiState.Loading,
        isQuestionEnable = scannerResultState !is UiState.Loading,
        snackbarHostState = snackbarHostState
    )
}

@Composable
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
)
private fun ScannerResultContentPreview() {
    val foodDiaryResult = FoodDiaryDetail(
        foodDiaryId = "1",
        totalFoodCalories = 200.4512f,
        maxDailyBmiCalorie = 800.6798f,
        totalUserCaloriesToday = 500.7892f,
        status = "Boleh dimakan",
        feedback = "Terlalu banyak gula",
        foods = listOf(
            Food(
                id = "1",
                name = "Nasi",
                calorie = 50f,
                protein = 8f,
                fat = 2f,
                carbohydrates = 4.3f,
                sugar = 8.7f,
                questions = listOf(
                    Question(
                        id = "1",
                        label = "Minyak",
                        question = "Apakah makan digoreng dengan minyak berkali-kali?",
                        type = QuestionType.BOOLEAN,
                        unit = null
                    ),
                    Question(
                        id = "2",
                        label = "Gula",
                        question = "Berapa kandungan gula dalam makanan ini?",
                        type = QuestionType.FLOAT,
                        unit = "g"
                    ),
                )
            ),
            Food(
                id = "2",
                name = "Ayam Bakar",
                calorie = 50f,
                protein = 6f,
                fat = 10f,
                carbohydrates = 8.3f,
                sugar = null,
                questions = listOf(
                    Question(
                        id = "2",
                        label = "Gula",
                        question = "Berapa kandungan gula dalam makanan ini?",
                        type = QuestionType.FLOAT,
                        unit = "g"
                    ),
                )
            ),
            Food(
                id = "3",
                name = "Tempe Goreng",
                calorie = 5.8f,
                protein = 9f,
                fat = 1f,
                carbohydrates = 8.3f,
                sugar = 0f,
                questions = listOf(
                    Question(
                        id = "2",
                        label = "Gula",
                        question = "Berapa kandungan gula dalam makanan ini?",
                        type = QuestionType.FLOAT,
                        unit = "g"
                    ),
                )
            ),
            Food(
                id = "4",
                name = "Sayur Kangkung",
                calorie = 5.8f,
                protein = 9f,
                fat = 0.5f,
                carbohydrates = 8.3f,
                sugar = 0f,
                questions = listOf(
                    Question(
                        id = "2",
                        label = "Gula",
                        question = "Berapa kandungan gula dalam makanan ini?",
                        type = QuestionType.FLOAT,
                        unit = "g"
                    ),
                )
            ),
        )
    )

    DietaryTheme {
        ScannerResultContent(
            onQuestion = {},
            navigateUp = {},
            body = {
                ScannerResultBody(
                    totalUserCaloriesToday = foodDiaryResult.totalUserCaloriesToday,
                    userDailyBmiCalorie = foodDiaryResult.maxDailyBmiCalorie,
                    totalFoodCalories = foodDiaryResult.totalFoodCalories,
                    foods = foodDiaryResult.foods,
                    status = foodDiaryResult.status,
                    feedback = foodDiaryResult.feedback,
                    modifier = Modifier.padding(it)
                )
            },
            isQuestionVisible = foodDiaryResult.isQuestionNotEmpty(),
            onRemove = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScannerResultContent(
    onQuestion: () -> Unit,
    onRemove: () -> Unit,
    navigateUp: () -> Unit,
    body: @Composable (PaddingValues) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    isQuestionVisible: Boolean = true,
    isRemoveVisible: Boolean = true,
    isRemoveEnable: Boolean = true,
    isQuestionEnable: Boolean = true,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.food),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    AnimatedVisibility(visible = isQuestionVisible) {
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                            tooltip = {
                                PlainTooltip {
                                    Text(text = "Tambahkan informasi")
                                }
                            },
                            state = rememberTooltipState()
                        ) {
                            IconButton(onClick = onQuestion, enabled = isQuestionEnable) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_question_exchange_24dp),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    AnimatedVisibility(visible = isRemoveVisible) {
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                            tooltip = {
                                PlainTooltip {
                                    Text(text = "Hapus\nmakanan", textAlign = TextAlign.Center)
                                }
                            },
                            state = rememberTooltipState()
                        ) {
                            IconButton(onClick = onRemove, enabled = isRemoveEnable) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_outline_cancel_24dp),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                })
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier,
    ) { innerPadding ->
        body(innerPadding)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuestionDialog(
    onSave: () -> Unit,
    onCancel: () -> Unit,
    isDialogOpen: Boolean,
    onDismissRequest: () -> Unit,
    foods: List<Food>,
    answeredQuestions: List<List<AnsweredQuestion>>,
    onAnswerChange: (newAnswer: String, foodIndex: Int, answerIndex: Int) -> Unit,
    isEnable: Boolean,
    modifier: Modifier = Modifier
) {
    if (isDialogOpen) Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
        ),
    ) {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

        Scaffold(
            modifier = modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onCancel) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                tint = MaterialTheme.colorScheme.onSurface,
                                contentDescription = null
                            )
                        }
                    },
                    title = {
                        Text(
                            text = stringResource(R.string.question),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.ExtraBold
                        )
                    },
                    actions = {
                        TextButton(
                            onClick = onSave,
                            enabled = isEnable
                        ) {
                            Text(
                                text = stringResource(id = R.string.save),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            }
        ) { paddingValues ->
            LazyColumn(
                contentPadding = paddingValues,
                modifier = Modifier
                    .consumeWindowInsets(paddingValues)
            ) {
                items(count = foods.size,
                    key = { foods[it].id },
                    contentType = { foods[it] }) { index ->
                    QuestionListItem(
                        foodName = foods[index].name,
                        answeredQuestions = answeredQuestions[index],
                        onAnswerChange = { newValue, answerIndex ->
                            onAnswerChange(newValue, index, answerIndex)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun QuestionListItem(
    foodName: String,
    answeredQuestions: List<AnsweredQuestion>,
    onAnswerChange: (newAnswer: String, index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val focuses = remember {
        mutableStateListOf(*answeredQuestions.map { false }.toTypedArray())
    }

    Column(Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "✧",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = foodName,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = modifier
                .padding(start = 26.dp)
                .fillMaxWidth()
        ) {
            List(answeredQuestions.size) { index ->
                val isNotValid =
                    when (answeredQuestions[index].type) {
                        QuestionType.INTEGER -> !answeredQuestions[index].answer.isIntAnswerValid()
                        QuestionType.FLOAT -> !answeredQuestions[index].answer.isFloatAnswerValid()
                        else -> answeredQuestions[index].answer.isBlank()
                    }

                when (answeredQuestions[index].type) {
                    QuestionType.BOOLEAN -> {
                        var state by rememberSaveable { mutableStateOf(true) }

                        LaunchedEffect(state) {
                            onAnswerChange(state.toString(), index)
                        }

                        Column {
                            Text(
                                text = answeredQuestions[index].question,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.selectableGroup()
                            ) {
                                RadioButton(
                                    selected = state,
                                    onClick = { state = true },
                                )
                                Text(
                                    text = stringResource(id = R.string.yes),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = !state,
                                    onClick = { state = false },
                                )
                                Text(
                                    text = stringResource(R.string.no),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    else -> {
                        val errorText = stringResource(
                            id = when (answeredQuestions[index].type) {
                                QuestionType.FLOAT -> R.string.error_text_float
                                QuestionType.INTEGER -> R.string.error_text_int
                                else -> R.string.error_text_text
                            },
                            answeredQuestions[index].label
                        )
                        val keyboardType =
                            if (answeredQuestions[index].type == QuestionType.BOOLEAN) KeyboardType.Text else KeyboardType.Number
                        val imeAction = ImeAction.Done

                        OutlinedTextField(
                            keyboardOptions = KeyboardOptions(
                                keyboardType = keyboardType, imeAction = imeAction
                            ),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_question_mark_24dp),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            value = answeredQuestions[index].answer,
                            singleLine = true,
                            onValueChange = { onAnswerChange(it, index) },
                            placeholder = {
                                Text(
                                    text = answeredQuestions[index].question,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            label = {
                                Text(
                                    text = answeredQuestions[index].label,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            supportingText = {
                                if (answeredQuestions[index].answer.isNotEmpty() and isNotValid) Text(
                                    text = errorText,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                                else if (focuses[index]) Text(
                                    text = stringResource(R.string.supporting_text_required),
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            },
                            isError = answeredQuestions[index].answer.isNotEmpty() and isNotValid,
                            trailingIcon = {
                                if (answeredQuestions[index].answer.isNotEmpty() and isNotValid) Icon(
                                    painter = painterResource(id = R.drawable.ic_round_error_24dp),
                                    contentDescription = null,
                                )
                                else answeredQuestions[index].unit?.let { Text(text = it) }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged {
                                    focuses[index] = it.isFocused
                                }
                                .imePadding(),
                        )
                    }
                }
            }
        }
    }
}
