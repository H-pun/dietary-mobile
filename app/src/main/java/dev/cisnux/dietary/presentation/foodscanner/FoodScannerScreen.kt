package dev.cisnux.dietary.presentation.foodscanner

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import dev.cisnux.dietary.R
import dev.cisnux.dietary.domain.models.UserProfileDetail
import dev.cisnux.dietary.presentation.ui.components.HealthProfileDialog
import dev.cisnux.dietary.presentation.ui.theme.DietaryTheme
import dev.cisnux.dietary.utils.AppDestination
import dev.cisnux.dietary.utils.Failure
import dev.cisnux.dietary.utils.UiState
import java.io.File

@Composable
fun FoodScannerScreen(
    onNavigateUp: () -> Unit,
    navigateToMyProfile: () -> Unit,
    navigateToSignIn: (String) -> Unit,
    onGalleryButton: (launcher: ActivityResultLauncher<Intent>) -> Unit,
    onScannerResult: (foodPicture: File, title: String, foodDiaryCategory: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FoodScannerViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraController = remember {
        LifecycleCameraController(context)
    }
    val lottieComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(resId = R.raw.focusable_anim))
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
    var isHealthProfileDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    var isCapturedByCamera by rememberSaveable {
        mutableStateOf(false)
    }
    val userProfileState by viewModel.userProfileState.collectAsState(initial = UiState.Initialize)
    val onRefresh by rememberUpdatedState(viewModel::updateRefreshUserProfile)

    LaunchedEffect(Unit) {
        onRefresh(true)
    }

    if (userProfileState is UiState.Error) {
        (userProfileState as UiState.Error).error?.let { exception ->
            LaunchedEffect(snackbarHostState) {
                exception.message?.let {
                    val snackbarResult = snackbarHostState.showSnackbar(
                        message = it,
                        actionLabel = context.getString(R.string.retry),
                        withDismissAction = true,
                        duration = SnackbarDuration.Long
                    )
                    if (snackbarResult == SnackbarResult.ActionPerformed) viewModel.updateRefreshUserProfile(
                        true
                    )
                }
            }
            if (exception is Failure.UnauthorizedFailure) {
                viewModel.signOut()
                navigateToSignIn(AppDestination.FoodScannerRoute.route)
            }
        }
    }

    val userProfileDetail by viewModel.userProfileDetail.collectAsState(
        initial = UserProfileDetail(
            username = "",
            emailAddress = "",
            age = 0,
            weight = 0f,
            height = 0f,
            gender = "",
            goal = "",
            weightTarget = 0f,
            activityLevel = "",
        )
    )

    cameraFile?.let { file ->
        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        cameraController.takePicture(outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    onScannerResult(file, viewModel.title, viewModel.foodDiaryCategory)
                    viewModel.clearFileStates()
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
        onScannerResult(file, viewModel.title, viewModel.foodDiaryCategory)
        viewModel.clearFileStates()
    }

    FoodScannerContent(
        body = {
            FoodScannerBody(
                lottieComposition = lottieComposition,
                onNavigateUp = onNavigateUp,
                onCaptureByCamera = {
                    isHealthProfileDialogOpen = true
                    isCapturedByCamera = true
                },
                onGalleryButton = {
                    isHealthProfileDialogOpen = true
                    isCapturedByCamera = false
                },
                onRotateButton = { isBackCamera = !isBackCamera },
                cameraPreview = {
                    AndroidView(factory = { context ->
                        cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                        cameraController.isTapToFocusEnabled
                        cameraController.setEnabledUseCases(CameraController.IMAGE_CAPTURE)
                        cameraController.bindToLifecycle(lifecycleOwner)
                        PreviewView(context).apply {
                            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                            setBackgroundColor(Color.BLACK)
                            scaleType = PreviewView.ScaleType.FILL_START
                            controller = cameraController
                        }
                    }, update = { preview ->
                        preview.controller?.cameraSelector =
                            if (isBackCamera) CameraSelector.DEFAULT_BACK_CAMERA
                            else CameraSelector.DEFAULT_FRONT_CAMERA
                    })
                },
                modifier = modifier.padding(it),
                isLoading = (userProfileState is UiState.Loading ||
                        userProfileState is UiState.Error && (userProfileState as UiState.Error).error !is Failure.ConnectionFailure)
                        || userProfileDetail.username.isBlank()
            )
            HealthProfileDialog(
                onUpdate = {
                    isHealthProfileDialogOpen = false
                    navigateToMyProfile()
                },
                onDone = {
                    isHealthProfileDialogOpen = false
                    if (!isCapturedByCamera) onGalleryButton(galleryLauncher)
                    else viewModel.createFile()
                },
                onDismissRequest = { isHealthProfileDialogOpen = false },
                isDialogOpen = isHealthProfileDialogOpen,
                age = userProfileDetail.age.toString(),
                weight = userProfileDetail.weight.toString(),
                height = userProfileDetail.height.toString(),
                gender = userProfileDetail.gender,
            )
        },
        snackbarHostState = snackbarHostState,
    )
}

@Preview(showBackground = true)
@Composable
private fun FoodScannerContentPreview() {
    val lottieComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(resId = R.raw.focusable_anim))
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    DietaryTheme(darkTheme = false) {
        FoodScannerContent(
            body = {
                FoodScannerBody(
                    lottieComposition = lottieComposition,
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
    modifier: Modifier = Modifier
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
    lottieComposition: LottieComposition?,
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
        val (buttonContainer, supportingTextContainer, supportingText, focusableAnim, backButton, galleryButton, rotateButton, takePictureButton) = createRefs()
        AnimatedVisibility(isLoading) {
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(50.dp))
                }
            }
        }
        if (!isLoading) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                content = cameraPreview,
                color = ComposeColor.Black
            )
            LottieAnimation(composition = lottieComposition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier
                    .constrainAs(focusableAnim) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
                    .height(220.dp)
                    .width(205.dp))
            Surface(color = ComposeColor.Black.copy(alpha = 0.4f),
                modifier = Modifier
                    .constrainAs(supportingTextContainer) {
                        start.linkTo(focusableAnim.start)
                        end.linkTo(focusableAnim.end)
                        top.linkTo(focusableAnim.bottom)
                    }
                    .height(80.dp)
                    .width(180.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .blur(radius = 2.dp)) {}
            Text(text = stringResource(id = R.string.camera_supporting_text),
                color = ComposeColor.White,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .constrainAs(supportingText) {
                        start.linkTo(supportingTextContainer.start)
                        end.linkTo(supportingTextContainer.end)
                        top.linkTo(supportingTextContainer.top)
                        bottom.linkTo(supportingTextContainer.bottom)
                    }
                    .width(170.dp))
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
        if (!isLoading) {
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
            IconButton(
                onClick = onCaptureByCamera,
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
}
