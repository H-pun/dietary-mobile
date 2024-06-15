package org.cisnux.mydietary.presentation.myprofile

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.glance.appwidget.updateAll
import androidx.hilt.navigation.compose.hiltViewModel
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.launch
import org.cisnux.mydietary.R
import org.cisnux.mydietary.domain.models.UserProfileDetail
import org.cisnux.mydietary.presentation.addmyprofile.MyProfile
import org.cisnux.mydietary.presentation.ui.components.BottomBar
import org.cisnux.mydietary.presentation.ui.components.ListTileProfile
import org.cisnux.mydietary.presentation.ui.components.MyProfileForm
import org.cisnux.mydietary.presentation.ui.components.UserAccountCard
import org.cisnux.mydietary.presentation.ui.theme.DietaryTheme
import org.cisnux.mydietary.presentation.ui.theme.darkProgress
import org.cisnux.mydietary.presentation.ui.theme.lightProgress
import org.cisnux.mydietary.presentation.widgets.ReportWidget
import org.cisnux.mydietary.commons.utils.AppDestination
import org.cisnux.mydietary.commons.utils.UiState
import org.cisnux.mydietary.commons.utils.isFloatAndGreaterThanZero
import org.cisnux.mydietary.commons.utils.isFloatAndGreaterAndEqualToZero
import org.cisnux.mydietary.commons.utils.isUsernameValid
import org.cisnux.mydietary.commons.utils.Failure
import org.cisnux.mydietary.commons.utils.isAgeValid
import java.util.Locale

