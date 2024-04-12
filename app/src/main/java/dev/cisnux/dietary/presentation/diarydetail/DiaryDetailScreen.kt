package dev.cisnux.dietary.presentation.diarydetail

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import dev.cisnux.dietary.R
import dev.cisnux.dietary.domain.models.Bound
import dev.cisnux.dietary.domain.models.Food
import dev.cisnux.dietary.domain.models.FoodDiaryDetail
import dev.cisnux.dietary.presentation.ui.components.AddedDietaryBody
import dev.cisnux.dietary.presentation.ui.components.ScannerResultShimmer
import dev.cisnux.dietary.presentation.ui.theme.DietaryTheme
import dev.cisnux.dietary.presentation.ui.theme.Typography
import dev.cisnux.dietary.utils.AppDestination
import dev.cisnux.dietary.utils.Failure
import dev.cisnux.dietary.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryDetailScreen(
    modifier: Modifier = Modifier,
    navigateToSignIn: (String) -> Unit,
    navigateUp: () -> Unit,
    viewModel: DiaryDetailViewModel = hiltViewModel()
) {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberModalBottomSheetState()
    )
    LaunchedEffect(Unit) {
        scaffoldState.bottomSheetState.expand()
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val foodDiaryDetailState by viewModel.foodDiaryDetailState.collectAsState()
    val removeState by viewModel.removeState.collectAsState()
    val context = LocalContext.current

    when {
        foodDiaryDetailState is UiState.Error -> (foodDiaryDetailState as UiState.Error).error?.let { exception ->
            LaunchedEffect(snackbarHostState) {
                exception.message?.let {
                    val snackbarResult = snackbarHostState.showSnackbar(
                        message = it,
                        actionLabel = context.getString(R.string.retry),
                        withDismissAction = true,
                        duration = SnackbarDuration.Long
                    )
                    if (snackbarResult == SnackbarResult.ActionPerformed) viewModel.getFoodDiaryDetailById()
                }
            }
            if (exception is Failure.UnauthorizedFailure)
                navigateToSignIn(AppDestination.DiaryDetailRoute.route)
        }

        removeState is UiState.Error -> (removeState as UiState.Error).error?.let { exception ->
            LaunchedEffect(snackbarHostState) {
                exception.message?.let {
                    val snackbarResult = snackbarHostState.showSnackbar(
                        message = it,
                        actionLabel = context.getString(R.string.retry),
                        withDismissAction = true,
                        duration = SnackbarDuration.Long
                    )
                    if (snackbarResult == SnackbarResult.ActionPerformed)
                        viewModel.deleteFoodDiaryById()
                }
            }
        }

        removeState is UiState.Success -> navigateUp()
    }

    DiaryDetailContent(
        snackbarHostState = snackbarHostState,
        body = {
            DiaryDetailBody(
                foodPictures = if (foodDiaryDetailState is UiState.Success)
                    (foodDiaryDetailState as UiState.Success<FoodDiaryDetail>).data?.foodPicture
                else null,
                onNavigateUp = navigateUp,
                onRemove = viewModel::deleteFoodDiaryById,
                modifier = Modifier.padding(it),
                isRemoveVisible = foodDiaryDetailState is UiState.Success,
                isRemoveEnable = removeState !is UiState.Loading,
            )
        },
        sheetContent = {
            AnimatedVisibility(foodDiaryDetailState is UiState.Success) {
                (foodDiaryDetailState as UiState.Success<FoodDiaryDetail>).data?.let { foodDiaryDetail ->
                    AddedDietaryBody(
                        totalUserCaloriesToday = foodDiaryDetail.totalUserCaloriesToday,
                        userDailyBmiCalorie = foodDiaryDetail.maxDailyBmrCalorie,
                        totalFoodCalories = foodDiaryDetail.totalFoodCalories,
                        foods = foodDiaryDetail.foods,
                        status = foodDiaryDetail.status,
                        feedback = foodDiaryDetail.feedback,
                    )
                }
            }
            AnimatedVisibility(visible = foodDiaryDetailState is UiState.Loading || foodDiaryDetailState is UiState.Error) {
                ScannerResultShimmer()
            }
        },
        bottomSheetScaffoldState = scaffoldState,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
)
@Composable
private fun DiaryDetailContentPreview() {
    val foodDiaryDetail = FoodDiaryDetail(
        foodDiaryId = "1",
        totalFoodCalories = 200.4512f,
        maxDailyBmrCalorie = 800.6798f,
        totalUserCaloriesToday = 500.7892f,
        status = "Kurang disarankan",
        feedback = "Terlalu banyak gula",
        foodPicture = "https://firebasestorage.googleapis.com/v0/b/dietary-f1812.appspot.com/o/images%2FWhatsApp%20Image%202024-03-26%20at%2019.30.43.jpeg?alt=media&token=e89da25b-baa6-481b-b4d0-cb7c9504e759",
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
                bound = Bound(
                    x = 404.0,
                    y = 75.0,
                    width = 411.0,
                    height = 308.0
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
                bound = Bound(
                    x = 404.0,
                    y = 75.0,
                    width = 411.0,
                    height = 308.0
                )
            ),
        )
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberModalBottomSheetState()
    )

    LaunchedEffect(Unit) {
        scaffoldState.bottomSheetState.expand()
    }

    DietaryTheme {
        DiaryDetailContent(
            snackbarHostState = SnackbarHostState(),
            body = {
                foodDiaryDetail.foodPicture?.let { foodPicture ->
                    DiaryDetailBody(
                        foodPictures = foodPicture,
                        onNavigateUp = { /*TODO*/ },
                        onRemove = { /*TODO*/ },
                        modifier = Modifier.padding(it)
                    )
                }
            },
            sheetContent = {
                AddedDietaryBody(
                    totalUserCaloriesToday = foodDiaryDetail.totalUserCaloriesToday,
                    userDailyBmiCalorie = foodDiaryDetail.maxDailyBmrCalorie,
                    totalFoodCalories = foodDiaryDetail.totalFoodCalories,
                    foods = foodDiaryDetail.foods,
                    status = foodDiaryDetail.status,
                    feedback = foodDiaryDetail.feedback,
                )
            },
            bottomSheetScaffoldState = scaffoldState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
)
@Composable
private fun DiaryDetailContentBoundsPreview() {
    val foodDiaryDetail = FoodDiaryDetail(
        foodDiaryId = "1",
        totalFoodCalories = 200.4512f,
        maxDailyBmrCalorie = 800.6798f,
        totalUserCaloriesToday = 500.7892f,
        status = "Kurang disarankan",
        feedback = "Terlalu banyak gula",
        foodPicture = "https://firebasestorage.googleapis.com/v0/b/dietary-f1812.appspot.com/o/images%2FWhatsApp%20Image%202024-03-26%20at%2019.30.43.jpeg?alt=media&token=e89da25b-baa6-481b-b4d0-cb7c9504e759",
        foods = listOf(
            Food(
                id = "1",
                name = "Nasi Putih",
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
                )
            ),
            Food(
                id = "2",
                name = "Ayam Goreng",
                calorie = 50f,
                protein = 6f,
                fat = 10f,
                carbohydrates = 8.3f,
                sugar = null,
                bound = Bound(
                    x = 197.0,
                    y = 275.0,
                    width = 434.0,
                    height = 277.0
                )
            ),
        )
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberModalBottomSheetState()
    )

    LaunchedEffect(Unit) {
        scaffoldState.bottomSheetState.expand()
    }

    DietaryTheme {
        DiaryDetailContent(
            snackbarHostState = SnackbarHostState(),
            body = {
                foodDiaryDetail.foodPicture?.let { foodPicture ->
                    DiaryDetailBody(
                        foodPictures = foodPicture,
                        onNavigateUp = { /*TODO*/ },
                        onRemove = { /*TODO*/ },
                        modifier = Modifier.padding(it),
                        foods = foodDiaryDetail.foods
                    )
                }
            },
            sheetContent = {
                AddedDietaryBody(
                    totalUserCaloriesToday = foodDiaryDetail.totalUserCaloriesToday,
                    userDailyBmiCalorie = foodDiaryDetail.maxDailyBmrCalorie,
                    totalFoodCalories = foodDiaryDetail.totalFoodCalories,
                    foods = foodDiaryDetail.foods,
                    status = foodDiaryDetail.status,
                    feedback = foodDiaryDetail.feedback,
                )
            },
            bottomSheetScaffoldState = scaffoldState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryDetailBody(
    foodPictures: String?,
    onNavigateUp: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    isRemoveEnable: Boolean = true,
    isRemoveVisible: Boolean = true,
    foods: List<Food> = listOf()
) {
    Box(modifier = modifier) {
        AnimatedVisibility(
            visible = foodPictures == null, modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
        }
        AnimatedVisibility(visible = foodPictures != null) {
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
                SubcomposeAsyncImage(
                    model = foodPictures,
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
                                    size = measuredText.size.toSize().copy(width = measuredText.size.toSize().width + 8, height = measuredText.size.toSize().height + 8)
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
        FilledIconButton(
            onClick = onNavigateUp,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = Color.Black.copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .padding(start = 12.dp, top = 12.dp)
                .align(Alignment.TopStart),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = null,
                tint = Color.White,
            )
        }
        AnimatedVisibility(
            visible = isRemoveVisible,
            modifier = modifier
                .padding(top = 12.dp, end = 12.dp)
                .align(Alignment.TopEnd)
        ) {
            TooltipBox(
                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                tooltip = {
                    PlainTooltip {
                        Text(text = "Hapus\nfood diary", textAlign = TextAlign.Center)
                    }
                },
                state = rememberTooltipState(),
            ) {
                FilledIconButton(
                    onClick = onRemove,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.5f)
                    ),
                    enabled = isRemoveEnable
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_outline_cancel_24dp),
                        contentDescription = null,
                        tint = Color.White,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DiaryDetailContent(
    snackbarHostState: SnackbarHostState,
    body: @Composable (PaddingValues) -> Unit,
    sheetContent: @Composable ColumnScope.() -> Unit,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    modifier: Modifier = Modifier,
) {
    BottomSheetScaffold(
        modifier = modifier,
        sheetContent = sheetContent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 160.dp,
    ) {
        body(it)
    }
}