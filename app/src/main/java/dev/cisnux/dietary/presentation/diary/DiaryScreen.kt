package dev.cisnux.dietary.presentation.diary

import android.content.res.Configuration
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import dev.cisnux.dietary.R
import dev.cisnux.dietary.domain.models.Bound
import dev.cisnux.dietary.domain.models.Food
import dev.cisnux.dietary.domain.models.FoodDiaryDetail
import dev.cisnux.dietary.domain.models.Question
import dev.cisnux.dietary.presentation.ui.components.EmptyContents
import dev.cisnux.dietary.presentation.ui.components.AddedDietaryBody
import dev.cisnux.dietary.presentation.ui.components.ScannerResultShimmer
import dev.cisnux.dietary.presentation.ui.theme.DietaryTheme
import dev.cisnux.dietary.utils.AppDestination
import dev.cisnux.dietary.utils.Failure
import dev.cisnux.dietary.utils.UiState
import dev.cisnux.dietary.utils.isQuestionNotEmpty
import java.io.File

@Composable
fun DiaryScreen(
    onNavigateUp: () -> Unit,
    navigateToSignIn: (String) -> Unit,
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
    var isQuestionDialogOpen by remember(scannerResultState) {
        mutableStateOf(scannerResultState is UiState.Success && (scannerResultState as UiState.Success<FoodDiaryDetail>).data?.isQuestionNotEmpty() ?: false)
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
                if (exception is Failure.UnauthorizedFailure) {
                    viewModel.signOut()
                    navigateToSignIn(AppDestination.AddedDietaryRoute.route)
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
                if (exception is Failure.UnauthorizedFailure) {
                    viewModel.signOut()
                    navigateToSignIn(AppDestination.AddedDietaryRoute.route)
                }
            }
        }

        removeState is UiState.Success -> {
            onNavigateUp()
        }
    }

    DiaryContent(
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
                        AnimatedVisibility(visible = foodScannerResult.foods.isNotEmpty()) {
                            AddedDietaryBody(
                                totalUserCaloriesToday = foodScannerResult.totalUserCaloriesToday,
                                userDailyBmiCalorie = foodScannerResult.maxDailyBmrCalorie,
                                foods = foodScannerResult.foods,
                                totalFoodCalories = foodScannerResult.totalFoodCalories,
                                status = foodScannerResult.status,
                                feedback = foodScannerResult.feedback,
                                modifier = Modifier.padding(it)
                            )
                        }

                        AnimatedVisibility(visible = foodScannerResult.foods.isEmpty()) {
                            Box(contentAlignment = Alignment.Center) {
                                EmptyContents(
                                    label = "Tidak ada makanan yang terdeteksi",
                                    painter = painterResource(id = R.drawable.empty_foods),
                                    contentDescription = "No food added to your diary",
                                )
                            }
                        }


//                        val foodQuestions =
//                            foodScannerResult.foods.filter { food -> food.questions?.isNotEmpty() == true }
//
//                        val answeredQuestions = remember {
//                            mutableStateListOf(
//                                *foodQuestions
//                                    .map { food ->
//                                        val answers =
//                                            food.questions?.map { question ->
//                                                AnsweredQuestion(
//                                                    questionId = question.id,
//                                                    question = question.question,
//                                                    answer = "",
//                                                    choices = question.choices
//                                                )
//                                            }?.toTypedArray() ?: arrayOf()
//                                        mutableStateListOf(*answers)
//                                    }.toTypedArray()
//                            )
//                        }
//
//                        val isEnable by remember {
//                            derivedStateOf {
//                                answeredQuestions.any { foodQuestion ->
//                                    foodQuestion.any { answeredQuestion ->
//                                        answeredQuestion.answer.isNotBlank()
//                                    }
//                                }
//                            }
//                        }
//
//                        QuestionDialog(
//                            onSave = {
//                                isQuestionDialogOpen = false
//                                viewModel.updateFoodDiaryBaseOnQuestion(
//                                    foodDiaryDetail = foodScannerResult,
//                                    foodQuestions = foodQuestions,
//                                    answeredQuestions = answeredQuestions
//                                )
//                            },
//                            onCancel = { isQuestionDialogOpen = false },
//                            isDialogOpen = isQuestionDialogOpen,
//                            onDismissRequest = { isQuestionDialogOpen = false },
//                            foods = foodQuestions,
//                            isEnable = isEnable,
//                            answeredQuestions = answeredQuestions,
//                            onAnswerChange = { newAnswer, foodIndex, answerIndex ->
//                                answeredQuestions[foodIndex][answerIndex] =
//                                    answeredQuestions[foodIndex][answerIndex].copy(answer = newAnswer)
//                            },
//                        )
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
        isBackVisible = scannerResultState !is UiState.Loading,
        snackbarHostState = snackbarHostState
    )

    BackHandler {
        if (scannerResultState !is UiState.Loading)
            onNavigateUp()
    }
}