@Composable
fun MyProfileScreen(
    drawerNavigation: (destination: AppDestination, currentRoute: AppDestination) -> Unit,
    navigateToSignIn: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyProfileViewModel = hiltViewModel(),
    navigateUp: () -> Unit,
) {
    BackHandler {
        navigateUp()
    }

    val onRefresh by rememberUpdatedState(viewModel::updateRefreshUserProfile)

    LaunchedEffect(Unit) {
        onRefresh(true)
    }

    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val userProfileDetail by viewModel.userProfileDetail.collectAsState(
        initial = UserProfileDetail(
            id = "1",
            userAccountId = "1",
            username = "",
            age = 0,
            weight = 0f,
            height = 0f,
            gender = "",
            goal = "",
            weightTarget = 0f,
            activityLevel = "",
            waistCircumference = 0f,
            emailAddress = ""
        )
    )
    val userProfileState by viewModel.userProfileState.collectAsState(initial = UiState.Initialize)
    val updateUserProfileState by viewModel.updateMyProfileState.collectAsState()
    var isUpdateMyProfileDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }
    var myProfile by rememberSaveable(
        userProfileDetail.username,
        userProfileDetail.age,
        userProfileDetail.weight,
        userProfileDetail.height,
        userProfileDetail.gender,
        userProfileDetail.goal,
        userProfileDetail.weightTarget,
        userProfileDetail.activityLevel,
        userProfileDetail.waistCircumference
    ) {
        mutableStateOf(
            MyProfile(
                username = userProfileDetail.username,
                age = userProfileDetail.age.toString(),
                weight = userProfileDetail.weight.toString(),
                height = userProfileDetail.height.toString(),
                gender = userProfileDetail.gender,
                goal = userProfileDetail.goal,
                weightTarget = userProfileDetail.weightTarget.toString(),
                activityLevel = userProfileDetail.activityLevel,
                waistCircumference = userProfileDetail.waistCircumference.toString()
            )
        )
    }
    val genders = stringArrayResource(id = R.array.gender)
    val goals = stringArrayResource(id = R.array.goal)
    val activityLevels = stringArrayResource(id = R.array.activity_level)
    val activityDescriptions = stringArrayResource(id = R.array.activity_description)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    if (userProfileState is UiState.Error)
        (userProfileState as UiState.Error).error?.let { exception ->
            LaunchedEffect(snackbarHostState) {
                exception.message?.let {
                    val snackbarResult = snackbarHostState.showSnackbar(
                        message = it,
                        actionLabel = context.getString(R.string.retry),
                        withDismissAction = true,
                        duration = SnackbarDuration.Long
                    )
                    if (snackbarResult == SnackbarResult.ActionPerformed) viewModel.updateRefreshUserProfile(
                        isRefresh = true
                    )
                }
            }
            if (exception is Failure.UnauthorizedFailure) {
                viewModel.signOut()
                navigateToSignIn()
            }
        }

    when (updateUserProfileState) {
        is UiState.Error -> {
            (updateUserProfileState as UiState.Error).error?.let { exception ->
                LaunchedEffect(snackbarHostState) {
                    exception.message?.let {
                        val snackbarResult = snackbarHostState.showSnackbar(
                            message = it,
                            actionLabel = context.getString(R.string.retry),
                            withDismissAction = true,
                            duration = SnackbarDuration.Long
                        )
                        if (snackbarResult == SnackbarResult.ActionPerformed) viewModel.updateMyProfile(
                            id = userProfileDetail.id,
                            myProfile = myProfile,
                            oldUserProfileDetail = userProfileDetail
                        )
                    }
                }
                if (exception is Failure.UnauthorizedFailure) {
                    viewModel.signOut()
                    navigateToSignIn()
                }
            }
        }

        is UiState.Success -> {
            LaunchedEffect(updateUserProfileState) {
                coroutineScope.launch {
                    ReportWidget().updateAll(context)
                }
            }
        }

        else -> {}
    }

    MyProfileContent(
        signOut = {
            viewModel.signOut()
            navigateToSignIn()
        },
        drawerTitle = {
            UserAccountCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                username = userProfileDetail.username.ifBlank { null },
                email = userProfileDetail.emailAddress.ifBlank { null },
                isVerified = userProfileDetail.isVerified
            )
        },
        onEdit = {
            isUpdateMyProfileDialogOpen = true
        },
        isSuccess = userProfileState is UiState.Success,
        onSelectedDestination = drawerNavigation,
        body = {
            AnimatedVisibility(
                visible = userProfileState is UiState.Success ||
                        (userProfileState is UiState.Error && (userProfileState as UiState.Error).error is Failure.ConnectionFailure
                                && userProfileDetail.username.isNotBlank())
            ) {
                val locale = Locale.getDefault()
                MyProfileBody(
                    username = userProfileDetail.username,
                    age = userProfileDetail.age,
                    weight = String.format(locale, "%.2f", userProfileDetail.weight),
                    height = String.format(locale, "%.2f", userProfileDetail.height),
                    weightTarget = String.format(locale, "%.2f", userProfileDetail.weightTarget),
                    gender = userProfileDetail.gender,
                    goal = userProfileDetail.goal,
                    activityLevel = userProfileDetail.activityLevel,
                    waistCircumference = String.format(
                        locale,
                        "%.2f",
                        userProfileDetail.waistCircumference
                    ),
                    modifier = modifier.padding(it)
                )
                UpdateMyProfileDialog(
                    onSave = {
                        isUpdateMyProfileDialogOpen = false
                        viewModel.updateMyProfile(
                            id = userProfileDetail.id,
                            myProfile = myProfile,
                            oldUserProfileDetail = userProfileDetail
                        )
                    },
                    onCancel = {
                        isUpdateMyProfileDialogOpen = false
                        myProfile = myProfile.copy(
                            username = userProfileDetail.username,
                            age = userProfileDetail.age.toString(),
                            weight = userProfileDetail.weight.toString(),
                            height = userProfileDetail.height.toString(),
                            gender = userProfileDetail.gender,
                            goal = userProfileDetail.goal,
                            weightTarget = userProfileDetail.weightTarget.toString(),
                            activityLevel = userProfileDetail.activityLevel,
                            waistCircumference = userProfileDetail.waistCircumference.toString()
                        )
                    },
                    onDismissRequest = { isUpdateMyProfileDialogOpen = false },
                    isDialogOpen = isUpdateMyProfileDialogOpen,
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
                    onHeightChange = { newValue ->
                        myProfile = myProfile.copy(height = newValue)
                    },
                    onWeightChange = { newValue ->
                        myProfile = myProfile.copy(weight = newValue)
                    },
                    onGenderChange = { newValue ->
                        myProfile = myProfile.copy(gender = newValue)
                    },
                    onGoalChange = { newValue ->
                        myProfile = myProfile.copy(goal = newValue)
                    },
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
            AnimatedVisibility(
                visible = userProfileState is UiState.Loading ||
                        userProfileState is UiState.Error && (userProfileState as UiState.Error).error !is Failure.ConnectionFailure
                        || userProfileDetail.username.isBlank()
            ) {
                MyProfileShimmer(modifier = modifier.padding(it).semantics {
                    testTag = "my_profile_shimmer"
                })
            }
        },
        snackbarHostState = snackbarHostState
    )
}

