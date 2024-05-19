package org.cisnux.mydietary.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.cisnux.mydietary.R
import org.cisnux.mydietary.presentation.addmyprofile.MyProfile
import org.cisnux.mydietary.presentation.ui.theme.DietaryTheme
import org.cisnux.mydietary.utils.isFloatAndGreaterThanZero
import org.cisnux.mydietary.utils.isUsernameValid

@Preview(showBackground = true, locale = "in")
@Composable
private fun MyProfileFormPreview() {
    val genders = stringArrayResource(id = R.array.gender)
    val goals = stringArrayResource(id = R.array.goal)
    val activityLevels = stringArrayResource(id = R.array.activity_level)
    val activityDescriptions = stringArrayResource(id = R.array.activity_description)

    var myProfile by rememberSaveable {
        mutableStateOf(
            MyProfile(
                username = "",
                age = "",
                weight = "",
                height = "",
                gender = genders[0],
                goal = goals[0],
                weightTarget = "",
                activityLevel = activityLevels[0],
                waistCircumference = ""
            )
        )
    }

    DietaryTheme {
        MyProfileForm(
            username = myProfile.username,
            age = myProfile.age,
            weight = myProfile.weight,
            height = myProfile.height,
            selectedGender = myProfile.gender,
            selectedGoal = myProfile.goal,
            weightTarget = myProfile.weightTarget,
            selectedActivityLevel = myProfile.activityLevel,
            genders = genders,
            activityLevels = activityLevels,
            goals = goals,
            activityDescriptions = activityDescriptions,
            onUsernameChange = { newValue ->
                myProfile = myProfile.copy(username = newValue)
            },
            onAgeChange = { newValue -> myProfile = myProfile.copy(age = newValue) },
            onHeightChange = { newValue -> myProfile = myProfile.copy(height = newValue) },
            onWeightChange = { newValue -> myProfile = myProfile.copy(weight = newValue) },
            onGenderChange = { newValue -> myProfile = myProfile.copy(gender = newValue) },
            onGoalChange = { newValue -> myProfile = myProfile.copy(goal = newValue) },
            onTargetWeightChange = { newValue ->
                myProfile = myProfile.copy(weightTarget = newValue)
            },
            onActivityLevelChange = { newValue ->
                myProfile = myProfile.copy(activityLevel = newValue)
            },
            waistCircumference = myProfile.waistCircumference,
            onWaistCircumference = { newValue ->
                myProfile = myProfile.copy(waistCircumference = newValue)
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileForm(
    username: String,
    age: String,
    weight: String,
    height: String,
    selectedGender: String,
    selectedGoal: String,
    weightTarget: String,
    waistCircumference: String,
    selectedActivityLevel: String,
    genders: Array<String>,
    activityLevels: Array<String>,
    goals: Array<String>,
    activityDescriptions: Array<String>,
    onUsernameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onGenderChange: (String) -> Unit,
    onGoalChange: (String) -> Unit,
    onWaistCircumference: (String) -> Unit,
    onTargetWeightChange: (String) -> Unit,
    onActivityLevelChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isUsernameFocused by rememberSaveable {
        mutableStateOf(false)
    }
    var isTargetWeightFocused by rememberSaveable {
        mutableStateOf(false)
    }
    var isGoalExpanded by rememberSaveable { mutableStateOf(false) }
    var isActivityLevelExpanded by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            value = username,
            singleLine = true,
            onValueChange = onUsernameChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.username_placeholder),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.username_label),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            supportingText = {
                if (username.isNotEmpty() and !username.isUsernameValid())
                    Text(
                        text = stringResource(R.string.username_error_text),
                        style = MaterialTheme.typography.bodySmall,
                    )
                else if (isUsernameFocused)
                    Text(
                        text = stringResource(R.string.supporting_text_required),
                        style = MaterialTheme.typography.bodySmall,
                    )
            },
            isError = username.isNotEmpty() and !username.isUsernameValid(),
            trailingIcon = {
                if (username.isNotEmpty() and !username.isUsernameValid())
                    Icon(
                        painter = painterResource(id = R.drawable.ic_round_error_24dp),
                        contentDescription = null,
                    )
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    isUsernameFocused = it.isFocused
                },
        )
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
            waistCircumference = waistCircumference,
            onWaistCircumference = onWaistCircumference,
        )
        Spacer(modifier = Modifier.height(15.dp))
        ExposedDropdownMenuBox(
            expanded = isGoalExpanded,
            onExpandedChange = { isGoalExpanded = it },
        ) {
            OutlinedTextField(
                leadingIcon = {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.ic_goal_24dp
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                value = selectedGoal,
                onValueChange = {},
                label = {
                    Text(
                        text = stringResource(R.string.goal_label),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isGoalExpanded) },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
            )
            ExposedDropdownMenu(
                expanded = isGoalExpanded,
                onDismissRequest = { isGoalExpanded = false },
            ) {
                goals.forEach { selectedOption ->
                    DropdownMenuItem(
                        text = { Text(selectedOption) },
                        onClick = {
                            onGoalChange(selectedOption)
                            isGoalExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
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
            value = weightTarget,
            singleLine = true,
            onValueChange = onTargetWeightChange,
            placeholder = {
                Text(
                    text = stringResource(
                        R.string.target_weight_placeholder
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.target_weight_label),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            supportingText = {
                if (weightTarget.isNotEmpty() and !weightTarget.isFloatAndGreaterThanZero())
                    Text(
                        text = stringResource(R.string.target_weight_error_text),
                        style = MaterialTheme.typography.bodySmall,
                    )
                else if (isTargetWeightFocused)
                    Text(
                        text = stringResource(R.string.supporting_text_required),
                        style = MaterialTheme.typography.bodySmall,
                    )
            },
            isError = weightTarget.isNotEmpty() and !weightTarget.isFloatAndGreaterThanZero(),
            trailingIcon = {
                if (weightTarget.isNotEmpty() and !weightTarget.isFloatAndGreaterThanZero())
                    Icon(
                        painter = painterResource(id = R.drawable.ic_round_error_24dp),
                        contentDescription = null,
                    )
                else Text(text = stringResource(R.string.trailing_text_kg))
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    isTargetWeightFocused = it.isFocused
                },
        )
        ExposedDropdownMenuBox(
            expanded = isActivityLevelExpanded,
            onExpandedChange = { isActivityLevelExpanded = it },
        ) {
            OutlinedTextField(
                leadingIcon = {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.ic_activity_100dp
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                value = selectedActivityLevel,
                onValueChange = {},
                label = {
                    Text(
                        text = stringResource(R.string.activity_level_label),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isActivityLevelExpanded) },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
            )
            ExposedDropdownMenu(
                expanded = isActivityLevelExpanded,
                onDismissRequest = { isActivityLevelExpanded = false },
            ) {
                activityLevels.forEachIndexed { index, selectedOption ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    selectedOption,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    activityDescriptions[index],
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                HorizontalDivider()
                                Spacer(modifier = Modifier.height(2.dp))
                            }
                        },
                        onClick = {
                            onActivityLevelChange(selectedOption)
                            isActivityLevelExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }
}