package org.cisnux.mydietary.presentation.devmode

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.cisnux.mydietary.R
import org.cisnux.mydietary.presentation.ui.theme.DietaryTheme
import org.cisnux.mydietary.utils.isHttps

@Composable
fun DevModeScreen(
    navigateUp: () -> Unit,
    devModeViewModel: DevModeViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember {
        SnackbarHostState()

    }
    val currentBaseUrl by devModeViewModel.baseUrl.collectAsState()
    var baseUrl by remember(currentBaseUrl) {
        mutableStateOf(currentBaseUrl)
    }

    DevModeContent(
        snackbarHostState = snackbarHostState,
        baseUrl = baseUrl,
        onBaseUrlChanged = { newValue ->
            baseUrl = newValue
        },
        onSave = {
            devModeViewModel.updateBaseUrl(baseUrl = baseUrl)
                .invokeOnCompletion {
                    navigateUp()
                }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_8_pro")
@Composable
private fun DevModeContentPreview() {
    var baseUrl by remember {
        mutableStateOf("")
    }

    DietaryTheme {
        DevModeContent(
            snackbarHostState = SnackbarHostState(),
            baseUrl = baseUrl,
            onBaseUrlChanged = { newValue -> baseUrl = newValue })
    }
}

@Composable
private fun DevModeContent(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    baseUrl: String = "",
    onBaseUrlChanged: (String) -> Unit = {},
    onSave: () -> Unit = {},
) {
    var isBaseUrlFocused by rememberSaveable {
        mutableStateOf(false)
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (baseUrl.isHttps())
                    onSave()
            }) {
                Icon(imageVector = Icons.Default.AddCircle, contentDescription = null)
            }
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = modifier
                .padding(it)
                .padding(horizontal = 16.dp),
        ) {
            OutlinedTextField(
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                value = baseUrl,
                onValueChange = onBaseUrlChanged,
                singleLine = true,
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.api_url_placeholder),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                label = {
                    Text(
                        text = stringResource(R.string.api_url_label),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_insert_link_24dp),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                supportingText = {
                    if (baseUrl.isNotEmpty() and !baseUrl.isHttps())
                        Text(
                            text = stringResource(R.string.api_url_error_text),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    else if (isBaseUrlFocused)
                        Text(
                            text = stringResource(R.string.supporting_text_required),
                            style = MaterialTheme.typography.bodySmall,
                        )
                },
                isError = baseUrl.isNotEmpty() and !baseUrl.isHttps(),
                trailingIcon = {
                    if (baseUrl.isNotEmpty() and !baseUrl.isHttps())
                        Icon(
                            painter = painterResource(id = R.drawable.ic_round_error_24dp),
                            contentDescription = null,
                        )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        isBaseUrlFocused = focusState.isFocused
                    },
            )
        }
    }
}
