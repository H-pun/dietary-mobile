package org.cisnux.mydietary.presentation.verifycode

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.cisnux.mydietary.R
import org.cisnux.mydietary.presentation.ui.theme.DietaryTheme
import org.cisnux.mydietary.utils.UiState
import org.cisnux.mydietary.utils.isResetCodeValid

@Composable
fun VerifyCodeScreen(
    navigateUp: () -> Unit,
    navigateToNewPassword: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VerifyCodeViewModel = hiltViewModel()
) {
    BackHandler {
        navigateUp()
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val resetPasswordState by viewModel.resetPasswordState.collectAsState()
    val context = LocalContext.current
    var code by rememberSaveable(viewModel.code) {
        mutableStateOf(viewModel.code ?: "")
    }

    when (resetPasswordState) {
        is UiState.Success -> {
            (resetPasswordState as UiState.Success).data?.let {
                LaunchedEffect(snackbarHostState) {
                    snackbarHostState.showSnackbar(
                        message = "Periksa inbox email anda",
                        withDismissAction = true,
                        duration = SnackbarDuration.Long
                    )
                }
            }
        }

        is UiState.Error -> {
            (resetPasswordState as UiState.Error).error?.let { exception ->
                LaunchedEffect(snackbarHostState) {
                    exception.message?.let {
                        val snackbarResult = snackbarHostState.showSnackbar(
                            message = it,
                            actionLabel = context.getString(R.string.retry),
                            withDismissAction = true,
                            duration = SnackbarDuration.Long
                        )
                        if (snackbarResult == SnackbarResult.ActionPerformed) viewModel.resetPassword()
                    }
                }
            }
        }

        else -> {}
    }

    VerifyPasswordContent(
        body = {
            VerifyPasswordBody(
                emailAddress = viewModel.emailAddress,
                onDone = { navigateToNewPassword(viewModel.emailAddress, code) },
                code = code,
                onCodeChange = { newValue ->
                    code = if (newValue.length <= 6)
                        newValue
                    else ""
                },
                onResend = viewModel::resetPassword,
                modifier = Modifier.padding(it),
                isLoading = resetPasswordState is UiState.Loading,
                onNavigateUp = navigateUp
            )
        }, snackbarHostState = snackbarHostState, modifier = modifier
    )
}

@Preview(
    showBackground = true,
    name = "light",
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.NONE,
    device = "id:pixel_3a"
)
@Composable
private fun VerifyPasswordContentPreview() {
    var code by rememberSaveable {
        mutableStateOf("")
    }
    var isLoading by rememberSaveable {
        mutableStateOf(false)
    }
    val coroutineScope = rememberCoroutineScope()

    DietaryTheme {
        VerifyPasswordContent(body = {
            VerifyPasswordBody(
                emailAddress = "fajrarisqulla@gmail.com",
                onDone = { /*TODO*/ },
                code = code,
                onCodeChange = { newValue ->
                    code = if (newValue.length <= 6)
                        newValue
                    else ""
                },
                onResend = {
                    coroutineScope.launch {
                        isLoading = true
                        delay(1500L)
                        isLoading = false
                    }
                },
                modifier = Modifier.padding(it),
                isLoading = isLoading
            )
        }, snackbarHostState = SnackbarHostState())
    }
}

@Composable
private fun VerifyPasswordBody(
    emailAddress: String,
    onDone: () -> Unit,
    onResend: () -> Unit,
    code: String,
    onCodeChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onNavigateUp: () -> Unit = {},
) {
    val focusRequester = remember { FocusRequester() }
    var timeLeft by rememberSaveable {
        mutableIntStateOf(60)
    }
    val configuration = LocalConfiguration.current

    LaunchedEffect(isLoading) {
        if (!isLoading) timeLeft = 60
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(isLoading) {
        while (timeLeft > 0 && !isLoading) {
            delay(1000L)
            timeLeft--
        }
    }

    LaunchedEffect(code) {
        if (code.isResetCodeValid()) onDone()
    }

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(PaddingValues(16.dp))
    ) {
        FilledIconButton(
            onClick = onNavigateUp,
            modifier = Modifier.align(Alignment.Start),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        Image(
            painter = painterResource(id = R.drawable.verify_code_illustration),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height((configuration.screenHeightDp * 0.38f).dp)
        )
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                    append("Masukkan 6-digit kode yang dikirim ke ")
                }
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(emailAddress)
                }
            },
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        BasicTextField(
            value = code,
            onValueChange = onCodeChange,
            keyboardActions = KeyboardActions(
                onDone = {
                    if (code.isResetCodeValid())
                        onDone()
                }
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done
            ),
            textStyle = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester = focusRequester),
            singleLine = true,
            maxLines = 1,
            decorationBox = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(6) {
                            Column(
                                modifier = Modifier.wrapContentWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = (code.getOrNull(it) ?: '0').toString(),
                                    style = code.getOrNull(it)
                                        ?.let {
                                            MaterialTheme.typography.headlineMedium.copy(
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        ?: MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.surfaceVariant)
                                )
                                HorizontalDivider(
                                    modifier = Modifier.width(30.dp),
                                    color = code.getOrNull(it)
                                        ?.let { MaterialTheme.colorScheme.primary }
                                        ?: MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                    }
                }
            }
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Belum dapat?",
                style = MaterialTheme.typography.labelLarge,
            )
            Spacer(modifier = Modifier.width(4.dp))
            TextButton(onClick = onResend, enabled = timeLeft == 0 && !isLoading) {
                if (!isLoading) Text(
                    text = "Kirim Ulang ${
                        if (timeLeft == 0) "" else "(${timeLeft})s"
                    }",
                ) else CircularProgressIndicator(modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun VerifyPasswordContent(
    body: @Composable (PaddingValues) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = modifier,
    ) {
        body(it)
    }
}
