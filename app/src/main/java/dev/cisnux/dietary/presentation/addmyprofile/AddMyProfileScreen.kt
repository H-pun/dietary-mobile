package dev.cisnux.dietary.presentation.addmyprofile

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import dev.cisnux.dietary.R
import dev.cisnux.dietary.presentation.ui.theme.DietaryTheme
import dev.cisnux.dietary.presentation.utils.isAgeValid
import dev.cisnux.dietary.presentation.utils.isHeightOrWeightValid
import dev.cisnux.dietary.presentation.utils.isTargetWeightValid
import dev.cisnux.dietary.presentation.utils.isUsernameValid

@Composable
fun AddMyProfileScreen(
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val genders = stringArrayResource(id = R.array.gender)
    val goals = stringArrayResource(id = R.array.goal)
    val activityLevels = stringArrayResource(id = R.array.activity_level)
    val activityDescriptions = stringArrayResource(id = R.array.activity_description)
    val snackbarHostState = rememberSaveable {
        SnackbarHostState()
    }

    var myProfile by rememberSaveable {
        mutableStateOf(
            MyProfile(
                username = "",
                age = "",
                weight = "",
                height = "",
                gender = genders[0],
                goal = goals[0],
                targetWeight = "",
                activityLevel = activityLevels[0],
            )
        )
    }

    AddMyProfileContent(
        body = {
            MyProfileBody(
                username = myProfile.username,
                age = myProfile.age,
                weight = myProfile.weight,
                height = myProfile.height,
                selectedGender = myProfile.gender,
                selectedGoal = myProfile.goal,
                targetWeight = myProfile.targetWeight,
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
                    myProfile = myProfile.copy(targetWeight = newValue)
                },
                onActivityLevelChange = { newValue ->
                    myProfile = myProfile.copy(activityLevel = newValue)
                },
                onBuildProfile = {},
                modifier = modifier.padding(it),
            )
        },
        snackbarHostState = snackbarHostState
    )
}

@Preview(
    showBackground = true,
    name = "light",
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.NONE,
    device = "id:pixel_7_pro"
)
@Composable
private fun AddMyProfileContentPreview() {
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
                targetWeight = "",
                activityLevel = activityLevels[0],
            )
        )
    }


    DietaryTheme {
        AddMyProfileContent(
            body = {
                MyProfileBody(
                    username = myProfile.username,
                    age = myProfile.age,
                    weight = myProfile.weight,
                    height = myProfile.height,
                    selectedGender = myProfile.gender,
                    selectedGoal = myProfile.goal,
                    targetWeight = myProfile.targetWeight,
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
                        myProfile = myProfile.copy(targetWeight = newValue)
                    },
                    onActivityLevelChange = { newValue ->
                        myProfile = myProfile.copy(activityLevel = newValue)
                    },
                    onBuildProfile = {},
                    modifier = Modifier.padding(it),
                )
            },
            snackbarHostState = SnackbarHostState()
        )
    }
}

