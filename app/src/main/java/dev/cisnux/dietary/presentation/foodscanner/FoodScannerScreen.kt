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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
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
import dev.cisnux.dietary.presentation.ui.components.HealthProfile
import dev.cisnux.dietary.presentation.ui.components.HealthProfileDialog
import dev.cisnux.dietary.presentation.ui.components.UpdateHealthProfileDialog
import dev.cisnux.dietary.presentation.ui.theme.DietaryTheme

@Composable
fun FoodScannerScreen(
    onNavigateUp: () -> Unit,
    onGalleryButton: (launcher: ActivityResultLauncher<Intent>) -> Unit,
    onScannerResult: (foodPicture: String) -> Unit,
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
    var isUpdateHealthProfileDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }
    var isCapturedByCamera by rememberSaveable {
        mutableStateOf(false)
    }
    val genders = stringArrayResource(id = R.array.gender)
    var healthProfile by rememberSaveable {
        mutableStateOf(
            HealthProfile(
                age = "40",
                weight = "50",
                height = "170",
                gender = genders[0],
            )
        )
    }

    cameraFile?.let { file ->
        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        cameraController.takePicture(outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    onScannerResult(file.path)
                    viewModel.clearFileStates()
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.failed_to_took_picture), Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
    galleryFile?.let { file ->
        onScannerResult(file.path)
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
                modifier = modifier.padding(it)
            )
            HealthProfileDialog(
                onUpdate = {
                    isHealthProfileDialogOpen = false
                    isUpdateHealthProfileDialogOpen = true
                },
                onDone = {
                    isHealthProfileDialogOpen = false
                    if (!isCapturedByCamera) onGalleryButton(galleryLauncher)
                    else viewModel.createFile()
                },
                onDismissRequest = { isHealthProfileDialogOpen = false },
                isDialogOpen = isHealthProfileDialogOpen,
                age = "40",
                weight = "50",
                height = "170",
                gender = "Man",
            )
            UpdateHealthProfileDialog(
                onSave = { isUpdateHealthProfileDialogOpen = false },
                onCancel = { isUpdateHealthProfileDialogOpen = false },
                onDismissRequest = { isUpdateHealthProfileDialogOpen = false },
                isDialogOpen = isUpdateHealthProfileDialogOpen,
                age = healthProfile.age,
                weight = healthProfile.weight,
                height = healthProfile.height,
                selectedGender = healthProfile.gender,
                onAgeChange = { newValue -> healthProfile = healthProfile.copy(age = newValue) },
                onHeightChange = { newValue ->
                    healthProfile = healthProfile.copy(height = newValue)
                },
                onWeightChange = { newValue ->
                    healthProfile = healthProfile.copy(weight = newValue)
                },
                onGenderChange = { newValue ->
                    healthProfile = healthProfile.copy(gender = newValue)
                },
                genders = genders
            )
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun FoodScannerContentPreview() {
    val lottieComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(resId = R.raw.focusable_anim))

    DietaryTheme(darkTheme = false) {
        FoodScannerContent(body = {
            FoodScannerContent(body = {
                FoodScannerBody(
                    lottieComposition = lottieComposition,
                    onNavigateUp = { /*TODO*/ },
                    onCaptureByCamera = { /*TODO*/ },
                    onGalleryButton = { /*TODO*/ },
                    onRotateButton = { /*TODO*/ },
                    cameraPreview = {},
                    modifier = Modifier.padding(it)
                )
            })
        })
    }
}

@Composable
private fun FoodScannerContent(
    body: @Composable (innerPadding: PaddingValues) -> Unit, modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
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
) {
    ConstraintLayout(
        modifier = modifier.fillMaxSize()
    ) {
        val (buttonContainer, supportingTextContainer, supportingText, focusableAnim, backButton, galleryButton, rotateButton, takePictureButton) = createRefs()
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
                .height(58.dp)
                .width(165.dp)
                .clip(MaterialTheme.shapes.medium)
                .blur(radius = 2.dp)) {}
        Text(text = stringResource(id = R.string.camera_supporting_text),
            color = ComposeColor.White,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .constrainAs(supportingText) {
                    start.linkTo(supportingTextContainer.start)
                    end.linkTo(supportingTextContainer.end)
                    top.linkTo(supportingTextContainer.top)
                    bottom.linkTo(supportingTextContainer.bottom)
                }
                .width(150.dp))
        FilledIconButton(
            onClick = onNavigateUp,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = ComposeColor.Black.copy(
                    alpha = 0.54f
                )
            ),
            content = {
                Icon(
                    tint = ComposeColor.White,
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "back"
                )
            },
            modifier = Modifier
                .constrainAs(backButton) {
                    top.linkTo(parent.top, 8.dp)
                    start.linkTo(parent.start, 8.dp)
                }
        )
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
            modifier = Modifier
                .constrainAs(takePictureButton) {
                    top.linkTo(buttonContainer.top)
                    start.linkTo(buttonContainer.start)
                    end.linkTo(buttonContainer.end)
                    bottom.linkTo(buttonContainer.bottom)
                }
        ) {
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
