package dev.cisnux.dietary.presentation.predictedresult

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import dev.cisnux.dietary.R
import dev.cisnux.dietary.domain.models.Bound
import dev.cisnux.dietary.domain.models.PredictedFood
import dev.cisnux.dietary.presentation.diary.AnsweredQuestion
import dev.cisnux.dietary.presentation.diary.QuestionListItem
import dev.cisnux.dietary.presentation.ui.theme.DietaryTheme
import dev.cisnux.dietary.presentation.ui.theme.Typography
import dev.cisnux.dietary.utils.AppDestination
import dev.cisnux.dietary.utils.Failure
import dev.cisnux.dietary.utils.UiState
import java.io.File

@Composable
fun PredictedResultScreen(
    onNavigateUp: () -> Unit,
    navigateToSignIn: (String) -> Unit,
    foodPicture: File?,
    modifier: Modifier = Modifier,
    viewModel: PredictedResultViewModel = hiltViewModel()
) {
    val oneTimePredictedFoods by rememberUpdatedState(viewModel::predictFoods)
    LaunchedEffect(Unit) {
        foodPicture?.let { oneTimePredictedFoods(it) }
    }

    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val predictedResultState by viewModel.predictedResultState.collectAsState()

    if (predictedResultState is UiState.Error) {
        (predictedResultState as UiState.Error).error?.let { exception ->
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
    var isQuestionDialogOpen by remember {
        mutableStateOf(false)
    }
    var isAddDiaryDialogOpen by remember {
        mutableStateOf(false)
    }
    val foods = if (predictedResultState is UiState.Success)
        (predictedResultState as UiState.Success<List<PredictedFood>>).data ?: listOf()
    else listOf()

    val foodQuestions =
        foods.filter { food -> food.questions.isNotEmpty() }

    val answeredQuestions = remember(foodQuestions) {
        mutableStateListOf(
            *foodQuestions
                .map { food ->
                    val answers =
                        food.questions.map { question ->
                            AnsweredQuestion(
                                questionId = question.id,
                                question = question.question,
                                answer = "",
                                choices = question.choices
                            )
                        }.toTypedArray()
                    mutableStateListOf(*answers)
                }.toTypedArray()
        )
    }

    val isEnable by remember(answeredQuestions) {
        derivedStateOf {
            answeredQuestions.any { foodQuestion ->
                foodQuestion.any { answeredQuestion ->
                    answeredQuestion.answer.isNotBlank()
                }
            }
        }
    }

    PredictedResultContent(
        snackbarHostState = snackbarHostState,
        isAddDietaryFabVisible = foods.isNotEmpty(),
        modifier = modifier,
        onAddDiaryFab = {
            isAddDiaryDialogOpen = true
        }
    ) {
        PredictedResultBody(
            foodPictures = foodPicture,
            onNavigateUp = onNavigateUp,
            onQuestionDialog = { isQuestionDialogOpen = true },
            modifier = Modifier.padding(it),
            isQuestionDialogButtonEnable = true,
            isQuestionDialogButtonVisible = foods.any { predictedFood ->
                predictedFood.questions.isNotEmpty()
            },
            foods = foods,
        )

        QuestionDialog(
            onSave = {
                isQuestionDialogOpen = false
            },
            onCancel = {
                isQuestionDialogOpen = false
                answeredQuestions.forEachIndexed { foodIndex, items ->
                    items.forEachIndexed { answeredIndex, answeredQuestion ->
                        answeredQuestions[foodIndex][answeredIndex] =
                            answeredQuestion.copy(answer = "")
                    }
                }
            },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuestionDialog(
    onSave: () -> Unit,
    onCancel: () -> Unit,
    isDialogOpen: Boolean,
    onDismissRequest: () -> Unit,
    foods: List<PredictedFood>,
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
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(count = foods.size,
                    key = { foods[it].id },
                    contentType = { foods[it] }
                ) { index ->
                    if (index != 0) {
                        val prevIndex = index - 1
                        val isNotBlank = answeredQuestions[prevIndex].all { it.answer.isNotBlank() }
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

@Preview(
    showBackground = true, showSystemUi = false, device = "id:pixel_8_pro",
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun PredictedResultContentPreview() {
    DietaryTheme {
        PredictedResultContent(snackbarHostState = SnackbarHostState()) {
            PredictedResultBody(
                foodPictures = null,
                onQuestionDialog = {},
                onNavigateUp = {},
                foods = listOf(
                    PredictedFood(
                        id = "1",
                        name = "Nasi Putih",
                        bound = Bound(
                            x = 404.0,
                            y = 75.0,
                            width = 411.0,
                            height = 308.0
                        )
                    ),
                    PredictedFood(
                        id = "2",
                        name = "Ayam Goreng",
                        bound = Bound(
                            x = 197.0,
                            y = 275.0,
                            width = 434.0,
                            height = 277.0
                        )
                    ),
                ),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PredictedResultBody(
    foodPictures: File?,
    onNavigateUp: () -> Unit,
    onQuestionDialog: () -> Unit,
    modifier: Modifier = Modifier,
    isQuestionDialogButtonEnable: Boolean = true,
    isQuestionDialogButtonVisible: Boolean = true,
    foods: List<PredictedFood> = listOf(),
) {
    Box(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = foods.isEmpty(), modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
        }
        AnimatedVisibility(visible = foods.isNotEmpty()) {
            var scale by remember { mutableFloatStateOf(1f) }
            var rotation by remember { mutableFloatStateOf(0f) }
            var offset by remember { mutableStateOf(Offset.Zero) }

            val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
                scale *= zoomChange
                rotation += rotationChange
                offset += offsetChange
            }

            Box(
                Modifier
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        rotationZ = rotation,
                        translationX = offset.x,
                        translationY = offset.y
                    )
                    // add transformable to listen to multitouch transformation events
                    // after offset
                    .transformable(state = state)
                    .fillMaxSize()
            ) {
                val textMeasurer = rememberTextMeasurer()
                val context = LocalContext.current
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context).data(
                        foodPictures
                    ).build(),
                    contentScale = ContentScale.Fit,
                    contentDescription = null,
                    modifier = Modifier
                        .drawWithContent {
                            drawContent()
                            foods.forEach {
                                val foodSize = Size(
                                    width = it.bound.width.toFloat(),
                                    height = it.bound.height.toFloat()
                                )
                                val measuredText =
                                    textMeasurer.measure(
                                        AnnotatedString(it.name),
                                        overflow = TextOverflow.Ellipsis,
                                        style = Typography.labelSmall.copy(fontSize = 8.sp)
                                    )
                                drawRect(
                                    topLeft = foodSize.toRect().topLeft.copy(
                                        x = it.bound.x.toFloat(),
                                        y = it.bound.y.toFloat() - 40f
                                    ),
                                    color = Color.White,
                                    size = measuredText.size
                                        .toSize()
                                        .copy(
                                            width = measuredText.size.toSize().width + 8,
                                            height = measuredText.size.toSize().height + 8
                                        )
                                )
                                drawText(
                                    measuredText, topLeft = foodSize.toRect().topLeft.copy(
                                        x = it.bound.x.toFloat() + 4,
                                        y = it.bound.y.toFloat() - 36f
                                    )
                                )
                                drawRoundRect(
                                    topLeft = foodSize.toRect().topLeft.copy(
                                        x = it.bound.x.toFloat(),
                                        y = it.bound.y.toFloat()
                                    ),
                                    color = Color.White,
                                    cornerRadius = CornerRadius(2f, 2f),
                                    style = Stroke(width = 4f),
                                    size = foodSize
                                )
                            }
                        }
                        .align(Alignment.Center)
                )
            }
        }
        TooltipBox(
            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
            tooltip = {
                PlainTooltip {
                    Text(text = "Hapus\nfood diary", textAlign = TextAlign.Center)
                }
            },
            state = rememberTooltipState(),
            modifier = Modifier
                .padding(top = 12.dp, start = 12.dp)
                .align(Alignment.TopEnd)
        ) {
            FilledIconButton(
                onClick = onNavigateUp,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color.Black.copy(alpha = 0.5f)
                ),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_outline_cancel_24dp),
                    contentDescription = null,
                    tint = Color.White,
                )
            }
        }
        AnimatedVisibility(
            visible = isQuestionDialogButtonVisible,
            modifier = Modifier
                .padding(top = 12.dp, end = 12.dp)
                .align(Alignment.TopEnd)
        ) {
            FilledIconButton(
                onClick = onQuestionDialog,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color.Black.copy(alpha = 0.5f)
                ),
                enabled = isQuestionDialogButtonEnable
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_question_exchange_24dp),
                    contentDescription = null,
                    tint = Color.White,
                )
            }
        }
    }
}

@Composable
fun PredictedResultContent(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    isAddDietaryFabVisible: Boolean = false,
    onAddDiaryFab: () -> Unit = {},
    body: @Composable (PaddingValues) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            AnimatedVisibility(visible = isAddDietaryFabVisible) {
                FloatingActionButton(onClick = onAddDiaryFab) {
                    Icon(imageVector = Icons.Rounded.AddCircle, contentDescription = null)
                }
            }
        },
    ) {
        body(it)
    }
}