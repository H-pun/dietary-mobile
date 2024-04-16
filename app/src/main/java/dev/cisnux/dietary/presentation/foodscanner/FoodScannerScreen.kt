package dev.cisnux.dietary.presentation.foodscanner

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
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
import dev.cisnux.dietary.utils.afterMeasured
import java.io.File
import java.util.concurrent.TimeUnit

@SuppressLint("ClickableViewAccessibility")
@Composable
fun FoodScannerScreen(
    onNavigateUp: () -> Unit,
    navigateToSignIn: (String) -> Unit,
    onGalleryButton: (launcher: ActivityResultLauncher<Intent>) -> Unit,
    navigateToAddedDietary: (String, String, File) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FoodScannerViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            isTapToFocusEnabled = true
            isPinchToZoomEnabled = true
            bindToLifecycle(lifecycleOwner)
        }
    }
    var isBackCamera by rememberSaveable {
        mutableStateOf(true)
    }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                viewModel.fileFromUri(image = uri)
            }
        }
    }
    val cameraFile by viewModel.cameraFile.collectAsState()
    val galleryFile by viewModel.galleryFile.collectAsState()
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val currentFileState by viewModel.currentFileState.collectAsState()
    val foodDiaryCategories = stringArrayResource(id = R.array.food_diary_category)
    var title by rememberSaveable {
        mutableStateOf("")
    }
    var selectedFoodDiaryCategory by rememberSaveable {
        mutableStateOf(foodDiaryCategories[0])
    }
    var isPredictedResultDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }
    var isQuestionDialogOpen by remember {
        mutableStateOf(false)
    }
    var isAddDiaryDialogOpen by remember {
        mutableStateOf(true)
    }
    val predictedResultState by viewModel.predictedResultState.collectAsState()
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

    val isQuestionDialogEnable by remember(answeredQuestions) {
        derivedStateOf {
            answeredQuestions.any { foodQuestion ->
                foodQuestion.any { answeredQuestion ->
                    answeredQuestion.answer.isNotBlank()
                }
            }
        }
    }

    when (predictedResultState) {
        is UiState.Error -> {
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
                    navigateToSignIn(AppDestination.FoodScannerRoute.route)
                }
            }
        }

        is UiState.Success -> {
            isPredictedResultDialogOpen = true
        }

        else -> {}
    }

    BackHandler {
        onNavigateUp()
    }

    cameraFile?.let { file ->
        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        cameraController.takePicture(outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    viewModel.predictFoods(file)
                    viewModel.clearCameraStates()
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.failed_to_took_picture),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
    galleryFile?.let { file ->
        viewModel.predictFoods(file)
        viewModel.clearCameraStates()
    }

    FoodScannerContent(
        body = {
            FoodScannerBody(
                onNavigateUp = onNavigateUp,
                onCaptureByCamera = { viewModel.createFile() },
                onGalleryButton = { onGalleryButton(galleryLauncher) },
                onRotateButton = { isBackCamera = !isBackCamera },
                isLoading = predictedResultState is UiState.Loading,
                cameraPreview = {
                    AndroidView(
                        factory = { context ->
                            PreviewView(context).apply {
                                layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                                setBackgroundColor(Color.BLACK)
                                scaleType = PreviewView.ScaleType.FILL_START
                                controller = cameraController
                                afterMeasured {
                                    setOnTouchListener { _, event ->
                                        return@setOnTouchListener when (event.action) {
                                            MotionEvent.ACTION_DOWN -> true
                                            MotionEvent.ACTION_UP -> {
                                                try {
                                                    val autoFocusPoint =
                                                        SurfaceOrientedMeteringPointFactory(1f, 1f)
                                                            .createPoint(.5f, .5f)
                                                    val autoFocusAction =
                                                        FocusMeteringAction.Builder(
                                                            autoFocusPoint,
                                                            FocusMeteringAction.FLAG_AF
                                                        ).apply {
                                                            setAutoCancelDuration(
                                                                500L,
                                                                TimeUnit.MILLISECONDS
                                                            )
                                                        }.build()
                                                    cameraController.cameraControl?.startFocusAndMetering(
                                                        autoFocusAction
                                                    )
                                                } catch (e: CameraInfoUnavailableException) {
                                                    Log.d(
                                                        "FoodScannerScreen",
                                                        "couldn't access the camera",
                                                        e
                                                    )
                                                }
                                                true
                                            }

                                            else -> false
                                        }
                                    }
                                }
                            }
                        },
                        update = { preview ->
                            preview.controller?.cameraSelector =
                                if (isBackCamera) CameraSelector.DEFAULT_BACK_CAMERA
                                else CameraSelector.DEFAULT_FRONT_CAMERA
                        },
                    )
                },
                modifier = modifier.padding(it),
            )

            AddDiaryDialog(
                onCancel = {
                    onNavigateUp()
                    title = ""
                },
                isDialogOpen = isAddDiaryDialogOpen,
                onDismissRequest = {
                    onNavigateUp()
                    title = ""
                },
                title = title,
                selectedFoodDiaryCategory = selectedFoodDiaryCategory,
                onTitleChange = { newValue -> title = newValue },
                onFoodDiaryCategoryChange = { newValue -> selectedFoodDiaryCategory = newValue },
                foodDiaryCategories = foodDiaryCategories,
                onSave = { isAddDiaryDialogOpen = false },
            )

            PredictedResultDialog(
                onCancel = {
                    isPredictedResultDialogOpen = false
                    viewModel.resetPredictedStates()
                },
                isDialogOpen = isPredictedResultDialogOpen,
                onDismissRequest = { isPredictedResultDialogOpen = false },
                foodPictures = currentFileState,
                onQuestionDialog = {
                    isQuestionDialogOpen = foods.any { predictedFood ->
                        predictedFood.questions.isNotEmpty()
                    }
                    if (!isQuestionDialogOpen) {
                        currentFileState?.let { currentFileState ->
                            navigateToAddedDietary(
                                title,
                                selectedFoodDiaryCategory,
                                currentFileState
                            )
                        }
                    }
                },
                modifier = Modifier.padding(it),
                foods = foods,
                snackbarHostState = snackbarHostState,
            )

            QuestionDialog(
                onSave = {
                    isQuestionDialogOpen = false
                    currentFileState?.let { currentFileState ->
                        navigateToAddedDietary(
                            title,
                            selectedFoodDiaryCategory,
                            currentFileState
                        )
                    }
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
                isEnable = isQuestionDialogEnable,
                answeredQuestions = answeredQuestions,
                onAnswerChange = { newAnswer, foodIndex, answerIndex ->
                    answeredQuestions[foodIndex][answerIndex] =
                        answeredQuestions[foodIndex][answerIndex].copy(answer = newAnswer)
                },
            )
        },
        snackbarHostState = snackbarHostState,
    )
}

@Preview(showBackground = true)
@Composable
private fun FoodScannerContentPreview() {
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    DietaryTheme(darkTheme = false) {
        FoodScannerContent(
            body = {
                FoodScannerBody(
                    onNavigateUp = { /*TODO*/ },
                    onCaptureByCamera = { /*TODO*/ },
                    onGalleryButton = { /*TODO*/ },
                    onRotateButton = { /*TODO*/ },
                    cameraPreview = {},
                    modifier = Modifier.padding(it),
                )
            },
            snackbarHostState = snackbarHostState,
        )
    }
}

@Composable
private fun FoodScannerContent(
    body: @Composable (PaddingValues) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = modifier,
    ) { innerPadding ->
        body(innerPadding)
    }
}

