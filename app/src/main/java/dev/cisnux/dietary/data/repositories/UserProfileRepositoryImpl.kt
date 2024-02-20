package dev.cisnux.dietary.data.repositories

import android.util.Log
import dev.cisnux.dietary.data.locals.UserAccountLocalSource
import dev.cisnux.dietary.data.locals.UserProfileLocalSource
import dev.cisnux.dietary.data.remotes.UserProfileRemoteSource
import dev.cisnux.dietary.data.remotes.bodyrequests.NewUserProfileBodyRequest
import dev.cisnux.dietary.data.remotes.bodyrequests.UpdateUserProfileBodyRequest
import dev.cisnux.dietary.domain.models.UserProfile
import dev.cisnux.dietary.domain.models.UserProfileDetail
import dev.cisnux.dietary.domain.repositories.UserProfileRepository
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import dev.cisnux.dietary.utils.Failure
import kotlinx.coroutines.flow.map

class UserProfileRepositoryImpl @Inject constructor(
    private val userProfileRemoteSource: UserProfileRemoteSource,
    private val userAccountLocalSource: UserAccountLocalSource,
    private val userProfileLocalSource: UserProfileLocalSource,
) : UserProfileRepository {
    private val _userProfileDetail = MutableStateFlow(
        UserProfileDetail(
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

    override fun addUserProfile(userProfile: UserProfile): Flow<UiState<Nothing>> =
        channelFlow {
            send(UiState.Loading)
            delay(1000L)
            userAccountLocalSource.userId.combine(userAccountLocalSource.accessToken) { userId, accessToken ->
                Pair(userId, accessToken)
            }.collectLatest {
                if (it.first != null && it.first?.isNotBlank() == true && it.second != null && it.second?.isNotBlank() == true)
                    userProfileRemoteSource.addUserProfile(
                        accessToken = it.second!!,
                        userProfile = NewUserProfileBodyRequest(
                            userAccountId = it.first!!,
                            username = userProfile.username,
                            activityLevel = userProfile.activityLevel,
                            goal = userProfile.goal,
                            weightTarget = userProfile.weightTarget,
                            gender = userProfile.gender,
                            height = userProfile.height,
                            weight = userProfile.weight,
                            age = userProfile.age
                        )
                    ).fold(
                        ifLeft = { exception -> send(UiState.Error(exception)) },
                        ifRight = { send(UiState.Success(null)) }
                    )
            }
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)

    override fun getUserProfile(): Flow<UiState<Nothing>> =
        channelFlow {
            send(UiState.Loading)
            delay(1000L)
            userAccountLocalSource.userId.combine(userAccountLocalSource.accessToken) { userId, accessToken ->
                Pair(userId, accessToken)
            }.collectLatest {
                if (it.first != null && it.first?.isNotBlank() == true && it.second != null && it.second?.isNotBlank() == true)
                    userProfileRemoteSource.getUserProfile(
                        userAccountId = it.first!!,
                        accessToken = it.second!!
                    ).fold(
                        ifLeft = { exception -> send(UiState.Error(exception)) },
                        ifRight = { userProfile ->
                            Log.d(
                                UserProfileRepositoryImpl::class.simpleName,
                                userProfile.toString()
                            )
                            userProfileLocalSource.updateUserProfile(userProfile)
                            send(UiState.Success(null))
                        }
                    )
            }
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)

    override fun updateUserProfile(userProfile: UserProfile): Flow<UiState<Nothing>> =
        channelFlow {
            send(UiState.Loading)
            delay(1000L)
            userAccountLocalSource.accessToken.combine(userProfileLocalSource.userProfile) { accessToken, currentUserProfile ->
                Pair(accessToken, currentUserProfile)
            }.collectLatest {
                if (it.first != null && it.first?.isNotBlank() == true)
                    userProfileRemoteSource.updateUserProfile(
                        accessToken = it.first!!,
                        userProfile = UpdateUserProfileBodyRequest(
                            id = it.second.id,
                            username = userProfile.username,
                            activityLevel = userProfile.activityLevel,
                            goal = userProfile.goal,
                            weightTarget = userProfile.weightTarget,
                            gender = userProfile.gender,
                            height = userProfile.height,
                            weight = userProfile.weight,
                            age = userProfile.age
                        )
                    ).fold(
                        ifLeft = { exception -> send(UiState.Error(exception)) },
                        ifRight = { send(UiState.Success(null)) }
                    )
            }
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)

    override val userProfileDetail: Flow<UserProfileDetail>
        get() = userProfileLocalSource.userProfile.map {
            UserProfileDetail(
                age = it.age,
                weight = it.weight,
                height = it.height,
                gender = it.gender,
                weightTarget = it.weightTarget,
                goal = it.goal,
                activityLevel = it.activityLevel,
                username = it.username,
                emailAddress = it.emailAddress
            )
        }.flowOn(Dispatchers.IO).distinctUntilChanged()

    override val isUserProfileExist: Flow<Boolean>
        get() = channelFlow {
            userAccountLocalSource.userId.combine(userAccountLocalSource.accessToken) { userId, accessToken ->
                Pair(userId, accessToken)
            }.collectLatest {
                if (it.first != null && it.first?.isNotBlank() == true && it.second != null && it.second?.isNotBlank() == true) {
                    Log.d(UserProfileRepositoryImpl::class.simpleName, "running")
                    userProfileRemoteSource.getUserProfile(
                        userAccountId = it.first!!,
                        accessToken = it.second!!
                    ).fold(
                        ifLeft = { exception ->
                            if (exception is Failure.NotFoundFailure)
                                send(false)
                        },
                        ifRight = { userProfile ->
                            userProfileLocalSource.updateUserProfile(userProfile)
                            send(true)
                        }
                    )
                }
                else
                    send(false)
            }
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)
}