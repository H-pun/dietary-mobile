package dev.cisnux.dietary.presentation.signin

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
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
import dev.cisnux.dietary.presentation.utils.isEmailValid
import dev.cisnux.dietary.presentation.utils.isPasswordSecure

@Composable
fun SignInScreen(
    navigateToHome: () -> Unit,
    navigateToAddMyProfile: () -> Unit,
    navigateToResetPassword: () -> Unit,
    navigateToSignUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var emailAddress by rememberSaveable {
        mutableStateOf("")
    }
    var password by rememberSaveable {
        mutableStateOf("")
    }
    val snackbarHostState = rememberSaveable {
        SnackbarHostState()
    }

    SignInContent(
        body = {
            SignInBody(
                onSignUp = navigateToSignUp,
                onGoogleSignIn = { /*TODO*/ },
                onEmailPasswordSignIn = { /*TODO*/ },
                emailAddress = emailAddress,
                password = password,
                onEmailAddressChange = { newValue -> emailAddress = newValue },
                onPasswordChange = { newValue -> password = newValue },
                onForgotPassword = navigateToResetPassword,
                modifier = modifier.padding(it)
            )
        },
        snackbarHostState = snackbarHostState,
    )
}

@Preview(
    showBackground = true, name = "light",
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.NONE
)
@Composable
private fun SignInContentPreview() {
    var emailAddress by rememberSaveable {
        mutableStateOf("")
    }
    var password by rememberSaveable {
        mutableStateOf("")
    }

    DietaryTheme {
        SignInContent(
            body = {
                SignInBody(
                    onSignUp = { /*TODO*/ },
                    onGoogleSignIn = { /*TODO*/ },
                    onEmailPasswordSignIn = { /*TODO*/ },
                    emailAddress = emailAddress,
                    password = password,
                    onEmailAddressChange = { newValue -> emailAddress = newValue },
                    onPasswordChange = { newValue -> password = newValue },
                    onForgotPassword = {},
                    modifier = Modifier.padding(it)
                )
            },
            snackbarHostState = SnackbarHostState(),
        )
    }
}

@Preview(
    showBackground = true, name = "dark and indonesia", backgroundColor = 0xFFFFFFFF,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    device = "id:pixel_7_pro", locale = "in"
)
@Composable
private fun SignInContentDarkPreview() {
    var emailAddress by rememberSaveable {
        mutableStateOf("")
    }
    var password by rememberSaveable {
        mutableStateOf("")
    }

    DietaryTheme {
        SignInContent(
            body = {
                SignInBody(
                    onSignUp = { /*TODO*/ },
                    onGoogleSignIn = { /*TODO*/ },
                    onEmailPasswordSignIn = { /*TODO*/ },
                    emailAddress = emailAddress,
                    password = password,
                    onEmailAddressChange = { newValue -> emailAddress = newValue },
                    onPasswordChange = { newValue -> password = newValue },
                    onForgotPassword = {},
                    modifier = Modifier.padding(it)
                )
            },
            snackbarHostState = SnackbarHostState(),
        )
    }
}

@Preview(
    showBackground = true, name = "(loading) dark and indonesia", backgroundColor = 0xFFFFFFFF,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    device = "id:pixel_7_pro", locale = "in"
)
@Composable
private fun SignInContentLoadingDarkPreview() {
    var emailAddress by rememberSaveable {
        mutableStateOf("")
    }
    var password by rememberSaveable {
        mutableStateOf("")
    }

    DietaryTheme {
        SignInContent(
            body = {
                SignInBody(
                    onSignUp = { /*TODO*/ },
                    onGoogleSignIn = { /*TODO*/ },
                    onEmailPasswordSignIn = { /*TODO*/ },
                    emailAddress = emailAddress,
                    password = password,
                    onEmailAddressChange = { newValue -> emailAddress = newValue },
                    onPasswordChange = { newValue -> password = newValue },
                    onForgotPassword = {},
                    modifier = Modifier.padding(it),
                    isEmailPassSignInLoading = true,
                    isGoogleSignInLoading = true
                )
            },
            snackbarHostState = SnackbarHostState(),
        )
    }
}

@Composable
private fun SignInBody(
    onSignUp: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onEmailPasswordSignIn: () -> Unit,
    onForgotPassword: () -> Unit,
    emailAddress: String,
    password: String,
    onEmailAddressChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isEmailPassSignInLoading: Boolean = false,
    isGoogleSignInLoading: Boolean = false
) {
    var isPasswordVisible by rememberSaveable {
        mutableStateOf(false)
    }
    val scrollState = rememberScrollState()
    var isEmailAddressFocused by rememberSaveable {
        mutableStateOf(false)
    }
    var isPasswordFocused by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(state = scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.sign_in_illustration),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(330.dp)
        )
        Text(
            text = stringResource(R.string.welcome_back),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.ExtraBold,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = stringResource(R.string.you_ve_been_missed),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            shape = MaterialTheme.shapes.medium,
            onClick = onGoogleSignIn,
            enabled = !isEmailPassSignInLoading,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (isGoogleSignInLoading)
                CircularProgressIndicator()
            else {
                Image(
                    painter = painterResource(id = R.drawable.ic_google_24dp),
                    contentDescription = null,
                )
                Text(
                    text = stringResource(R.string.sign_in_with_google),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Divider(thickness = 2.dp)
            Surface(color = MaterialTheme.colorScheme.surface) {
                Text(
                    text = stringResource(R.string.or),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        OutlinedTextField(
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = stringResource(R.string.forgot_password),
                color = if (!isSystemInDarkTheme()) Color.Blue else Color.Cyan,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(MaterialTheme.shapes.extraLarge)
                    .clickable(onClick = onForgotPassword)
                    .padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Button(
            onClick = onEmailPasswordSignIn,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            enabled = emailAddress.isEmailValid() and password.isPasswordSecure() and !isEmailPassSignInLoading,
        ) {
            if (isEmailPassSignInLoading)
                CircularProgressIndicator()
            else
                Text(text = stringResource(R.string.sign_in))
        }
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.don_t_have_an_account),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.sign_up_here),
                color = if (!isSystemInDarkTheme()) Color.Blue else Color.Cyan,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(MaterialTheme.shapes.extraLarge)
                    .clickable(onClick = onSignUp)
                    .padding(4.dp)
            )
        }
    }
}

@Composable
private fun SignInContent(
    body: @Composable (PaddingValues) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = modifier
    ) { innerPadding ->
        body(innerPadding)
    }
}