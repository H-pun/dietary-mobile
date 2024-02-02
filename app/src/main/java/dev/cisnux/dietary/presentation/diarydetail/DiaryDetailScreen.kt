package dev.cisnux.dietary.presentation.diarydetail

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import dev.cisnux.dietary.R
import dev.cisnux.dietary.domain.models.Food
import dev.cisnux.dietary.domain.models.FoodDiaryDetail
import dev.cisnux.dietary.presentation.ui.components.ScannerResultBody
import dev.cisnux.dietary.presentation.ui.components.ScannerResultShimmer
import dev.cisnux.dietary.presentation.ui.theme.DietaryTheme
import dev.cisnux.dietary.utils.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryDetailScreen(
    modifier: Modifier = Modifier,
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
    val duplicateState by viewModel.duplicateState.collectAsState()
    val removeState by viewModel.removeState.collectAsState()
    var isDuplicateDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }
    val foodDiaryCategories = stringArrayResource(id = R.array.food_diary_category)
    var selectedFoodDiaryCategory by rememberSaveable {
        mutableStateOf(foodDiaryCategories[0])
    }
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

        duplicateState is UiState.Error -> (duplicateState as UiState.Error).error?.let { exception ->
            LaunchedEffect(snackbarHostState) {
                exception.message?.let {
                    val snackbarResult = snackbarHostState.showSnackbar(
                        message = it,
                        actionLabel = context.getString(R.string.retry),
                        withDismissAction = true,
                        duration = SnackbarDuration.Long
                    )
                    if (snackbarResult == SnackbarResult.ActionPerformed)
                        viewModel.duplicateDiaryById(foodDiaryCategory = selectedFoodDiaryCategory)
                }
            }
        }

        removeState is UiState.Success -> navigateUp()
        duplicateState is UiState.Success -> navigateUp()
    }

    DiaryDetailContent(
        snackbarHostState = snackbarHostState,
        body = {
            DiaryDetailBody(
                foodPicture = if (foodDiaryDetailState is UiState.Success)
                    (foodDiaryDetailState as UiState.Success<FoodDiaryDetail>).data?.foodPicture
                else null,
                onNavigateUp = navigateUp,
                onDuplicate = { isDuplicateDialogOpen = true },
                onRemove = viewModel::deleteFoodDiaryById,
                modifier = Modifier.padding(it),
                isRemoveVisible = foodDiaryDetailState is UiState.Success,
                isDuplicateVisible = foodDiaryDetailState is UiState.Success,
                isRemoveEnable = removeState !is UiState.Loading,
                isDuplicateEnable = duplicateState !is UiState.Loading,
            )

            DuplicateDialog(
                selectedFoodDiaryCategory = selectedFoodDiaryCategory,
                onFoodDiaryCategoryChange = { newValue -> selectedFoodDiaryCategory = newValue },
                foodDiaryCategories = foodDiaryCategories,
                onDone = {
                    isDuplicateDialogOpen = false
                    viewModel.duplicateDiaryById(foodDiaryCategory = selectedFoodDiaryCategory)
                },
                onCancel = { isDuplicateDialogOpen = false },
                isDialogOpen = isDuplicateDialogOpen,
                onDismissRequest = { isDuplicateDialogOpen = false },
            )
        },
        sheetContent = {
            AnimatedVisibility(foodDiaryDetailState is UiState.Success) {
                (foodDiaryDetailState as UiState.Success<FoodDiaryDetail>).data?.let { foodDiaryDetail ->
                    ScannerResultBody(
                        totalUserCaloriesToday = foodDiaryDetail.totalUserCaloriesToday,
                        userDailyBmiCalorie = foodDiaryDetail.userDailyBmiCalorie,
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
        userDailyBmiCalorie = 800.6798f,
        totalUserCaloriesToday = 500.7892f,
        status = "Kurang disarankan",
        feedback = "Terlalu banyak gula",
        foodPicture = "https://awsimages.detik.net.id/community/media/visual/2020/07/06/nasi-padang.jpeg?w=600&q=90",
        foods = listOf(
            Food(
                id = "1",
                foodName = "Nasi",
                calorie = 50f,
                protein = 8f,
                fat = 2f,
                carbohydrates = 4.3f,
                sugar = 8.7f,
            ),
            Food(
                id = "2",
                foodName = "Ayam Bakar",
                calorie = 50f,
                protein = 6f,
                fat = 10f,
                carbohydrates = 8.3f,
                sugar = null,
            ),
            Food(
                id = "3",
                foodName = "Tempe Goreng",
                calorie = 5.8f,
                protein = 9f,
                fat = 1f,
                carbohydrates = 8.3f,
                sugar = 0f,
            ),
            Food(
                id = "4",
                foodName = "Sayur Kangkung",
                calorie = 5.8f,
                protein = 9f,
                fat = 0.5f,
                carbohydrates = 8.3f,
                sugar = 0f,
            ),
        )
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberModalBottomSheetState()
    )

    DietaryTheme {
        DiaryDetailContent(
            snackbarHostState = SnackbarHostState(),
            body = {
                foodDiaryDetail.foodPicture?.let { foodPicture ->
                    DiaryDetailBody(
                        foodPicture = foodPicture,
                        onNavigateUp = { /*TODO*/ },
                        onDuplicate = { /*TODO*/ },
                        onRemove = { /*TODO*/ },
                        modifier = Modifier.padding(it)
                    )
                }
            },
            sheetContent = {
                ScannerResultBody(
                    totalUserCaloriesToday = foodDiaryDetail.totalUserCaloriesToday,
                    userDailyBmiCalorie = foodDiaryDetail.userDailyBmiCalorie,
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
    foodPicture: String?,
    onNavigateUp: () -> Unit,
    onDuplicate: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    isDuplicateEnable: Boolean = true,
    isRemoveEnable: Boolean = true,
    isDuplicateVisible: Boolean = true,
    isRemoveVisible: Boolean = true
) {
    Box(modifier = modifier) {
        AnimatedVisibility(
            visible = foodPicture == null, modifier = modifier
                .align(Alignment.Center)
                .fillMaxSize()
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
        }
        AnimatedVisibility(visible = foodPicture != null) {
            SubcomposeAsyncImage(
                model = foodPicture,
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
            )
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
            visible = isDuplicateVisible,
            modifier = modifier
                .padding(top = 12.dp, end = 64.dp)
                .align(Alignment.TopEnd)
        ) {
            TooltipBox(
                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                tooltip = {
                    PlainTooltip {
                        Text(text = "Tambahkan makanan")
                    }
                },
                state = rememberTooltipState(),
            ) {
                FilledIconButton(
                    onClick = onDuplicate,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.5f)
                    ),
                    enabled = isDuplicateEnable
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AddCircle,
                        contentDescription = null,
                        tint = Color.White,
                    )
                }
            }
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
                        Text(text = "Hapus\nmakanan", textAlign = TextAlign.Center)
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


@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
)
@Composable
private fun DuplicateDialogPreview() {
    val foodDiaryCategories = stringArrayResource(id = R.array.food_diary_category)
    var selectedFoodDiaryCategory by rememberSaveable {
        mutableStateOf(foodDiaryCategories[0])
    }

    DietaryTheme {
        DuplicateDialog(
            onCancel = { /*TODO*/ },
            onDone = { /*TODO*/ },
            onDismissRequest = { /*TODO*/ },
            isDialogOpen = true,
            foodDiaryCategories = foodDiaryCategories,
            onFoodDiaryCategoryChange = { newValue ->
                selectedFoodDiaryCategory = newValue
            },
            selectedFoodDiaryCategory = selectedFoodDiaryCategory
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DuplicateDialog(
    selectedFoodDiaryCategory: String,
    onFoodDiaryCategoryChange: (String) -> Unit,
    foodDiaryCategories: Array<String>,
    onCancel: () -> Unit,
    onDone: () -> Unit,
    onDismissRequest: () -> Unit,
    isDialogOpen: Boolean,
    modifier: Modifier = Modifier,
) {
    var isFoodDiaryCategoryExpanded by rememberSaveable { mutableStateOf(false) }

    if (isDialogOpen) Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.clip(MaterialTheme.shapes.extraLarge)
        ) {
            Column(
                modifier = modifier.padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Text(
                    text = "Tambahkan Makanan",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Pilih kategori makanan yang akan ditambahkan sarapan, makan siang\natau makan malam.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                ExposedDropdownMenuBox(
                    expanded = isFoodDiaryCategoryExpanded,
                    onExpandedChange = { isFoodDiaryCategoryExpanded = it },
                ) {
                    OutlinedTextField(
                        leadingIcon = {
                            Icon(
                                painter = painterResource(
                                    id = when (selectedFoodDiaryCategory) {
                                        foodDiaryCategories[0] -> R.drawable.ic_breakfast_24dp
                                        foodDiaryCategories[1] -> R.drawable.ic_lunch_24dp
                                        else -> R.drawable.ic_dinner_24dp
                                    }
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        value = selectedFoodDiaryCategory,
                        onValueChange = {},
                        label = {
                            Text(
                                text = "Category",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isFoodDiaryCategoryExpanded) },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                    )
                    ExposedDropdownMenu(
                        expanded = isFoodDiaryCategoryExpanded,
                        onDismissRequest = { isFoodDiaryCategoryExpanded = false },
                    ) {
                        foodDiaryCategories.forEach { selectedOption ->
                            DropdownMenuItem(
                                text = { Text(selectedOption) },
                                onClick = {
                                    onFoodDiaryCategoryChange(selectedOption)
                                    isFoodDiaryCategoryExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onCancel) {
                        Text(
                            text = "Batal",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = onDone) {
                        Text(
                            text = stringResource(id = R.string.add),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}