@Preview(
    showBackground = true,
    name = "dark and indonesia",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.NONE,
    device = "id:pixel_7_pro", locale = "in"
)
@Composable
private fun AddMyProfileContentDarkPreview() {
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
                targetWeight = "",
                activityLevel = activityLevels[0],
            )
        )
    }


    DietaryTheme {
        AddMyProfileContent(
            body = {
                MyProfileBody(
                    username = myProfile.username,
                    age = myProfile.age,
                    weight = myProfile.weight,
                    height = myProfile.height,
                    selectedGender = myProfile.gender,
                    selectedGoal = myProfile.goal,
                    targetWeight = myProfile.targetWeight,
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
                        myProfile = myProfile.copy(targetWeight = newValue)
                    },
                    onActivityLevelChange = { newValue ->
                        myProfile = myProfile.copy(activityLevel = newValue)
                    },
                    onBuildProfile = {},
                    modifier = Modifier.padding(it),
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
    wallpaper = Wallpapers.NONE,
    device = "id:pixel_7_pro", locale = "in"
)
@Composable
private fun AddMyProfileContentLoadingDarkPreview() {
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
                targetWeight = "",
                activityLevel = activityLevels[0],
            )
        )
    }


    DietaryTheme {
        AddMyProfileContent(
            body = {
                MyProfileBody(
                    username = myProfile.username,
                    age = myProfile.age,
                    weight = myProfile.weight,
                    height = myProfile.height,
                    selectedGender = myProfile.gender,
                    selectedGoal = myProfile.goal,
                    targetWeight = myProfile.targetWeight,
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
                        myProfile = myProfile.copy(targetWeight = newValue)
                    },
                    onActivityLevelChange = { newValue ->
                        myProfile = myProfile.copy(activityLevel = newValue)
                    },
                    onBuildProfile = {},
                    modifier = Modifier.padding(it),
                    isBuildProfileLoading = true
                )
            },
            snackbarHostState = SnackbarHostState()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyProfileBody(
    username: String,
    age: String,
    weight: String,
    height: String,
    selectedGender: String,
    selectedGoal: String,
    targetWeight: String,
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
    onTargetWeightChange: (String) -> Unit,
    onActivityLevelChange: (String) -> Unit,
    onBuildProfile: () -> Unit,
    modifier: Modifier = Modifier,
    isBuildProfileLoading: Boolean = false,
) {
    val scrollState = rememberScrollState()
    var isUsernameFocused by rememberSaveable {
        mutableStateOf(false)
    }
    var isAgeFocused by rememberSaveable {
        mutableStateOf(false)
    }
    var isWeightFocused by rememberSaveable {
        mutableStateOf(false)
    }
    var isHeightFocused by rememberSaveable {
        mutableStateOf(false)
    }
    var isTargetWeightFocused by rememberSaveable {
        mutableStateOf(false)
    }
    var isGenderExpanded by rememberSaveable { mutableStateOf(false) }
    var isGoalExpanded by rememberSaveable { mutableStateOf(false) }
    var isActivityLevelExpanded by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(state = scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.add_my_profile_label),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
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
                if (age.isNotEmpty() and !age.isAgeValid())
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
            isError = age.isNotEmpty() and !age.isAgeValid(),
            trailingIcon = {
                if (age.isNotEmpty() and !age.isAgeValid())
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
                if (weight.isNotEmpty() and !weight.isHeightOrWeightValid())
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
            isError = weight.isNotEmpty() and !weight.isHeightOrWeightValid(),
            trailingIcon = {
                if (weight.isNotEmpty() and !weight.isHeightOrWeightValid())
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
                if (height.isNotEmpty() and !height.isHeightOrWeightValid())
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
            isError = height.isNotEmpty() and !height.isHeightOrWeightValid(),
            trailingIcon = {
                if (height.isNotEmpty() and !height.isHeightOrWeightValid())
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
        AnimatedVisibility(visible = selectedGoal != goals[1]) {
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
                value = targetWeight,
                singleLine = true,
                onValueChange = onTargetWeightChange,
                placeholder = {
                    Text(
                        text = stringResource(R.string.target_weight_placeholder),
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
                    if (targetWeight.isNotEmpty() and !targetWeight.isTargetWeightValid())
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
                isError = targetWeight.isNotEmpty() and !targetWeight.isTargetWeightValid(),
                trailingIcon = {
                    if (targetWeight.isNotEmpty() and !targetWeight.isTargetWeightValid())
                        Icon(
                            painter = painterResource(id = R.drawable.ic_round_error_24dp),
                            contentDescription = null,
                        )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged {
                        isTargetWeightFocused = it.isFocused
                    },
            )
        }
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
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    activityDescriptions[index],
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Divider()
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
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onBuildProfile,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            enabled = username.isUsernameValid() and age.isAgeValid()
                    and weight.isHeightOrWeightValid() and height.isHeightOrWeightValid()
                    and targetWeight.isTargetWeightValid() and !isBuildProfileLoading,
        ) {
            if (isBuildProfileLoading)
                CircularProgressIndicator()
            else
                Text(text = stringResource(R.string.build_profile))
        }
    }
}

@Composable
private fun AddMyProfileContent(
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
