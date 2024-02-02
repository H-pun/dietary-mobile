package dev.cisnux.dietary.data.repositories

import dev.cisnux.dietary.domain.models.UserProfile
import dev.cisnux.dietary.domain.models.UserProfileDetail
import dev.cisnux.dietary.domain.repositories.UserProfileRepository
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import dev.cisnux.dietary.utils.Failure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UserProfileRepositoryImpl @Inject constructor() : UserProfileRepository {
    private val userProfileDetailDummy = MutableStateFlow(
        UserProfileDetail(
            username = "cisnux12",
            emailAddress = "yagami12@gmail.com",
            age = 40,
            weight = 50f,
            height = 170f,
            gender = "Pria",
            goal = "Menurunkan berat badan",
            weightTarget = 10f,
            activityLevel = "Very Active",
            totalCaloriesToday = 200.4772f,
            bmiDailyCalorie = 400.7291f,
        )
    )

    override fun addUserProfile(userProfile: UserProfile): Flow<UiState<Nothing>> =
        flow {
            emit(UiState.Loading)
            delay(1000L)
//            emit(UiState.Error(error = Failure.BadRequestFailure(message = "username is not valid")))
            emit(UiState.Success())
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)

    override fun getUserProfile(): Flow<UiState<Nothing>> =
        flow {
            emit(UiState.Loading)
            delay(1000L)
//            emit(UiState.Error(error = Failure.NotFoundFailure("Userprofile not found")))
//            emit(UiState.Error(error = Failure.ConnectionFailure("No internet access")))
            emit(UiState.Success())
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)

    override fun updateUserProfile(userProfile: UserProfile): Flow<UiState<Nothing>> =
        flow {
            emit(UiState.Loading)
            delay(1000L)
//            emit(UiState.Error(error = Failure.BadRequestFailure(message = "username is not valid")))
            userProfileDetailDummy.value = userProfileDetailDummy.value.copy(
                username = userProfile.username,
                age = userProfile.age,
                weight = userProfile.weight,
                height = userProfile.height,
                gender = userProfile.gender,
                goal = userProfile.goal,
                weightTarget = userProfile.weightTarget,
                activityLevel = userProfile.activityLevel
            )
            emit(UiState.Success())
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)

    override val userProfileDetail: Flow<UserProfileDetail>
        get() = userProfileDetailDummy.asStateFlow()

    override val isUserProfileExist: Flow<Boolean>
        get() = flow {
            emit(true)
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)
}