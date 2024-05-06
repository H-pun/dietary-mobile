package org.cisnux.mydietary.presentation.myprofile

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.valentinilk.shimmer.shimmer
import org.cisnux.mydietary.R
import org.cisnux.mydietary.domain.models.UserProfileDetail
import org.cisnux.mydietary.presentation.addmyprofile.MyProfile
import org.cisnux.mydietary.presentation.ui.components.BottomBar
import org.cisnux.mydietary.presentation.ui.components.ListTileProfile
import org.cisnux.mydietary.presentation.ui.components.MyProfileForm
import org.cisnux.mydietary.presentation.ui.theme.DietaryTheme
import org.cisnux.mydietary.presentation.ui.theme.placeholder
import org.cisnux.mydietary.utils.AppDestination
import org.cisnux.mydietary.utils.UiState
import org.cisnux.mydietary.utils.isIntAnswerValid
import org.cisnux.mydietary.utils.isHeightOrWeightValid
import org.cisnux.mydietary.utils.isFloatAnswerValid
import org.cisnux.mydietary.utils.isUsernameValid
import org.cisnux.mydietary.utils.Failure

@Composable
fun MyProfileScreen(
    navigateForBottomNav: (destination: AppDestination, currentRoute: AppDestination) -> Unit,
    navigateToSignIn: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyProfileViewModel = hiltViewModel()
) {
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
            emailAddress = "",
            age = 0,
            weight = 0f,
            height = 0f,
            gender = "",
            goal = "",
            weightTarget = 0f,
            activityLevel = "",
        )
    )
    val userProfileState by viewModel.userProfileState.collectAsState(initial = UiState.Initialize)
    val updateUserProfileState by viewModel.updateMyProfileState.collectAsState()
    var isUpdateMyProfileDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }
    var myProfile by rememberSaveable(
        userProfileDetail.username,
        userProfileDetail.emailAddress,
        userProfileDetail.age,
        userProfileDetail.weight,
        userProfileDetail.height,
        userProfileDetail.gender,
        userProfileDetail.goal,
        userProfileDetail.weightTarget,
        userProfileDetail.activityLevel,
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
                activityLevel = userProfileDetail.activityLevel
            )
        )
    }
    val genders = stringArrayResource(id = R.array.gender)
    val goals = stringArrayResource(id = R.array.goal)
    val activityLevels = stringArrayResource(id = R.array.activity_level)
    val activityDescriptions = stringArrayResource(id = R.array.activity_description)
    val context = LocalContext.current

    when {
        userProfileState is UiState.Error -> {
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
                            true
                        )
                    }
                }
                if (exception is Failure.UnauthorizedFailure) {
                    viewModel.signOut()
                    navigateToSignIn()
                }
            }
        }

        updateUserProfileState is UiState.Error -> {
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
                            myProfile = myProfile
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


    MyProfileContent(
        onSignOut = {
            viewModel.signOut()
            navigateToSignIn()
        },
        onSelectedDestination = navigateForBottomNav,
        body = {
            AnimatedVisibility(
                visible = userProfileState is UiState.Success && updateUserProfileState !is UiState.Loading ||
                        (userProfileState is UiState.Error && (userProfileState as UiState.Error).error is Failure.ConnectionFailure
                                && userProfileDetail.username.isNotBlank())
            ) {
                MyProfileBody(
                    username = userProfileDetail.username,
                    emailAddress = userProfileDetail.emailAddress,
                    age = userProfileDetail.age,
                    weight = String.format("%.2f", userProfileDetail.weight),
                    height = String.format("%.2f", userProfileDetail.height),
                    weightTarget = String.format("%.2f", userProfileDetail.weightTarget),
                    gender = userProfileDetail.gender,
                    goal = userProfileDetail.goal,
                    activityLevel = userProfileDetail.activityLevel,
                    onEdit = {
                        isUpdateMyProfileDialogOpen = true
                    },
                    isWeightTargetVisible = userProfileDetail.goal != goals[1],
                    modifier = modifier.padding(it)
                )
                UpdateMyProfileDialog(
                    onSave = {
                        isUpdateMyProfileDialogOpen = false
                        viewModel.updateMyProfile(myProfile = myProfile)
                    },
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
                        if (myProfile.goal == goals[1])
                            myProfile = myProfile.copy(weightTarget = 0f.toString())
                    },
                    onTargetWeightChange = { newValue ->
                        myProfile = myProfile.copy(weightTarget = newValue)
                    },
                    onActivityLevelChange = { newValue ->
                        myProfile = myProfile.copy(activityLevel = newValue)
                    },
                )
            }
            AnimatedVisibility(
                visible = userProfileState is UiState.Loading || updateUserProfileState is UiState.Loading ||
                        userProfileState is UiState.Error && (userProfileState as UiState.Error).error !is Failure.ConnectionFailure
                        || userProfileDetail.username.isBlank()
            ) {
                MyProfileShimmer(modifier = modifier.padding(it))
            }
        },
        shouldBottomBarOpen = true,
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
        emailAddress = "yagami12@gmail.com",
        age = 40,
        weight = 50f,
        height = 170f,
        gender = "Pria",
        goal = "Menurunkan berat badan",
        weightTarget = 10f,
        activityLevel = "Very Active",
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
                activityLevel = userProfileDetail.activityLevel
            )
        )
    }
    val genders = stringArrayResource(id = R.array.gender)
    val goals = stringArrayResource(id = R.array.goal)
    val activityLevels = stringArrayResource(id = R.array.activity_level)
    val activityDescriptions = stringArrayResource(id = R.array.activity_description)

    DietaryTheme {
        MyProfileContent(
            onSelectedDestination = { _, _ -> },
            body = {
                MyProfileBody(
                    username = userProfileDetail.username,
                    emailAddress = userProfileDetail.emailAddress,
                    age = userProfileDetail.age,
                    weight = String.format("%.2f", userProfileDetail.weight),
                    height = String.format("%.2f", userProfileDetail.height),
                    weightTarget = String.format("%.2f", userProfileDetail.weightTarget),
                    gender = userProfileDetail.gender,
                    goal = userProfileDetail.goal,
                    activityLevel = userProfileDetail.activityLevel,
                    onEdit = {
                        isUpdateMyProfileDialogOpen = true
                    },
                    modifier = Modifier.padding(it),
                    isWeightTargetVisible = userProfileDetail.weightTarget != 0f,
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
                        if (myProfile.goal == goals[1])
                            myProfile = myProfile.copy(weightTarget = "0")
                    },
                    onTargetWeightChange = { newValue ->
                        myProfile = myProfile.copy(weightTarget = newValue)
                    },
                    onActivityLevelChange = { newValue ->
                        myProfile = myProfile.copy(activityLevel = newValue)
                    },
                )
            },
            shouldBottomBarOpen = true,
            onSignOut = {},
            snackbarHostState = snackbarHostState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyProfileContent(
    onSelectedDestination: (destination: AppDestination, currentRoute: AppDestination) -> Unit,
    body: @Composable (innerPadding: PaddingValues) -> Unit,
    shouldBottomBarOpen: Boolean,
    onSignOut: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.my_profile_title),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                actions = {
                    IconButton(onClick = onSignOut) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_logout_24dp),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            AnimatedVisibility(visible = shouldBottomBarOpen) {
                BottomBar(
                    currentRoute = AppDestination.MyProfileRoute,
                    onSelectedDestination = onSelectedDestination
                )
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
    emailAddress: String,
    age: Int,
    weight: String,
    height: String,
    gender: String,
    goal: String,
    weightTarget: String,
    activityLevel: String,
    isWeightTargetVisible: Boolean,
    onEdit: () -> Unit,
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
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row {
                Box(contentAlignment = Alignment.Center) {
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    ) {}
                    Text(
                        text = username[0].uppercase(),
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.padding(start = 16.dp))
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = username,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = emailAddress,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(modifier = Modifier.padding(end = 9.dp)) {
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
                        text = stringResource(R.string.tahun, age),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
            )
            Spacer(modifier = Modifier.height(16.dp))
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
            Spacer(modifier = Modifier.height(16.dp))
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
            Spacer(modifier = Modifier.height(16.dp))
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
            Spacer(modifier = Modifier.height(16.dp))
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
            if (isWeightTargetVisible) {
                Spacer(modifier = Modifier.height(16.dp))
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
            }
            Spacer(modifier = Modifier.height(16.dp))
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
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, device = "spec:parent=pixel_5")
@Composable
private fun MyProfileShimmer(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 7.dp)
            .verticalScroll(state = scrollState),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row {
                Surface(
                    color = placeholder,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .shimmer()
                ) {}
                Spacer(modifier = Modifier.padding(start = 16.dp))
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Surface(
                        color = placeholder,
                        modifier = Modifier
                            .height(25.dp)
                            .width(150.dp)
                            .shimmer()
                    ) {}
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = placeholder,
                        modifier = Modifier
                            .height(25.dp)
                            .width(100.dp)
                            .shimmer()
                    ) {}
                }
            }
            Surface(
                color = placeholder,
                modifier = Modifier
                    .padding(top = 8.dp, end = 9.dp)
                    .size(24.dp)
                    .shimmer()
            ) {}
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(modifier = Modifier.padding(end = 9.dp)) {
            repeat(7) {
                Surface(
                    color = placeholder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(25.dp)
                        .shimmer()
                ) {}
                Spacer(modifier = Modifier.height(8.dp))
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
                activityLevel = "Very Active"
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
                                enabled = username.isUsernameValid() and age.isIntAnswerValid()
                                        and weight.isHeightOrWeightValid() and height.isHeightOrWeightValid()
                                        and weightTarget.isFloatAnswerValid(),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .verticalScroll(scrollState)
                )
            }
        }
}