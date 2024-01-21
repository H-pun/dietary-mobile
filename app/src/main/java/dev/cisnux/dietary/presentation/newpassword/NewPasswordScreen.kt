package dev.cisnux.dietary.presentation.newpassword

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
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import dev.cisnux.dietary.R
import dev.cisnux.dietary.presentation.ui.theme.DietaryTheme
import dev.cisnux.dietary.presentation.utils.isPasswordSecure

@Preview(
    showBackground = true,
    name = "light",
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.NONE, device = "id:pixel_7_pro"
)
@Composable
private fun NewPasswordContentPreview() {
    var password by rememberSaveable {
        mutableStateOf("")
    }
    var confirmationPassword by remember {
        mutableStateOf("")
    }

    DietaryTheme {
        NewPasswordContent(
            body = {
                NewPasswordBody(
                    password = password,
                    confirmationPassword = confirmationPassword,
                    onPasswordChange = { newValue -> password = newValue },
                    onConfirmationPasswordChange = { newValue -> confirmationPassword = newValue },
                    onResetPassword = {},
                    modifier = Modifier.padding(it)
                )
            },
            snackbarHostState = SnackbarHostState(),
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
private fun NewPasswordContentDarkPreview() {
    var password by rememberSaveable {
        mutableStateOf("")
    }
    var confirmationPassword by rememberSaveable {
        mutableStateOf("")
    }

    DietaryTheme {
        NewPasswordContent(
            body = {
                NewPasswordBody(
                    password = password,
                    confirmationPassword = confirmationPassword,
                    onPasswordChange = { newValue -> password = newValue },
                    onConfirmationPasswordChange = { newValue -> confirmationPassword = newValue },
                    onResetPassword = {},
                    modifier = Modifier.padding(it)
                )
            },
            snackbarHostState = SnackbarHostState(),
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
private fun NewPasswordContentLoadingDarkPreview() {
    var password by rememberSaveable {
        mutableStateOf("")
    }
    var confirmationPassword by rememberSaveable {
        mutableStateOf("")
    }

    DietaryTheme {
        NewPasswordContent(
            body = {
                NewPasswordBody(
                    password = password,
                    confirmationPassword = confirmationPassword,
                    onPasswordChange = { newValue -> password = newValue },
                    onConfirmationPasswordChange = { newValue -> confirmationPassword = newValue },
                    onResetPassword = {},
                    modifier = Modifier.padding(it),
                    isResetPasswordLoading = true
                )
            },
            snackbarHostState = SnackbarHostState(),
        )
    }
}

@Composable
private fun NewPasswordBody(
    password: String,
    confirmationPassword: String,
    onPasswordChange: (String) -> Unit,
    onConfirmationPasswordChange: (String) -> Unit,
    onResetPassword: () -> Unit,
    modifier: Modifier = Modifier,
    isResetPasswordLoading: Boolean = false
) {
    var isPasswordVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var isConfirmationPassVisible by rememberSaveable {
        mutableStateOf(false)
    }
    val scrollState = rememberScrollState()
    var isPasswordFocused by rememberSaveable {
        mutableStateOf(false)
    }
    var isConfirmationPassFocused by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(state = scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Image(
            painter = painterResource(id = R.drawable.new_password_illustration),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.reset_password),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.ExtraBold,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = stringResource(R.string.new_password_message),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            value = password,
            onValueChange = onPasswordChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.password_placeholder),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            supportingText = {
                if (password.isNotEmpty() && !password.isPasswordSecure())
                    Text(
                        text = stringResource(R.string.password_error_text),
                        style = MaterialTheme.typography.bodySmall,
                    )
                else if (isPasswordFocused)
                    Text(
                        text = stringResource(id = R.string.supporting_text_required),
                        style = MaterialTheme.typography.bodySmall,
                    )
            },
            singleLine = true,
            label = {
                Text(
                    text = stringResource(R.string.password_label),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            isError = password.isNotEmpty() && !password.isPasswordSecure(),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        painter = painterResource(
                            id = if (isPasswordVisible)
                                R.drawable.ic_visibility_24_24dp
                            else R.drawable.ic_visibility_off_24dp
                        ),
                        contentDescription = null,
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    isPasswordFocused = it.isFocused
                },
        )
        OutlinedTextField(
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            value = confirmationPassword,
            onValueChange = onConfirmationPasswordChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.confirmation_password_placeholder),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            supportingText = {
                if (confirmationPassword.isNotEmpty() && confirmationPassword != password)
                    Text(
                        text = stringResource(R.string.confirmation_password_error_text),
                        style = MaterialTheme.typography.bodySmall,
                    )
                else if (isConfirmationPassFocused)
                    Text(
                        text = stringResource(id = R.string.supporting_text_required),
                        style = MaterialTheme.typography.bodySmall,
                    )
            },
            singleLine = true,
            label = {
                Text(
                    text = stringResource(id = R.string.confirmation_password_label),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            isError = confirmationPassword.isNotEmpty() && confirmationPassword != password,
            visualTransformation = if (isConfirmationPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { isConfirmationPassVisible = !isConfirmationPassVisible }) {
                    Icon(
                        painter = painterResource(
                            id = if (isConfirmationPassVisible)
                                R.drawable.ic_visibility_24_24dp
                            else R.drawable.ic_visibility_off_24dp
                        ),
                        contentDescription = null,
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    isConfirmationPassFocused = it.isFocused
                },
        )
        Button(
            onClick = onResetPassword,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            enabled = password.isPasswordSecure() and (password == confirmationPassword) and !isResetPasswordLoading,
        ) {
            if (isResetPasswordLoading)
                CircularProgressIndicator()
            else
                Text(text = "Reset Password")
        }
    }
}

@Composable
private fun NewPasswordContent(
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