@Composable
private fun FoodScannerBody(
    onNavigateUp: () -> Unit,
    onCaptureByCamera: () -> Unit,
    onGalleryButton: () -> Unit,
    onRotateButton: () -> Unit,
    cameraPreview: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    ConstraintLayout(
        modifier = modifier.fillMaxSize()
    ) {
        val (buttonContainer, supportingTextContainer, supportingText, focusableAnim, backButton, galleryButton, rotateButton, takePictureButton, progressIndicator) = createRefs()
        Surface(
            modifier = Modifier.fillMaxSize(),
            content = cameraPreview,
            color = ComposeColor.Black
        )
        Surface(
            color = ComposeColor.Transparent,
            modifier = Modifier
                .constrainAs(focusableAnim) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .size(
                    height = 250.dp,
                    width = 250.dp
                )
                .border(1.5.dp, ComposeColor.White, CircleShape)
                .clip(CircleShape)
        ) {}
        Surface(color = ComposeColor.Black.copy(alpha = 0.4f),
            modifier = Modifier
                .constrainAs(supportingTextContainer) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(focusableAnim.bottom, margin = 8.dp)
                }
                .height(60.dp)
                .width(180.dp)
                .clip(MaterialTheme.shapes.medium)
                .blur(radius = 2.dp)) {}
        Text(text = stringResource(id = R.string.camera_supporting_text),
            color = ComposeColor.White,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .constrainAs(supportingText) {
                    start.linkTo(supportingTextContainer.start)
                    end.linkTo(supportingTextContainer.end)
                    top.linkTo(supportingTextContainer.top)
                    bottom.linkTo(supportingTextContainer.bottom)
                }
                .width(170.dp))
        AnimatedVisibility(visible = isLoading, modifier = Modifier.constrainAs(progressIndicator) {
            start.linkTo(supportingTextContainer.start)
            end.linkTo(supportingTextContainer.end)
            top.linkTo(supportingTextContainer.bottom, margin = 8.dp)
        }) {
            LinearProgressIndicator(
                color = ComposeColor.White,
                trackColor = ComposeColor.Black.copy(alpha = 0.4f),
                modifier = Modifier
                    .width(180.dp)
            )
        }
        FilledIconButton(onClick = onNavigateUp, colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = ComposeColor.Black.copy(
                alpha = 0.54f
            )
        ), content = {
            Icon(
                tint = ComposeColor.White,
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "back"
            )
        }, modifier = Modifier.constrainAs(backButton) {
            top.linkTo(parent.top, 8.dp)
            start.linkTo(parent.start, 8.dp)
        })
        Surface(color = ComposeColor.Black.copy(alpha = 0.45f),
            modifier = Modifier
                .constrainAs(buttonContainer) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .fillMaxWidth()
                .height(120.dp)
                .blur(radius = 2.dp)) {}
        TextButton(onClick = onGalleryButton, modifier = Modifier.constrainAs(galleryButton) {
            top.linkTo(buttonContainer.top)
            start.linkTo(buttonContainer.start)
            end.linkTo(takePictureButton.start)
            bottom.linkTo(buttonContainer.bottom)
        }) {
            Text(
                text = stringResource(id = R.string.gallery_button),
                color = ComposeColor.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
        IconButton(onClick = onCaptureByCamera,
            modifier = Modifier.constrainAs(takePictureButton) {
                top.linkTo(buttonContainer.top)
                start.linkTo(buttonContainer.start)
                end.linkTo(buttonContainer.end)
                bottom.linkTo(buttonContainer.bottom)
            }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_photo_camera_24dp),
                contentDescription = stringResource(id = R.string.food_scanner_title),
                tint = ComposeColor.White,
            )
        }
        TextButton(onClick = onRotateButton, modifier = Modifier.constrainAs(rotateButton) {
            top.linkTo(buttonContainer.top)
            end.linkTo(buttonContainer.end)
            start.linkTo(takePictureButton.end)
            bottom.linkTo(buttonContainer.bottom)
        }) {
            Text(
                text = stringResource(id = R.string.rotate_button),
                color = ComposeColor.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
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

@Preview
@Composable
private fun PredictedResultDialogPreview() {
    DietaryTheme {
        PredictedResultDialog(
            onCancel = {},
            isDialogOpen = true,
            onDismissRequest = { },
            foodPictures = null,
            onQuestionDialog = { },
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
            snackbarHostState = SnackbarHostState()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddDiaryDialog(
    onCancel: () -> Unit,
    isDialogOpen: Boolean,
    onDismissRequest: () -> Unit,
    title: String,
    selectedFoodDiaryCategory: String,
    onTitleChange: (String) -> Unit,
    onFoodDiaryCategoryChange: (String) -> Unit,
    foodDiaryCategories: Array<String>,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isTitleFocused by remember {
        mutableStateOf(false)
    }
    if (isDialogOpen)
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
            ),
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Food Diary",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.ExtraBold
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onCancel) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = onSave,
                    ) {
                        Icon(imageVector = Icons.Rounded.AddCircle, contentDescription = null)
                    }
                },
                modifier = modifier,
            ) {
                var isFoodDiaryCategoryExpanded by rememberSaveable { mutableStateOf(false) }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .padding(it)
                        .padding(horizontal = 16.dp)
                        .fillMaxSize()
                ) {
                    OutlinedTextField(
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_fastfood_24dp),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        value = title,
                        singleLine = true,
                        onValueChange = onTitleChange,
                        placeholder = {
                            Text(
                                text = stringResource(R.string.title_placeholder),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(R.string.title),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        supportingText = {
                            if (title.isNotEmpty() and title.isBlank())
                                Text(
                                    text = stringResource(R.string.title_error_text),
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            else if (isTitleFocused)
                                Text(
                                    text = stringResource(R.string.supporting_text_required),
                                    style = MaterialTheme.typography.bodySmall,
                                )
                        },
                        isError = title.isNotEmpty() and title.isBlank(),
                        trailingIcon = {
                            if (title.isNotEmpty() and title.isBlank())
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_round_error_24dp),
                                    contentDescription = null,
                                )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { newValue ->
                                isTitleFocused = newValue.hasFocus
                            },
                    )
                    ExposedDropdownMenuBox(
                        expanded = isFoodDiaryCategoryExpanded,
                        onExpandedChange = { newValue ->
                            isFoodDiaryCategoryExpanded = newValue
                        },
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
                }
            }
        }
}

