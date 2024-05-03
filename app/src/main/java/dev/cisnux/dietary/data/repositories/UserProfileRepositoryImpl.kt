package dev.cisnux.dietary.data.repositories

import androidx.collection.emptyIntSet
import dev.cisnux.dietary.data.locals.UserProfileLocalSource
import dev.cisnux.dietary.data.remotes.UserProfileRemoteSource
import dev.cisnux.dietary.data.remotes.bodyrequests.NewUserProfileBodyRequest
import dev.cisnux.dietary.data.remotes.bodyrequests.UpdateUserProfileBodyRequest
import dev.cisnux.dietary.domain.models.UserNutrition
import dev.cisnux.dietary.domain.models.UserProfile
import dev.cisnux.dietary.domain.models.UserProfileDetail
import dev.cisnux.dietary.domain.repositories.UserProfileRepository
import dev.cisnux.dietary.utils.UiState
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
                    age = userProfile.age
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
                    age = userProfile.age
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
    ): Flow<UiState<UserNutrition>> = flow<UiState<UserNutrition>> {
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
                emailAddress = it.emailAddress
            )
        }.flowOn(Dispatchers.IO)
            .distinctUntilChanged()
}