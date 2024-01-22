package dev.cisnux.dietary.presentation.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dev.cisnux.dietary.R
import dev.cisnux.dietary.presentation.ui.components.BottomBar
import dev.cisnux.dietary.presentation.ui.theme.DietaryTheme
import dev.cisnux.dietary.presentation.utils.AppDestination
import dev.cisnux.dietary.presentation.utils.activity
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navigateForBottomNav: (destination: AppDestination, currentRoute: AppDestination) -> Unit,
    onFabFoodScanner: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = rememberSaveable {
        SnackbarHostState()
    }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val cameraLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                onFabFoodScanner()
            } else {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.camera_permission_message),
                        withDismissAction = true,
                        duration = SnackbarDuration.Long
                    )
                }
            }
        }
    var isCameraDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }

    HomeContent(
        onSelectedDestination = navigateForBottomNav,
        body = {
            HomeBody(modifier = modifier.padding(it))
            CameraAccessDialog(
                onDismissRequest = { isCameraDialogOpen = false },
                onAgree = {
                    isCameraDialogOpen = false
                    cameraLauncher.launch(Manifest.permission.CAMERA)
                },
                isDialogOpen = isCameraDialogOpen
            )
        },
        shouldBottomBarOpen = true,
        snackbarHostState = snackbarHostState,
        onFabFoodScanner = {
            when {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED -> {
                    onFabFoodScanner()
                }

                context.activity?.let {
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        it,
                        Manifest.permission.CAMERA
                    )
                } == true -> {
                    isCameraDialogOpen = true
                }

                else -> {
                    cameraLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    DietaryTheme {
        HomeContent(
            onSelectedDestination = { _, _ -> },
            body = { HomeBody(modifier = Modifier.padding(it)) },
            shouldBottomBarOpen = true,
            snackbarHostState = SnackbarHostState(),
            onFabFoodScanner = {}
        )
    }
}

@Composable
private fun HomeContent(
    onSelectedDestination: (destination: AppDestination, currentRoute: AppDestination) -> Unit,
    body: @Composable (innerPadding: PaddingValues) -> Unit,
    shouldBottomBarOpen: Boolean,
    snackbarHostState: SnackbarHostState,
    onFabFoodScanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        bottomBar = {
            AnimatedVisibility(visible = shouldBottomBarOpen) {
                BottomBar(
                    currentRoute = AppDestination.HomeRoute,
                    onSelectedDestination = onSelectedDestination
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onFabFoodScanner) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_food_scanner_24dp),
                    contentDescription = stringResource(R.string.food_scanner_title)
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        body(innerPadding)
    }
}

@Composable
private fun HomeBody(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Text(
            text = stringResource(id = R.string.home_title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(alignment = Alignment.Center)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CameraAccessDialogPreview() {
    DietaryTheme {
        CameraAccessDialog(onDismissRequest = { /*TODO*/ }, onAgree = {}, isDialogOpen = true)
    }
}

@Composable
private fun CameraAccessDialog(
    onDismissRequest: () -> Unit,
    onAgree: () -> Unit,
    isDialogOpen: Boolean,
    modifier: Modifier = Modifier
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
                    text = stringResource(id = R.string.food_scanner_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.camera_access_dialog_message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text(
                            text = stringResource(R.string.no_thanks),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = onAgree) {
                        Text(
                            text = stringResource(R.string.yes),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}