@Composable
private fun PredictedResultDialog(
    onCancel: () -> Unit,
    isDialogOpen: Boolean,
    onDismissRequest: () -> Unit,
    foodPictures: File?,
    onQuestionDialog: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    isAddDietaryFabVisible: Boolean = false,
    onAddDiaryFab: () -> Unit = {},
    foods: List<PredictedFood> = listOf(),
) {
    if (isDialogOpen)
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
            ),
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

                        val state =
                            rememberTransformableState { zoomChange, offsetChange, rotationChange ->
                                scale *= zoomChange
                                rotation += rotationChange
                                offset += offsetChange
                            }

                        Box(
                            Modifier
                                .padding(it)
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
                                                color = ComposeColor.White,
                                                size = measuredText.size
                                                    .toSize()
                                                    .copy(
                                                        width = measuredText.size.toSize().width + 8,
                                                        height = measuredText.size.toSize().height + 8
                                                    )
                                            )
                                            drawText(
                                                measuredText,
                                                topLeft = foodSize.toRect().topLeft.copy(
                                                    x = it.bound.x.toFloat() + 4,
                                                    y = it.bound.y.toFloat() - 36f
                                                )
                                            )
                                            drawRoundRect(
                                                topLeft = foodSize.toRect().topLeft.copy(
                                                    x = it.bound.x.toFloat(),
                                                    y = it.bound.y.toFloat()
                                                ),
                                                color = ComposeColor.White,
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
                    FilledTonalButton(
                        onClick = onCancel,
                        modifier = Modifier
                            .padding(bottom = 12.dp, start = 12.dp)
                            .align(Alignment.BottomStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        VerticalDivider(
                            thickness = 1.5.dp,
                            modifier = Modifier.height(20.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Retake")
                    }
                    FilledTonalButton(
                        onClick = onQuestionDialog,
                        modifier = Modifier
                            .padding(bottom = 12.dp, end = 12.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        VerticalDivider(
                            thickness = 1.5.dp,
                            modifier = Modifier.height(20.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Lanjutkan")
                    }
                }
            }
        }
}