@Preview(showBackground = true)
@Composable
private fun MyProfileContentPreview() {
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val userProfileDetail = UserProfileDetail(
        id = "1",
        userAccountId = "1",
        username = "yagamijaeger",
        age = 40,
        weight = 50f,
        height = 170f,
        gender = "Pria",
        goal = "Menurunkan berat badan",
        weightTarget = 10f,
        activityLevel = "Very Active",
        waistCircumference = 40.2f,
        emailAddress = ""
    )
    var isUpdateMyProfileDialogOpen by remember {
        mutableStateOf(false)
    }
    var myProfile by rememberSaveable {
        mutableStateOf(
            MyProfile(
                username = userProfileDetail.username,
                age = userProfileDetail.age.toString(),
                weight = userProfileDetail.weight.toString(),
                height = userProfileDetail.height.toString(),
                gender = userProfileDetail.gender,
                goal = userProfileDetail.goal,
                weightTarget = userProfileDetail.weightTarget.toString(),
                activityLevel = userProfileDetail.activityLevel,
                waistCircumference = userProfileDetail.waistCircumference.toString()
            )
        )
    }
    val genders = stringArrayResource(id = R.array.gender)
    val goals = stringArrayResource(id = R.array.goal)
    val activityLevels = stringArrayResource(id = R.array.activity_level)
    val activityDescriptions = stringArrayResource(id = R.array.activity_description)

    DietaryTheme {
        MyProfileContent(
            onEdit = {
                isUpdateMyProfileDialogOpen = true
            },
            onSelectedDestination = { _, _ -> },
            body = {
                val locale = Locale.getDefault()
                MyProfileBody(
                    username = userProfileDetail.username,
                    age = userProfileDetail.age,
                    weight = String.format(locale, "%.2f", userProfileDetail.weight),
                    height = String.format(locale, "%.2f", userProfileDetail.height),
                    weightTarget = String.format(locale, "%.2f", userProfileDetail.weightTarget),
                    gender = userProfileDetail.gender,
                    goal = userProfileDetail.goal,
                    activityLevel = userProfileDetail.activityLevel,
                    waistCircumference = String.format(
                        locale,
                        "%.2f",
                        userProfileDetail.waistCircumference
                    ),
                    modifier = Modifier.padding(it),
                )
                UpdateMyProfileDialog(
                    onSave = { isUpdateMyProfileDialogOpen = false },
                    onCancel = { isUpdateMyProfileDialogOpen = false },
                    onDismissRequest = { isUpdateMyProfileDialogOpen = false },
                    isDialogOpen = isUpdateMyProfileDialogOpen,
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
                    },
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
            },
            snackbarHostState = snackbarHostState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyProfileContent(
    onSelectedDestination: (destination: AppDestination, currentRoute: AppDestination) -> Unit,
    body: @Composable (innerPadding: PaddingValues) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onEdit: () -> Unit = {},
    drawerTitle: @Composable ColumnScope.() -> Unit = {},
    signOut: () -> Unit = {},
    isSuccess: Boolean = true
) {
    Scaffold(
        bottomBar = {
            BottomBar(
                currentRoute = AppDestination.MyProfileRoute,
                onSelectedDestination = onSelectedDestination
            )
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.my_profile_title),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            if (isSuccess)
                FloatingActionButton(onClick = onEdit, modifier = Modifier.testTag(tag = "edit_profile_button")) {
                    Icon(imageVector = Icons.Rounded.Edit, contentDescription = null)
                }
        },
        modifier = modifier
    ) { innerPadding ->
        body(innerPadding)
    }
}

@Composable
private fun MyProfileBody(
    username: String,
    age: Int,
    weight: String,
    height: String,
    waistCircumference: String,
    gender: String,
    goal: String,
    weightTarget: String,
    activityLevel: String,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 7.dp)
            .verticalScroll(state = scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.padding(end = 9.dp)) {
            ListTileProfile(
                icon = {
                    Icon(imageVector = Icons.Rounded.AccountCircle, contentDescription = null)
                },
                label = {
                    Text(
                        text = stringResource(id = R.string.username_label),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                },
                bodyLabel = {
                    Text(
                        text = username,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
            )
            Spacer(modifier = Modifier.height(12.dp))
            ListTileProfile(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_age_100dp),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = R.string.age_label),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                },
                bodyLabel = {
                    Text(
                        text = stringResource(R.string.year, age),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
            )
            Spacer(modifier = Modifier.height(12.dp))
            ListTileProfile(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_height_100dp),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = R.string.height_label),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                },
                bodyLabel = {
                    Text(
                        text = stringResource(R.string.cm, height),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            ListTileProfile(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_scale_100dp),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = R.string.weight_label),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                },
                bodyLabel = {
                    Text(
                        text = stringResource(R.string.kg, weight),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            ListTileProfile(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_waist_100dp),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = R.string.waist_circumference_label),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                },
                bodyLabel = {
                    Text(
                        text = stringResource(R.string.cm, waistCircumference),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            ListTileProfile(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_male_100dp),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = R.string.gender_label),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                },
                bodyLabel = {
                    Text(
                        text = gender,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            ListTileProfile(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_goal_24dp),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = R.string.goal_label),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                },
                bodyLabel = {
                    Text(
                        text = goal,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            ListTileProfile(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_scale_100dp),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = R.string.target_weight_label),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                },
                bodyLabel = {
                    Text(
                        text = stringResource(id = R.string.kg, weightTarget),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            ListTileProfile(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_activity_100dp),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = R.string.activity_level_label),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                },
                bodyLabel = {
                    Text(
                        text = activityLevel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:parent=pixel_5")
@Composable
private fun MyProfileShimmer(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val placeholder = if (!isSystemInDarkTheme()) lightProgress
    else darkProgress

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 7.dp)
            .verticalScroll(state = scrollState),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.padding(end = 9.dp)) {
            repeat(8) {
                Surface(
                    color = placeholder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(25.dp)
                        .shimmer()
                ) {}
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(thickness = 1.5.dp)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:parent=pixel_5,orientation=landscape")
@Composable
private fun UpdateMyProfileDialogPreview() {
    val genders = stringArrayResource(id = R.array.gender)
    val goals = stringArrayResource(id = R.array.goal)
    val activityLevels = stringArrayResource(id = R.array.activity_level)
    val activityDescriptions = stringArrayResource(id = R.array.activity_description)

    var myProfile by rememberSaveable {
        mutableStateOf(
            MyProfile(
                username = "yagamijaeger",
                age = "40",
                weight = "50",
                height = "170",
                gender = "Man",
                goal = "Weight Loss",
                weightTarget = "10",
                activityLevel = "Very Active",
                waistCircumference = "40.2"
            )
        )
    }

    DietaryTheme {
        UpdateMyProfileDialog(
            onSave = { /*TODO*/ },
            onCancel = { /*TODO*/ },
            onDismissRequest = {},
            isDialogOpen = true,
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
fun UpdateMyProfileDialog(
    onSave: () -> Unit,
    onCancel: () -> Unit,
    isDialogOpen: Boolean,
    onDismissRequest: () -> Unit,
    username: String,
    age: String,
    weight: String,
    height: String,
    selectedGender: String,
    selectedGoal: String,
    waistCircumference: String,
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
    onWaistCircumference: (String) -> Unit,
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
                                text = stringResource(id = R.string.my_profile_title),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        actions = {
                            TextButton(
                                onClick = onSave,
                                enabled = username.isUsernameValid() and age.isAgeValid()
                                        and weight.isFloatAndGreaterThanZero() and height.isFloatAndGreaterThanZero()
                                        and weightTarget.isFloatAndGreaterAndEqualToZero(),
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
                    onActivityLevelChange = onActivityLevelChange,
                    waistCircumference = waistCircumference,
                    onWaistCircumference = onWaistCircumference,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .verticalScroll(scrollState)
                )
            }
        }
}