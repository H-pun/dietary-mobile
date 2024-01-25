package dev.cisnux.dietary.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.cisnux.dietary.R
import dev.cisnux.dietary.presentation.ui.theme.DietaryTheme
import dev.cisnux.dietary.presentation.utils.isAgeValid
import dev.cisnux.dietary.presentation.utils.isHeightOrWeightValid

@Preview(showBackground = true)
@Composable
private fun UpdateHealthProfileDialogPreview() {
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

    DietaryTheme {
        UpdateHealthProfileDialog(
            onSave = { /*TODO*/ },
            onCancel = { /*TODO*/ },
            onDismissRequest = {},
            isDialogOpen = true,
            age = healthProfile.age,
            weight = healthProfile.weight,
            height = healthProfile.height,
            selectedGender = healthProfile.gender,
            onAgeChange = { newValue -> healthProfile = healthProfile.copy(age = newValue) },
            onHeightChange = { newValue -> healthProfile = healthProfile.copy(height = newValue) },
            onWeightChange = { newValue -> healthProfile = healthProfile.copy(weight = newValue) },
            onGenderChange = { newValue -> healthProfile = healthProfile.copy(gender = newValue) },
            genders = genders
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
    age: String,
    weight: String,
    height: String,
    selectedGender: String,
    genders: Array<String>,
    onAgeChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    if (isDialogOpen)
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
            ),
        ) {
            Scaffold(
                modifier = modifier,
                topBar = {
                    TopAppBar(
                        navigationIcon = {
                            IconButton(onClick = onCancel) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    contentDescription = null
                                )
                            }
                        },
                        title = {
                            Text(
                                text = stringResource(id = R.string.health_profile_dialog_title),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        actions = {
                            TextButton(
                                onClick = onSave,
                                enabled = age.isAgeValid() and height.isHeightOrWeightValid() and weight.isHeightOrWeightValid()
                            ) {
                                Text(
                                    text = stringResource(id = R.string.save),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    )
                }
            ) { paddingValues ->
                HealthProfileForm(
                    age = age,
                    weight = weight,
                    height = height,
                    selectedGender = selectedGender,
                    onAgeChange = onAgeChange,
                    onHeightChange = onHeightChange,
                    onWeightChange = onWeightChange,
                    onGenderChange = onGenderChange,
                    genders = genders,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .verticalScroll(scrollState)
                )
            }
        }
}