@Composable
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
)
private fun DiaryContentPreview() {
    val foodDiaryResult = FoodDiaryDetail(
        foodDiaryId = "1",
        totalFoodCalories = 200.4512f,
        maxDailyBmrCalorie = 800.6798f,
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
                bound = Bound(
                    x = 404.0,
                    y = 75.0,
                    width = 411.0,
                    height = 308.0
                ),
                questions = listOf(
                    Question(
                        id = "1",
                        question = "Apakah ini nasi putih?",
                        choices = listOf("Iya", "Tidak")
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
                bound = Bound(
                    x = 404.0,
                    y = 75.0,
                    width = 411.0,
                    height = 308.0
                ),
                questions = listOf(
                    Question(
                        id = "1",
                        question = "Apakah digoreng?",
                        choices = listOf("Ya", "Tidak")
                    ),
                    Question(
                        id = "2",
                        question = "Berapa kandungan gula dalam makanan ini?",
                        choices = listOf(
                            "Lebih dari 10% kalori harian",
                            "Kurang dari 100% kalori harian"
                        )
                    ),
                )
            ),
            Food(
                id = "3",
                name = "Tempe",
                calorie = 5.8f,
                protein = 9f,
                fat = 1f,
                bound = Bound(
                    x = 404.0,
                    y = 75.0,
                    width = 411.0,
                    height = 308.0
                ),
                carbohydrates = 8.3f,
                sugar = 0f,
                questions = listOf(
                    Question(
                        id = "1",
                        question = "Apakah digoreng?",
                        choices = listOf("Ya", "Tidak")
                    ),
                    Question(
                        id = "2",
                        question = "Berapa kandungan gula dalam makanan ini?",
                        choices = listOf(
                            "Lebih dari 10% kalori harian",
                            "Kurang dari 100% kalori harian"
                        )
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
                questions = null,
                bound = Bound(
                    x = 404.0,
                    y = 75.0,
                    width = 411.0,
                    height = 308.0
                ),
            ),
        ),
    )

    DietaryTheme {
        DiaryContent(
            onQuestion = {},
            navigateUp = {},
            body = {
                AddedDietaryBody(
                    totalUserCaloriesToday = foodDiaryResult.totalUserCaloriesToday,
                    userDailyBmiCalorie = foodDiaryResult.maxDailyBmrCalorie,
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
private fun DiaryContent(
    onQuestion: () -> Unit,
    onRemove: () -> Unit,
    navigateUp: () -> Unit,
    body: @Composable (PaddingValues) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    isBackVisible: Boolean = true,
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
                    AnimatedVisibility(visible = isBackVisible) {
                        IconButton(onClick = navigateUp) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                actions = {
//                    AnimatedVisibility(visible = isQuestionVisible) {
//                        TooltipBox(
//                            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
//                            tooltip = {
//                                PlainTooltip {
//                                    Text(text = "Tambahkan informasi")
//                                }
//                            },
//                            state = rememberTooltipState()
//                        ) {
//                            IconButton(onClick = onQuestion, enabled = isQuestionEnable) {
//                                Icon(
//                                    painter = painterResource(id = R.drawable.ic_question_exchange_24dp),
//                                    contentDescription = null,
//                                    tint = MaterialTheme.colorScheme.onSurface
//                                )
//                            }
//                        }
//                    }
                    AnimatedVisibility(visible = isRemoveVisible) {
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                            tooltip = {
                                PlainTooltip {
                                    Text(text = "Hapus\nfood diary", textAlign = TextAlign.Center)
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
    Log.d("ScannerResultScreen", isDialogOpen.toString())
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
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(count = foods.size,
//                    key = { foods[it].id },
                    contentType = { foods[it] }
                ) { index ->
                    if (index != 0) {
                        val prevIndex = index - 1
                        val isNotBlank = answeredQuestions[prevIndex].all { it.answer.isNotBlank() }
                        Log.d("isNotBlank", isNotBlank.toString())
                        Log.d("isNotBlank", answeredQuestions[prevIndex][0].toString())
                        AnimatedVisibility(visible = isNotBlank) {
                            QuestionListItem(
                                foodName = foods[index].name,
                                answeredQuestions = answeredQuestions[index],
                                onAnswerChange = { newValue, answerIndex ->
                                    onAnswerChange(newValue, index, answerIndex)
                                },
                            )
                        }
                    } else {
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
}

@Composable
fun QuestionListItem(
    foodName: String,
    answeredQuestions: List<AnsweredQuestion>,
    onAnswerChange: (newAnswer: String, index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
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
            val size by remember {
                derivedStateOf {
                    answeredQuestions
                        .filter { it.answer.isNotBlank() }
                        .size
                        .let {
                            if (it < answeredQuestions.size)
                                it + 1
                            else
                                it
                        }
                }
            }
            List(size) { index ->
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialTheme.shapes.extraLarge.copy(
                                topStart = CornerSize(2.dp),
                                bottomEnd = CornerSize(2.dp)
                            ),
                        ) {
                            Text(
                                text = answeredQuestions[index].question,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow {
                        items(
                            answeredQuestions[index].choices,
//                            key = { it },
                            contentType = { it }
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                shape = MaterialTheme.shapes.extraLarge,
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.extraLarge)
                                    .clickable {
                                        onAnswerChange(it, index)
                                    }
                            ) {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    }
                    AnimatedVisibility(visible = answeredQuestions[index].answer.isNotBlank()) {
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = MaterialTheme.shapes.extraLarge.copy(
                                        topEnd = CornerSize(2.dp),
                                        bottomStart = CornerSize(2.dp)
                                    ),
                                ) {
                                    Text(
                                        text = answeredQuestions[index].answer,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 8.dp
                                        )
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}