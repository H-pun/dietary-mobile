package org.cisnux.mydietary.presentation.addmyprofile

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.cisnux.mydietary.R
import org.cisnux.mydietary.presentation.ui.components.MyProfileForm
import org.cisnux.mydietary.presentation.ui.theme.DietaryTheme
import org.cisnux.mydietary.utils.AppDestination
import org.cisnux.mydietary.utils.Failure
import org.cisnux.mydietary.utils.isIntAnswerValid
import org.cisnux.mydietary.utils.isHeightOrWeightValid
import org.cisnux.mydietary.utils.isFloatAnswerValid
import org.cisnux.mydietary.utils.isUsernameValid
import org.cisnux.mydietary.utils.UiState

@Composable
fun AddMyProfileScreen(
    navigateToHome: (String) -> Unit,
    navigateToSignIn: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddMyProfileViewModel = hiltViewModel()
) {
    val genders = stringArrayResource(id = R.array.gender)
    val goals = stringArrayResource(id = R.array.goal)
    val activityLevels = stringArrayResource(id = R.array.activity_level)
    val activityDescriptions = stringArrayResource(id = R.array.activity_description)
    val snackbarHostState = remember {
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
                weightTarget = "",
                activityLevel = activityLevels[0],
            )
        )
    }
    val context = LocalContext.current
    val addMyProfileState by viewModel.addMyProfileState.collectAsState()

    when (addMyProfileState) {
        is UiState.Success -> {
            navigateToHome(AppDestination.AddMyProfileRoute.route)
        }

        is UiState.Error -> {
            (addMyProfileState as UiState.Error).error?.let { exception ->
                LaunchedEffect(snackbarHostState) {
                    exception.message?.let {
                        val snackbarResult = snackbarHostState.showSnackbar(
                            message = it,
                            actionLabel = context.getString(R.string.retry),
                            withDismissAction = true,
                            duration = SnackbarDuration.Long
                        )
                        if (snackbarResult == SnackbarResult.ActionPerformed)
                            viewModel.addMyProfile(myProfile)
                    }
                }
                if (exception is Failure.UnauthorizedFailure) {
                    viewModel.signOut()
                    navigateToSignIn(AppDestination.AddMyProfileRoute.route)
                }
            }
        }

        else -> {}
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
                onGoalChange = { newValue ->
                    myProfile = myProfile.copy(goal = newValue)
                    if (myProfile.goal == goals[1])
                        myProfile = myProfile.copy(weightTarget = "0")
                },
                onTargetWeightChange = { newValue ->
                    myProfile = myProfile.copy(weightTarget = newValue)
                },
                onActivityLevelChange = { newValue ->
                    myProfile = myProfile.copy(activityLevel = newValue)
                },
                onBuildProfile = { viewModel.addMyProfile(myProfile) },
                modifier = modifier.padding(it),
                isBuildProfileLoading = addMyProfileState is UiState.Loading
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
                weightTarget = "",
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
                weightTarget = "",
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
                weightTarget = "",
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
                    onBuildProfile = {},
                    modifier = Modifier.padding(it),
                    isBuildProfileLoading = true
                )
            },
            snackbarHostState = SnackbarHostState()
        )
    }
}

@Composable
private fun MyProfileBody(
    username: String,
    age: String,
    weight: String,
    height: String,
    selectedGender: String,
    selectedGoal: String,
    weightTarget: String,
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
        MyProfileForm(
            username = username,
            age = age,
            weight = weight,
            height = height,
            selectedGender = selectedGender,
            selectedGoal = selectedGoal,
            weightTarget = weightTarget,
            selectedActivityLevel = selectedActivityLevel,
            genders = genders,
            activityLevels = activityLevels,
            goals = goals,
            activityDescriptions = activityDescriptions,
            onUsernameChange = onUsernameChange,
            onAgeChange = onAgeChange,
            onHeightChange = onHeightChange,
            onWeightChange = onWeightChange,
            onGenderChange = onGenderChange,
            onGoalChange = onGoalChange,
            onTargetWeightChange = onTargetWeightChange,
            onActivityLevelChange = onActivityLevelChange
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onBuildProfile,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            enabled = username.isUsernameValid() and age.isIntAnswerValid()
                    and weight.isHeightOrWeightValid() and height.isHeightOrWeightValid()
                    and weightTarget.isFloatAnswerValid() and !isBuildProfileLoading,
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
