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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import dev.cisnux.dietary.R
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
    var isHealthProfileDialogOpen by remember {
        mutableStateOf(false)
    }
    var isUpdateHealthProfileDialogOpen by remember {
        mutableStateOf(false)
    }
    var isCapturedByCamera by rememberSaveable {
        mutableStateOf(false)
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
                        context, "failed to took picture", Toast.LENGTH_SHORT
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
                isDialogOpen = isHealthProfileDialogOpen
            )
            UpdateHealthProfileDialog(
                onSave = { isUpdateHealthProfileDialogOpen = false },
                onCancel = { isUpdateHealthProfileDialogOpen = false },
                isDialogOpen = isUpdateHealthProfileDialogOpen,
                onDismissRequest = { isUpdateHealthProfileDialogOpen = false }
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
            color = ComposeColor.White
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

@Preview(showBackground = true)
@Composable
private fun UpdateHealthProfileDialogPreview() {
    DietaryTheme {
        UpdateHealthProfileDialog(
            onSave = { /*TODO*/ },
            onCancel = { /*TODO*/ },
            onDismissRequest = {},
            isDialogOpen = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateHealthProfileDialog(
    onSave: () -> Unit,
    onCancel: () -> Unit,
    isDialogOpen: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val genders = stringArrayResource(id = R.array.gender)
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(genders[0]) }

    if (isDialogOpen)
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
            ),
        ) {
            Surface(color = MaterialTheme.colorScheme.surface, modifier = modifier.fillMaxSize()) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onCancel) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    contentDescription = "close dialog"
                                )
                            }
                            Text(
                                text = stringResource(id = R.string.health_profile_dialog_title),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        TextButton(onClick = onSave) {
                            Text(
                                text = stringResource(id = R.string.save),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        OutlinedTextField(
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_age_100dp),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            value = stringResource(id = R.string.dummy_age),
                            onValueChange = {},
                            label = {
                                Text(
                                    text = stringResource(id = R.string.age_label),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_height_100dp),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            value = stringResource(id = R.string.dummy_height),
                            onValueChange = {},
                            label = {
                                Text(
                                    text = stringResource(id = R.string.height_label),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_scale_100dp),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            value = stringResource(id = R.string.dummy_weight),
                            onValueChange = {},
                            label = {
                                Text(
                                    text = stringResource(id = R.string.weight_label),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = it },
                        ) {
                            OutlinedTextField(
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(
                                            id = if (selectedOptionText == genders[0]) R.drawable.ic_male_100dp
                                            else R.drawable.ic_female_100dp
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                value = selectedOptionText,
                                onValueChange = {},
                                label = {
                                    Text(
                                        text = stringResource(id = R.string.gender_label),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                            ) {
                                genders.forEach { selectionOption ->
                                    DropdownMenuItem(
                                        text = { Text(selectionOption) },
                                        onClick = {
                                            selectedOptionText = selectionOption
                                            expanded = false
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
}

@Preview
@Composable
private fun HealthProfileDialogPreview() {
    DietaryTheme {
        HealthProfileDialog(isDialogOpen = true, onUpdate = {}, onDone = {}, onDismissRequest = {})
    }
}

@Composable
private fun HealthProfileDialog(
    onUpdate: () -> Unit,
    onDone: () -> Unit,
    onDismissRequest: () -> Unit,
    isDialogOpen: Boolean,
    modifier: Modifier = Modifier,
) {
    if (isDialogOpen) Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.clip(MaterialTheme.shapes.extraLarge)
        ) {
            Column(
                modifier = modifier.padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.health_profile_dialog_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(id = R.string.health_profile_dialog_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_age_100dp),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.age_label),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = stringResource(id = R.string.dummy_dialog_age),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_height_100dp),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.height_label),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = stringResource(id = R.string.dummy_dialog_height),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_scale_100dp),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.weight_label),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = stringResource(id = R.string.dummy_dialog_weight),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_male_100dp),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.gender_label),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = stringResource(id = R.string.dummy_dialog_gender),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onUpdate) {
                        Text(
                            text = stringResource(id = R.string.health_profile_dialog_negative_btn),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = onDone) {
                        Text(
                            text = stringResource(id = R.string.health_profile_dialog_positive_btn),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}
