package org.cisnux.mydietary.data.repositories

import android.util.Log
import org.cisnux.mydietary.data.locals.UserProfileLocalSource
import org.cisnux.mydietary.data.remotes.UserProfileRemoteSource
import org.cisnux.mydietary.data.remotes.bodyrequests.NewUserProfileBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.UpdateUserProfileBodyRequest
import org.cisnux.mydietary.domain.models.UserNutrition
import org.cisnux.mydietary.domain.models.UserProfile
import org.cisnux.mydietary.domain.models.UserProfileDetail
import org.cisnux.mydietary.domain.repositories.UserProfileRepository
import org.cisnux.mydietary.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class UserProfileRepositoryImpl @Inject constructor(
    private val userProfileRemoteSource: UserProfileRemoteSource,
    private val userProfileLocalSource: UserProfileLocalSource,
) : UserProfileRepository {

    override fun addUserProfile(
        accessToken: String,
        userId: String,
        userProfile: UserProfile
    ): Flow<UiState<Nothing>> =
        flow {
            emit(UiState.Loading)
            userProfileRemoteSource.addUserProfile(
                accessToken = accessToken,
                userProfile = NewUserProfileBodyRequest(
                    userAccountId = userId,
                    username = userProfile.username,
                    activityLevel = userProfile.activityLevel,
                    goal = userProfile.goal,
                    weightTarget = userProfile.weightTarget,
                    gender = userProfile.gender,
                    height = userProfile.height,
                    weight = userProfile.weight,
                    age = userProfile.age,
                    waistCircumference = userProfile.waistCircumference
                )
            ).fold(
                ifLeft = { exception -> emit(UiState.Error(exception)) },
                ifRight = { emit(UiState.Success(null)) }
            )
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)

    override fun getUserProfile(accessToken: String, userId: String): Flow<UiState<Nothing>> =
        flow {
            emit(UiState.Loading)
            userProfileRemoteSource.getUserProfile(
                userAccountId = userId,
                accessToken = accessToken
            ).fold(
                ifLeft = { exception -> emit(UiState.Error(exception)) },
                ifRight = { userProfile ->
                    userProfileLocalSource.updateUserProfile(userProfile)
                    emit(UiState.Success(null))
                }
            )
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)

    override fun updateUserProfile(
        accessToken: String,
        userProfile: UserProfile
    ): Flow<UiState<Nothing>> =
        flow {
            emit(UiState.Loading)
            Log.d(UserProfileRepository::class.simpleName, "updateUserProfile: $userProfile")
            userProfileRemoteSource.updateUserProfile(
                accessToken = accessToken,
                userProfile = UpdateUserProfileBodyRequest(
                    id = userProfile.id,
                    username = userProfile.username,
                    activityLevel = userProfile.activityLevel,
                    goal = userProfile.goal,
                    weightTarget = userProfile.weightTarget,
                    gender = userProfile.gender,
                    height = userProfile.height,
                    weight = userProfile.weight,
                    age = userProfile.age,
                    waistCircumference = userProfile.waistCircumference
                )
            ).fold(
                ifLeft = { exception -> emit(UiState.Error(exception)) },
                ifRight = { emit(UiState.Success(null)) }
            )

        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)

    override fun getUserNutrition(
        accessToken: String,
        userId: String,
        date: String
    ): Flow<UiState<UserNutrition>> = flow {
        emit(UiState.Loading)
        userProfileRemoteSource.getDailyNutrients(
            accessToken = accessToken,
            userId = userId,
            date = date
        ).fold(
            ifLeft = { exception -> emit(UiState.Error(exception)) },
            ifRight = { nutrientResponse ->
                emit(
                    UiState.Success(
                        UserNutrition(
                            totalCaloriesToday = nutrientResponse.calories,
                            totalFatToday = nutrientResponse.fat,
                            totalCarbohydrateToday = nutrientResponse.carbohydrate,
                            totalProteinToday = nutrientResponse.protein
                        )
                    )
                )
            }
        )
    }.flowOn(Dispatchers.IO)
        .distinctUntilChanged()

    override val userProfileDetail: Flow<UserProfileDetail>
        get() = userProfileLocalSource.userProfile.map {
            UserProfileDetail(
                id = it.id,
                userAccountId = it.userAccountId,
                age = it.age,
                weight = it.weight,
                height = it.height,
                gender = it.gender,
                weightTarget = it.weightTarget,
                goal = it.goal,
                activityLevel = it.activityLevel,
                username = it.username,
                emailAddress = it.emailAddress,
                waistCircumference = it.waistCircumference
            )
        }.flowOn(Dispatchers.IO)
            .distinctUntilChanged()
}