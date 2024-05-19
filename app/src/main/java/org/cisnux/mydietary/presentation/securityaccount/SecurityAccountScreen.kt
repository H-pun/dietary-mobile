package org.cisnux.mydietary.presentation.securityaccount

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import org.cisnux.mydietary.R
import org.cisnux.mydietary.presentation.ui.components.ListTileProfile
import org.cisnux.mydietary.presentation.ui.components.NavigationDrawer
import org.cisnux.mydietary.presentation.ui.components.UserAccountCard
import org.cisnux.mydietary.presentation.ui.theme.DietaryTheme
import org.cisnux.mydietary.utils.AppDestination
import org.cisnux.mydietary.utils.Failure
import org.cisnux.mydietary.utils.UiState
import org.cisnux.mydietary.utils.isEmailValid
import org.cisnux.mydietary.utils.isPasswordSecure

@Composable
fun SecurityAccountScreen(
    drawerNavigation: (destination: AppDestination, currentRoute: AppDestination) -> Unit,
    navigateToSignIn: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SecurityAccountViewModel = hiltViewModel(),
    navigateUp: () -> Unit,
) {
    BackHandler {
        navigateUp()
    }

    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val context = LocalContext.current
    val latestEmailAddress by viewModel.emailAddress.collectAsState(initial = null)
    val isVerified by viewModel.isVerified.collectAsState(initial = false)

    var emailAddress by rememberSaveable(latestEmailAddress) {
        mutableStateOf(latestEmailAddress ?: "")
    }
    var oldPassword by rememberSaveable {
        mutableStateOf("")
    }
    var newPassword by rememberSaveable {
        mutableStateOf("")
    }
    var isChangeEmailDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }
    var isChangePasswordDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }
    val changeEmailState by viewModel.changeEmailState.collectAsState(initial = UiState.Initialize)
    val changePasswordState by viewModel.changePasswordState.collectAsState(initial = UiState.Initialize)
    val verifyEmailState by viewModel.verifyEmailState.collectAsState(initial = UiState.Initialize)
    val userProfile by viewModel.userProfile.collectAsState(initial = null)

    when {
        changeEmailState is UiState.Success -> {
            (changeEmailState as UiState.Success).data?.let {
                LaunchedEffect(snackbarHostState) {
                    snackbarHostState.showSnackbar(
                        message = "Berhasil mengubah email address",
                        withDismissAction = true,
                        duration = SnackbarDuration.Long
                    )
                }
            }
        }

        changeEmailState is UiState.Error -> {
            (changeEmailState as UiState.Error).error?.let { exception ->
                LaunchedEffect(snackbarHostState) {
                    exception.message?.let {
                        val snackbarResult = snackbarHostState.showSnackbar(
                            message = it,
                            actionLabel = context.getString(R.string.retry),
                            withDismissAction = true,
                            duration = SnackbarDuration.Long
                        )
                        if (snackbarResult == SnackbarResult.ActionPerformed)
                            viewModel.changeEmail(
                                newEmail = emailAddress,
                            )
                    }
                }
                if (exception is Failure.UnauthorizedFailure && exception.message?.lowercase()
                        ?.contains("token") == true
                ) {
                    viewModel.signOut()
                    navigateToSignIn()
                }
            }
        }

        changePasswordState is UiState.Success -> {
            (changePasswordState as UiState.Success).data?.let {
                LaunchedEffect(snackbarHostState) {
                    snackbarHostState.showSnackbar(
                        message = "Berhasil mengubah password",
                        withDismissAction = true,
                        duration = SnackbarDuration.Long
                    )
                }
            }
        }

        changePasswordState is UiState.Error -> {
            (changePasswordState as UiState.Error).error?.let { exception ->
                LaunchedEffect(snackbarHostState) {
                    exception.message?.let {
                        val snackbarResult = snackbarHostState.showSnackbar(
                            message = it,
                            actionLabel = context.getString(R.string.retry),
                            withDismissAction = true,
                            duration = SnackbarDuration.Long
                        )
                        if (snackbarResult == SnackbarResult.ActionPerformed)
                            viewModel.changePassword(
                                oldPassword = oldPassword,
                                newPassword = newPassword
                            )
                    }
                }
                if (exception is Failure.UnauthorizedFailure && exception.message?.lowercase()
                        ?.contains("token") == true
                ) {
                    viewModel.signOut()
                    navigateToSignIn()
                }
            }
        }

        verifyEmailState is UiState.Success -> {
            (verifyEmailState as UiState.Success).data?.let {
                LaunchedEffect(snackbarHostState) {
                    snackbarHostState.showSnackbar(
                        message = "Periksa inbox email anda",
                        withDismissAction = true,
                        duration = SnackbarDuration.Long
                    )
                }
            }
        }

        verifyEmailState is UiState.Error -> {
            (verifyEmailState as UiState.Error).error?.let { exception ->
                LaunchedEffect(snackbarHostState) {
                    exception.message?.let {
                        val snackbarResult = snackbarHostState.showSnackbar(
                            message = it,
                            actionLabel = context.getString(R.string.retry),
                            withDismissAction = true,
                            duration = SnackbarDuration.Long
                        )
                        if (snackbarResult == SnackbarResult.ActionPerformed)
                            viewModel.verifyEmail(
                                email = emailAddress,
                            )
                    }
                }
                if (exception is Failure.UnauthorizedFailure) {
                    viewModel.signOut()
                    navigateToSignIn()
                }
            }
        }
    }

    SecurityAccountContent(
        snackbarHostState = snackbarHostState,
        signOut = {
            viewModel.signOut()
            navigateToSignIn()
        },
        onSelectedDestination = drawerNavigation,
        drawerTitle = {
            UserAccountCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                username = userProfile?.username,
                email = userProfile?.emailAddress,
                isVerified = userProfile?.isVerified
            )
        },
        modifier = modifier
    ) {
        SecurityAccountBody(
            modifier = Modifier.padding(it),
            onVerifyEmail = {
                viewModel.verifyEmail(emailAddress)
            },
            isVerified = isVerified,
            isVerifyEmailLoading = verifyEmailState is UiState.Loading || changeEmailState is UiState.Loading,
            isChangePasswordLoading = changePasswordState is UiState.Loading,
            isChangeEmailLoading = changeEmailState is UiState.Loading,
            onChangeEmail = {
                isChangeEmailDialogOpen = true
            },
            onChangePassowrd = {
                isChangePasswordDialogOpen = true
            }
        )
        ChangeEmailDialog(
            onCancel = {
                isChangeEmailDialogOpen = false
                emailAddress = latestEmailAddress ?: ""
            },
            isDialogOpen = isChangeEmailDialogOpen,
            onDismissRequest = {
                isChangeEmailDialogOpen = false
                emailAddress = latestEmailAddress ?: ""
            },
            emailAddress = emailAddress,
            onSave = {
                isChangeEmailDialogOpen = false
                viewModel.changeEmail(emailAddress)
                emailAddress = latestEmailAddress ?: ""
            },
            onEmailAddressChange = { newValue -> emailAddress = newValue }
        )
        ChangePasswordDialog(
            onCancel = {
                isChangePasswordDialogOpen = false
                oldPassword = ""
                newPassword = ""
            },
            isDialogOpen = isChangePasswordDialogOpen,
            onDismissRequest = {
                isChangePasswordDialogOpen = false
                oldPassword = ""
                newPassword = ""
            },
            oldPassword = oldPassword,
            newPassword = newPassword,
            onSave = {
                isChangePasswordDialogOpen = false
                viewModel.changePassword(oldPassword, newPassword)
                oldPassword = ""
                newPassword = ""
            },
            onOldPasswordChange = { newValue -> oldPassword = newValue },
            onNewPasswordChange = { newValue -> newPassword = newValue }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SecurityAccountContentPreview() {
    DietaryTheme {
        SecurityAccountContent(onSelectedDestination = { _, _ -> }) {
            SecurityAccountBody(
                modifier = Modifier.padding(it),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SecurityAccountContent(
    onSelectedDestination: (destination: AppDestination, currentRoute: AppDestination) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember {
        SnackbarHostState()
    },
    drawerTitle: @Composable ColumnScope.() -> Unit = {},
    signOut: () -> Unit = {},
    body: @Composable (innerPadding: PaddingValues) -> Unit = {},
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    NavigationDrawer(
        title = drawerTitle,
        signOut = signOut,
        currentRoute = AppDestination.AccountSecurityRoute,
        onSelectedDestination = onSelectedDestination,
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(imageVector = Icons.Rounded.Menu, contentDescription = null)
                        }
                    },
                    title = {
                        Text(
                            text = stringResource(id = R.string.account_and_security_title),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.ExtraBold
                        )
                    },
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            modifier = modifier,
        ) {
            body(it)
        }
    }
}

@Composable
private fun SecurityAccountBody(
    modifier: Modifier = Modifier,
    isVerified: Boolean = false,
    isVerifyEmailLoading: Boolean = false,
    isChangeEmailLoading: Boolean = false,
    isChangePasswordLoading: Boolean = false,
    onChangePassowrd: () -> Unit = {},
    onChangeEmail: () -> Unit = {},
    onVerifyEmail: () -> Unit = {},
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        ListTileProfile(
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Email,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            },
            label = {
                Text(
                    text = "Ubah Email", style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            },
            bodyLabel = {
                if (!isChangeEmailLoading)
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = null
                    )
                else CircularProgressIndicator(modifier = Modifier.size(24.dp))
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onChangeEmail, enabled = !isChangeEmailLoading)
        )
        ListTileProfile(
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = null
                )
            },
            label = {
                Text(
                    text = "Ubah Password", style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            },
            bodyLabel = {
                if (!isChangePasswordLoading)
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                else CircularProgressIndicator(modifier = Modifier.size(24.dp))
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onChangePassowrd, enabled = !isChangePasswordLoading)
                .padding(top = 8.dp)
        )
        ListTileProfile(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_verified_account_100dp),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            },
            label = {
                Text(
                    text = "Verifikasi Email", style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            },
            bodyLabel = {
                if (isVerifyEmailLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    return@ListTileProfile
                }
                if (!isVerified)
                    Icon(imageVector = Icons.AutoMirrored.Rounded.Send, contentDescription = null)
                else
                    Icon(
                        painter = painterResource(id = R.drawable.ic_verified_account_100dp),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp),
                        tint = MaterialTheme.colorScheme.tertiary,
                    )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onVerifyEmail, enabled = !isVerifyEmailLoading && !isVerified)
                .padding(top = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangeEmailDialog(
    onCancel: () -> Unit,
    isDialogOpen: Boolean,
    onDismissRequest: () -> Unit,
    emailAddress: String,
    onEmailAddressChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSave: () -> Unit = {},
    isLoading: Boolean = false,
) {
    var isEmailAddressFocused by remember {
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
                                text = "Ubah Email",
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
                modifier = modifier,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .padding(it)
                        .padding(horizontal = 16.dp)
                ) {
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
                            .onFocusChanged { focusState ->
                                isEmailAddressFocused = focusState.isFocused
                            },
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onSave,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        enabled = emailAddress.isEmailValid() and !isLoading,
                    ) {
                        if (isLoading)
                            CircularProgressIndicator()
                        else
                            Text(text = "Perbarui")
                    }
                }
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangePasswordDialog(
    onCancel: () -> Unit,
    isDialogOpen: Boolean,
    onDismissRequest: () -> Unit,
    oldPassword: String,
    newPassword: String,
    onOldPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onSave: () -> Unit = {},
    isLoading: Boolean = false,
) {
    var isOldPasswordFocused by rememberSaveable {
        mutableStateOf(false)
    }
    var isOldPasswordVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var isNewPasswordFocused by rememberSaveable {
        mutableStateOf(false)
    }
    var isNewPasswordVisible by rememberSaveable {
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
                                text = "Ubah Password",
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
                modifier = modifier,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .padding(it)
                        .padding(horizontal = 16.dp)
                ) {
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
                        value = oldPassword,
                        onValueChange = onOldPasswordChange,
                        placeholder = {
                            Text(
                                text = stringResource(R.string.old_password_placeholder),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        supportingText = {
                            if (isOldPasswordFocused)
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
                        visualTransformation = if (isOldPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isOldPasswordVisible = !isOldPasswordVisible }) {
                                Icon(
                                    painter = painterResource(
                                        id = if (isOldPasswordVisible)
                                            R.drawable.ic_visibility_24_24dp
                                        else R.drawable.ic_visibility_off_24dp
                                    ),
                                    contentDescription = null,
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                isOldPasswordFocused = focusState.isFocused
                            },
                    )
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
                        value = newPassword,
                        onValueChange = onNewPasswordChange,
                        placeholder = {
                            Text(
                                text = stringResource(R.string.new_password_placeholder),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        supportingText = {
                            if (newPassword.isNotEmpty() && !newPassword.isPasswordSecure())
                                Text(
                                    text = stringResource(R.string.password_error_text),
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            else if (isNewPasswordFocused)
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
                        isError = newPassword.isNotEmpty() && !newPassword.isPasswordSecure(),
                        visualTransformation = if (isNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isNewPasswordVisible = !isNewPasswordVisible }) {
                                Icon(
                                    painter = painterResource(
                                        id = if (isNewPasswordVisible)
                                            R.drawable.ic_visibility_24_24dp
                                        else R.drawable.ic_visibility_off_24dp
                                    ),
                                    contentDescription = null,
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                isNewPasswordFocused = focusState.isFocused
                            },
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onSave,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        enabled = oldPassword.isNotBlank() and newPassword.isPasswordSecure() and !isLoading,
                    ) {
                        if (isLoading)
                            CircularProgressIndicator()
                        else
                            Text(text = "Perbarui")
                    }
                }
            }
        }
}
