package org.cisnux.mydietary.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.cisnux.mydietary.R
import org.cisnux.mydietary.presentation.ui.theme.DietaryTheme
import org.cisnux.mydietary.utils.isIntAnswerValid
import org.cisnux.mydietary.utils.isGreaterThanZeroValid

@Preview(showBackground = true)
@Composable
private fun HealthProfileFormPreview() {
    val genders = stringArrayResource(id = R.array.gender)

    DietaryTheme {
        HealthProfileForm(
            age = "",
            weight = "",
            height = "",
            selectedGender = genders[0],
            waistCircumference = "",
            onAgeChange = { },
            onHeightChange = { },
            onWeightChange = { },
            onGenderChange = { },
            onWaistCircumference = {},
            genders = genders
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthProfileForm(
    age: String,
    weight: String,
    height: String,
    selectedGender: String,
    waistCircumference: String,
    onAgeChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onWaistCircumference: (String) -> Unit,
    genders: Array<String>,
    modifier: Modifier = Modifier
) {
    var isAgeFocused by rememberSaveable {
        mutableStateOf(false)
    }
    var isWeightFocused by rememberSaveable {
        mutableStateOf(false)
    }
    var isHeightFocused by rememberSaveable {
        mutableStateOf(false)
    }
    var isWaistCircumferenceFocused by rememberSaveable {
        mutableStateOf(false)
    }
    var isGenderExpanded by rememberSaveable { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_age_100dp),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            value = age,
            singleLine = true,
            onValueChange = onAgeChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.age_placeholder),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = {
                Text(
                    text = stringResource(id = R.string.age_label),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            supportingText = {
                if (age.isNotEmpty() and !age.isIntAnswerValid())
                    Text(
                        text = stringResource(R.string.age_error_text),
                        style = MaterialTheme.typography.bodySmall,
                    )
                else if (isAgeFocused)
                    Text(
                        text = stringResource(R.string.supporting_text_required),
                        style = MaterialTheme.typography.bodySmall,
                    )
            },
            isError = age.isNotEmpty() and !age.isIntAnswerValid(),
            trailingIcon = {
                if (age.isNotEmpty() and !age.isIntAnswerValid())
                    Icon(
                        painter = painterResource(id = R.drawable.ic_round_error_24dp),
                        contentDescription = null,
                    )
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    isAgeFocused = it.isFocused
                },
        )
        OutlinedTextField(
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_scale_100dp),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            value = weight,
            singleLine = true,
            onValueChange = onWeightChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.weight_placeholder),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = {
                Text(
                    text = stringResource(id = R.string.weight_label),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            supportingText = {
                if (weight.isNotEmpty() and !weight.isGreaterThanZeroValid())
                    Text(
                        text = stringResource(R.string.weight_error_text),
                        style = MaterialTheme.typography.bodySmall,
                    )
                else if (isWeightFocused)
                    Text(
                        text = stringResource(R.string.supporting_text_required),
                        style = MaterialTheme.typography.bodySmall,
                    )
            },
            isError = weight.isNotEmpty() and !weight.isGreaterThanZeroValid(),
            trailingIcon = {
                if (weight.isNotEmpty() and !weight.isGreaterThanZeroValid())
                    Icon(
                        painter = painterResource(id = R.drawable.ic_round_error_24dp),
                        contentDescription = null,
                    )
                else Text(text = stringResource(R.string.trailing_text_kg))
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    isWeightFocused = it.isFocused
                },
        )
        OutlinedTextField(
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_height_100dp),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            value = height,
            singleLine = true,
            onValueChange = onHeightChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.height_placeholder),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = {
                Text(
                    text = stringResource(id = R.string.height_label),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            supportingText = {
                if (height.isNotEmpty() and !height.isGreaterThanZeroValid())
                    Text(
                        text = stringResource(R.string.height_error_text),
                        style = MaterialTheme.typography.bodySmall,
                    )
                else if (isHeightFocused)
                    Text(
                        text = stringResource(R.string.supporting_text_required),
                        style = MaterialTheme.typography.bodySmall,
                    )
            },
            isError = height.isNotEmpty() and !height.isGreaterThanZeroValid(),
            trailingIcon = {
                if (height.isNotEmpty() and !height.isGreaterThanZeroValid())
                    Icon(
                        painter = painterResource(id = R.drawable.ic_round_error_24dp),
                        contentDescription = null,
                    )
                else Text(text = stringResource(R.string.trailing_text_cm))
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    isHeightFocused = it.isFocused
                },
        )
        OutlinedTextField(
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_waist_100dp),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            value = waistCircumference,
            singleLine = true,
            onValueChange = onWaistCircumference,
            placeholder = {
                Text(
                    text = "Masukkan lingkar pinggang anda",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = {
                Text(
                    text = "Lingkar Pinggang",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            supportingText = {
                if (waistCircumference.isNotEmpty() and !waistCircumference.isGreaterThanZeroValid())
                    Text(
                        text = stringResource(R.string.waist_error_text),
                        style = MaterialTheme.typography.bodySmall,
                    )
                else if (isWaistCircumferenceFocused)
                    Text(
                        text = stringResource(R.string.supporting_text_required),
                        style = MaterialTheme.typography.bodySmall,
                    )
            },
            isError = waistCircumference.isNotEmpty() and !waistCircumference.isGreaterThanZeroValid(),
            trailingIcon = {
                if (waistCircumference.isNotEmpty() and !waistCircumference.isGreaterThanZeroValid())
                    Icon(
                        painter = painterResource(id = R.drawable.ic_round_error_24dp),
                        contentDescription = null,
                    )
                else Text(text = stringResource(R.string.trailing_text_cm))
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    isWaistCircumferenceFocused = it.isFocused
                },
        )
        ExposedDropdownMenuBox(
            expanded = isGenderExpanded,
            onExpandedChange = { isGenderExpanded = it },
        ) {
            OutlinedTextField(
                leadingIcon = {
                    Icon(
                        painter = painterResource(
                            id = if (selectedGender == genders[0]) R.drawable.ic_male_100dp
                            else R.drawable.ic_female_100dp
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                value = selectedGender,
                onValueChange = {},
                label = {
                    Text(
                        text = stringResource(id = R.string.gender_label),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isGenderExpanded) },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
            )
            ExposedDropdownMenu(
                expanded = isGenderExpanded,
                onDismissRequest = { isGenderExpanded = false },
            ) {
                genders.forEach { selectedOption ->
                    DropdownMenuItem(
                        text = { Text(selectedOption) },
                        onClick = {
                            onGenderChange(selectedOption)
                            isGenderExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }
}