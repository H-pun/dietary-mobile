package org.cisnux.mydietary.presentation.diarydetail

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SheetValue
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import org.cisnux.mydietary.R
import org.cisnux.mydietary.domain.models.FoodDiaryDetail
import org.cisnux.mydietary.domain.models.Food
import org.cisnux.mydietary.domain.models.FoodNutrition
import org.cisnux.mydietary.domain.models.UserNutrition
import org.cisnux.mydietary.presentation.ui.components.AddDiaryDialog
import org.cisnux.mydietary.presentation.ui.components.AddedDietaryBody
import org.cisnux.mydietary.presentation.ui.components.AddedDiaryShimmer
import org.cisnux.mydietary.presentation.ui.theme.DietaryTheme
import org.cisnux.mydietary.presentation.ui.theme.surfaceDark
import org.cisnux.mydietary.utils.AppDestination
import org.cisnux.mydietary.utils.Failure
import org.cisnux.mydietary.utils.UiState
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryDetailScreen(
    modifier: Modifier = Modifier,
    navigateToSignIn: (String) -> Unit,
    navigateUp: () -> Unit,
    viewModel: DiaryDetailViewModel = hiltViewModel()
) {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberModalBottomSheetState(
            confirmValueChange = {
                it != SheetValue.Hidden
            }
        )
    )
    LaunchedEffect(Unit) {
        scaffoldState.bottomSheetState.partialExpand()
        scaffoldState.bottomSheetState.hide()
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val foodDiaryDetailState by viewModel.foodDiaryDetailState.collectAsState()
    val userDailyNutritionState by viewModel.userDailyNutritionState.collectAsState(UiState.Initialize)
    val addFoodDiaryState by viewModel.addFoodDiaryState.collectAsState()
    val removeState by viewModel.removeState.collectAsState()
    val context = LocalContext.current

    BackHandler {
        navigateUp()
    }

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
            if (exception is Failure.UnauthorizedFailure) navigateToSignIn(AppDestination.DiaryDetailRoute.route)
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
                    if (snackbarResult == SnackbarResult.ActionPerformed) viewModel.deleteFoodDiaryById()
                }
            }
        }

        addFoodDiaryState is UiState.Error -> (addFoodDiaryState as UiState.Error).error?.let { exception ->
            LaunchedEffect(snackbarHostState) {
                exception.message?.let {
                    snackbarHostState.showSnackbar(
                        message = it, withDismissAction = true, duration = SnackbarDuration.Short
                    )
                }
            }
        }

        removeState is UiState.Success -> navigateUp()

        addFoodDiaryState is UiState.Success -> navigateUp()
    }


    val foodDiaryCategories = stringArrayResource(id = R.array.food_diary_category)
    var title by rememberSaveable {
        mutableStateOf("")
    }
    var selectedFoodDiaryCategory by rememberSaveable {
        mutableStateOf(foodDiaryCategories[0])
    }
    var isAddDiaryDialogOpen by remember {
        mutableStateOf(false)
    }

    DiaryDetailContent(
        snackbarHostState = snackbarHostState, body = {
            DiaryDetailBody(
                foodPictures = if (foodDiaryDetailState is UiState.Success) (foodDiaryDetailState as UiState.Success<FoodDiaryDetail>).data?.foodNutrition?.image
                else null,
                onNavigateUp = navigateUp,
                title = if (foodDiaryDetailState is UiState.Success) (foodDiaryDetailState as UiState.Success<FoodDiaryDetail>).data?.title
                    ?: ""
                else "",
                onRemove = viewModel::deleteFoodDiaryById,
                isSuccess = foodDiaryDetailState is UiState.Success,
                isRemoveEnable = removeState !is UiState.Loading,
            )
        },
        sheetContent = {
            if (foodDiaryDetailState is UiState.Success && userDailyNutritionState is UiState.Success) {
                val foodDiaryDetail =
                    (foodDiaryDetailState as UiState.Success<FoodDiaryDetail>).data!!
                val userDailyNutrition =
                    (userDailyNutritionState as UiState.Success<UserNutrition>).data!!

                AddedDietaryBody(
                    totalCaloriesToday = userDailyNutrition.totalCaloriesToday,
                    totalFatToday = userDailyNutrition.totalFatToday,
                    totalProteinToday = userDailyNutrition.totalProteinToday,
                    totalCarbohydrateToday = userDailyNutrition.totalCarbohydrateToday,
                    maxDailyProtein = userDailyNutrition.maxDailyProtein,
                    maxDailyFat = userDailyNutrition.maxDailyFat,
                    maxDailyCarbohydrate = userDailyNutrition.maxDailyCarbohydrate,
                    maxDailyCalories = userDailyNutrition.maxDailyCalories,
                    foods = foodDiaryDetail.foodNutrition.foods,
                    bottomContent = {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { isAddDiaryDialogOpen = true },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = addFoodDiaryState !is UiState.Loading
                        ) {
                            if (addFoodDiaryState is UiState.Loading) CircularProgressIndicator()
                            else Text(text = "Tambahkan Ulang")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    },
                    feedback = foodDiaryDetail.feedback,
                    totalFoodCalories = foodDiaryDetail.foodNutrition.totalCalories,
                    totalFoodFat = foodDiaryDetail.foodNutrition.totalFat,
                    totalFoodProtein = foodDiaryDetail.foodNutrition.totalProtein,
                    totalFoodCarbohydrate = foodDiaryDetail.foodNutrition.totalCarbohydrate
                )

                AddDiaryDialog(
                    onCancel = {
                        title = ""
                        isAddDiaryDialogOpen = false
                    },
                    isDialogOpen = isAddDiaryDialogOpen,
                    onDismissRequest = {
                        title = ""
                        isAddDiaryDialogOpen = false
                    },
                    title = title,
                    selectedFoodDiaryCategory = selectedFoodDiaryCategory,
                    onTitleChange = { newValue -> title = newValue },
                    onFoodDiaryCategoryChange = { newValue ->
                        selectedFoodDiaryCategory = newValue
                    },
                    foodDiaryCategories = foodDiaryCategories,
                    onSave = {
                        isAddDiaryDialogOpen = false
                        viewModel.addFoodDiary(
                            title = title,
                            category = selectedFoodDiaryCategory,
                            foodPicture = foodDiaryDetail.foodNutrition.image as File,
                            totalProtein = foodDiaryDetail.foodNutrition.totalProtein,
                            totalCarbohydrate = foodDiaryDetail.foodNutrition.totalCarbohydrate,
                            totalFat = foodDiaryDetail.foodNutrition.totalFat,
                            totalCalories = foodDiaryDetail.foodNutrition.totalCalories,
                            foodIds = foodDiaryDetail.foodNutrition.foods.map { food -> food.id },
                            feedback = foodDiaryDetail.feedback
                        )
                    },
                )
            }
            AnimatedVisibility(visible = foodDiaryDetailState is UiState.Loading || foodDiaryDetailState is UiState.Error || userDailyNutritionState is UiState.Loading || userDailyNutritionState is UiState.Error) {
                AddedDiaryShimmer()
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
        id = "1",
        title = "Warteg Bahari Bahari",
        status = "Kurang disarankan",
        feedback = listOf(
            "Bagian gosong pada makanan yang dibakar mengandung karsinogenik (senyawa yang berpotensi menyebabkan kanker), jangan terlalu sering mengkonsumsi makanan yang diolah dengan cara dibakar",
            "Bagian gosong pada makanan yang dibakar mengandung karsinogenik (senyawa yang berpotensi menyebabkan kanker), jangan terlalu sering mengkonsumsi makanan yang diolah dengan cara dibakar",
            "Bagian gosong pada makanan yang dibakar mengandung karsinogenik (senyawa yang berpotensi menyebabkan kanker), jangan terlalu sering mengkonsumsi makanan yang diolah dengan cara dibakar",
        ),
        foodNutrition = FoodNutrition(
            image = null,
            totalProtein = 2000f,
            foods = listOf(
                Food(
                    id = "1",
                    name = "Nasi Putih",
                    calories = 8.3f,
                    fat = 8.3f,
                    protein = 8.3f,
                    carbohydrates = 8.3f,
                    sugar = null,
                    feedback = listOf(
                        "Bagian gosong pada makanan yang dibakar mengandung karsinogenik (senyawa yang berpotensi menyebabkan kanker), jangan terlalu sering mengkonsumsi makanan yang diolah dengan cara dibakar",
                        "Bagian gosong pada makanan yang dibakar mengandung karsinogenik (senyawa yang berpotensi menyebabkan kanker), jangan terlalu sering mengkonsumsi makanan yang diolah dengan cara dibakar",
                        "Bagian gosong pada makanan yang dibakar mengandung karsinogenik (senyawa yang berpotensi menyebabkan kanker), jangan terlalu sering mengkonsumsi makanan yang diolah dengan cara dibakar",
                    )
                ),
                Food(
                    id = "2",
                    name = "Ayam Goreng",
                    calories = 8.3f,
                    fat = 8.3f,
                    protein = 8.3f,
                    carbohydrates = 8.3f,
                    sugar = null,
                ),
            ),
            totalFat = 2000f,
            totalCarbohydrate = 2000f,
            totalCalories = 2000f,
        ),
    )
    val userDailyNutrition = UserNutrition(
        totalCaloriesToday = 2000f,
        totalFatToday = 2000f,
        totalProteinToday = 2000f,
        totalCarbohydrateToday = 2000f,
        maxDailyCalories = 2000f,
        maxDailyFat = 2000f,
        maxDailyCarbohydrate = 2000f,
        maxDailyProtein = 2000f,
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberModalBottomSheetState()
    )

    DietaryTheme {
        DiaryDetailContent(snackbarHostState = SnackbarHostState(), body = {
            DiaryDetailBody(
                title = foodDiaryDetail.title,
                foodPictures = foodDiaryDetail.foodNutrition.image,
                onNavigateUp = { /*TODO*/ },
                onRemove = { /*TODO*/ },
            )
        }, sheetContent = {
            AddedDietaryBody(
                totalCaloriesToday = userDailyNutrition.totalCaloriesToday,
                totalFatToday = userDailyNutrition.totalFatToday,
                totalProteinToday = userDailyNutrition.totalProteinToday,
                totalCarbohydrateToday = userDailyNutrition.totalCarbohydrateToday,
                maxDailyProtein = userDailyNutrition.maxDailyCalories,
                maxDailyFat = userDailyNutrition.maxDailyFat,
                maxDailyCarbohydrate = userDailyNutrition.maxDailyCarbohydrate,
                maxDailyCalories = userDailyNutrition.maxDailyCalories,
                foods = foodDiaryDetail.foodNutrition.foods,
                feedback = foodDiaryDetail.feedback,
                totalFoodCalories = foodDiaryDetail.foodNutrition.totalCalories,
                totalFoodFat = foodDiaryDetail.foodNutrition.totalFat,
                totalFoodProtein = foodDiaryDetail.foodNutrition.totalProtein,
                totalFoodCarbohydrate = foodDiaryDetail.foodNutrition.totalCarbohydrate
            )
        }, bottomSheetScaffoldState = scaffoldState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryDetailBody(
    foodPictures: Any?,
    onNavigateUp: () -> Unit,
    onRemove: () -> Unit,
    isRemoveEnable: Boolean = true,
    isSuccess: Boolean = true,
    title: String = "",
) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = foodPictures == null,
            modifier = Modifier
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
                modifier = Modifier
                    .padding(bottom = 270.dp)
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
                    .fillMaxSize(),
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context).data(
                        foodPictures
                    ).build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.TopCenter)
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
            visible = isSuccess,
            modifier = Modifier
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
                    onClick = onRemove, colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.5f)
                    ), enabled = isRemoveEnable
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_outline_cancel_24dp),
                        contentDescription = null,
                        tint = Color.White,
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = isSuccess,
            modifier = Modifier
                .padding(top = 22.dp, start = 80.dp)
                .align(Alignment.TopStart)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.width(230.dp)
            )
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
        sheetPeekHeight = 370.dp,
        containerColor = surfaceDark
    ) {
        body(it)
    }
}