package dev.cisnux.dietary.presentation.diary

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
import java.io.File

@Composable
fun DiaryScreen(
    onNavigateUp: () -> Unit,
    navigateToSignIn: (String) -> Unit,
    foodPicture: File?,
    modifier: Modifier = Modifier,
    viewModel: FoodDiaryViewModel = hiltViewModel(),
) {
    val oneTimeAddFoodDiary by rememberUpdatedState(viewModel::addFoodDiary)
    LaunchedEffect(Unit) {
        foodPicture?.let { oneTimeAddFoodDiary(it) }
    }
    val scannerResultState by viewModel.scannerResultState.collectAsState()
    val removeState by viewModel.removeState.collectAsState()
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
                    navigateToSignIn(AppDestination.DiaryRoute.route)
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
                    navigateToSignIn(AppDestination.DiaryRoute.route)
                }
            }
        }

        removeState is UiState.Success -> {
            onNavigateUp()
        }
    }

    DiaryContent(
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
                                feedbacks = foodScannerResult.feedbacks,
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
                    }
                }

                else -> {
                    ScannerResultShimmer(modifier = Modifier.padding(it))
                }
            }
        },
        modifier = modifier,
        isRemoveVisible = scannerResultState is UiState.Success,
        isRemoveEnable = removeState !is UiState.Loading,
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
        feedbacks = listOf(
            "Terlalu berminyak",
            "Terlalu banyak gula",
            "Kurang protein",
        ),
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
                        options = listOf()
                    ),
                    Question(
                        id = "2",
                        question = "Berapa kandungan gula dalam makanan ini?",
                        options = listOf()
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
                        options = listOf()
                    ),
                    Question(
                        id = "2",
                        question = "Berapa kandungan gula dalam makanan ini?",
                        options = listOf()
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
            navigateUp = {},
            body = {
                AddedDietaryBody(
                    totalUserCaloriesToday = foodDiaryResult.totalUserCaloriesToday,
                    userDailyBmiCalorie = foodDiaryResult.maxDailyBmrCalorie,
                    totalFoodCalories = foodDiaryResult.totalFoodCalories,
                    foods = foodDiaryResult.foods,
                    status = foodDiaryResult.status,
                    feedbacks = foodDiaryResult.feedbacks,
                    modifier = Modifier.padding(it)
                )
            },
            onRemove = {},
            snackbarHostState = SnackbarHostState()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DiaryContent(
    onRemove: () -> Unit,
    navigateUp: () -> Unit,
    body: @Composable (PaddingValues) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    isBackVisible: Boolean = true,
    isRemoveVisible: Boolean = true,
    isRemoveEnable: Boolean = true,
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