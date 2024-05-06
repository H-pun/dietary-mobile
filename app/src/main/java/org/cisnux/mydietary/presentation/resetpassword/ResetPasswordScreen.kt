package org.cisnux.mydietary.presentation.resetpassword

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.cisnux.mydietary.R
import org.cisnux.mydietary.presentation.ui.theme.DietaryTheme
import org.cisnux.mydietary.utils.isEmailValid
import org.cisnux.mydietary.utils.UiState

@Composable
fun ResetPasswordScreen(
    navigateToSignIn: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {
    var emailAddress by rememberSaveable {
        mutableStateOf("")
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val resetPasswordState by viewModel.resetPasswordState.collectAsState()
    val context = LocalContext.current

    when (resetPasswordState) {
        is UiState.Success -> {
            navigateToSignIn()
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
                        if (snackbarResult == SnackbarResult.ActionPerformed)
                            viewModel.resetPassword(emailAddress)
                    }
                }
            }
        }

        else -> {}
    }

    ResetPasswordContent(
        body = {
            ResetPasswordBody(
                onNavigateUp = navigateToSignIn,
                onVerifyEmailAddress = { viewModel.resetPassword(emailAddress) },
                emailAddress = emailAddress,
                onEmailAddressChange = { newValue -> emailAddress = newValue },
                modifier = modifier.padding(it),
                isVerifyEmailAddressLoading = resetPasswordState is UiState.Loading
            )
        },
        snackbarHostState = snackbarHostState
    )
}

@Preview(
    showBackground = true,
    name = "light",
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.NONE, device = "id:pixel_7_pro"
)
@Composable
private fun ResetPasswordPreview() {
    var emailAddress by rememberSaveable {
        mutableStateOf("")
    }

    DietaryTheme {
        ResetPasswordContent(
            body = {
                ResetPasswordBody(
                    onNavigateUp = { /*TODO*/ },
                    onVerifyEmailAddress = { /*TODO*/ },
                    emailAddress = emailAddress,
                    onEmailAddressChange = { newValue -> emailAddress = newValue },
                    modifier = Modifier.padding(it)
                )
            }, snackbarHostState = SnackbarHostState()
        )
    }
}

@Preview(
    showBackground = true,
    name = "dark and indonesia",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.NONE, device = "id:pixel_7_pro", locale = "in"
)
@Composable
private fun ResetPasswordDarkPreview() {
    var emailAddress by rememberSaveable {
        mutableStateOf("")
    }

    DietaryTheme {
        ResetPasswordContent(
            body = {
                ResetPasswordBody(
                    onNavigateUp = { /*TODO*/ },
                    onVerifyEmailAddress = { /*TODO*/ },
                    emailAddress = emailAddress,
                    onEmailAddressChange = { newValue -> emailAddress = newValue },
                    modifier = Modifier.padding(it)
                )
            },
            snackbarHostState = SnackbarHostState()
        )
    }
}

@Preview(
    showBackground = true,
    name = "(loading) dark and indonesia",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.NONE, device = "id:pixel_7_pro", locale = "in"
)
@Composable
private fun ResetPasswordLoadingDarkPreview() {
    var emailAddress by rememberSaveable {
        mutableStateOf("")
    }

    DietaryTheme {
        ResetPasswordContent(
            body = {
                ResetPasswordBody(
                    onNavigateUp = { /*TODO*/ },
                    onVerifyEmailAddress = { /*TODO*/ },
                    emailAddress = emailAddress,
                    onEmailAddressChange = { newValue -> emailAddress = newValue },
                    modifier = Modifier.padding(it),
                    isVerifyEmailAddressLoading = true
                )
            },
            snackbarHostState = SnackbarHostState()
        )
    }
}

@Composable
private fun ResetPasswordBody(
    onNavigateUp: () -> Unit,
    onVerifyEmailAddress: () -> Unit,
    emailAddress: String,
    onEmailAddressChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isVerifyEmailAddressLoading: Boolean = false,
) {
    val scrollState = rememberScrollState()
    var isEmailAddressFocused by rememberSaveable {
        mutableStateOf(false)
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(state = scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
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
            painter = painterResource(id = R.drawable.reset_password_illustration),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(330.dp)
        )
        Text(
            text = stringResource(R.string.reset_password),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.ExtraBold,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = stringResource(R.string.reset_password_message),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Email,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            value = emailAddress,
            singleLine = true,
            onValueChange = onEmailAddressChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.email_address_placeholder),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.email_address_label),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            supportingText = {
                if (emailAddress.isNotEmpty() and !emailAddress.isEmailValid())
                    Text(
                        text = stringResource(R.string.email_address_error_text),
                        style = MaterialTheme.typography.bodySmall,
                    )
                else if (isEmailAddressFocused)
                    Text(
                        text = stringResource(R.string.supporting_text_required),
                        style = MaterialTheme.typography.bodySmall,
                    )
            },
            isError = emailAddress.isNotEmpty() and !emailAddress.isEmailValid(),
            trailingIcon = {
                if (emailAddress.isNotEmpty() and !emailAddress.isEmailValid())
                    Icon(
                        painter = painterResource(id = R.drawable.ic_round_error_24dp),
                        contentDescription = null,
                    )
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    isEmailAddressFocused = it.isFocused
                },
        )
        Spacer(modifier = Modifier.height(2.dp))
        Button(
            onClick = onVerifyEmailAddress,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            enabled = emailAddress.isEmailValid() and !isVerifyEmailAddressLoading,
        ) {
            if (isVerifyEmailAddressLoading)
                CircularProgressIndicator()
            else
                Text(text = stringResource(R.string.verify_email))
        }
    }
}

@Composable
private fun ResetPasswordContent(
    body: @Composable (PaddingValues) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
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
