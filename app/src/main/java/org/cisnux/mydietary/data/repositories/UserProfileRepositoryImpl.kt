package org.cisnux.mydietary.data.repositories

import org.cisnux.mydietary.data.locals.UserProfileLocalSource
import org.cisnux.mydietary.data.remotes.UserProfileRemoteSource
import org.cisnux.mydietary.data.remotes.bodyrequests.NewUserProfileBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.UpdateUserProfileBodyRequest
import org.cisnux.mydietary.domain.models.UserNutrition
import org.cisnux.mydietary.domain.models.AddUserProfile
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
import org.cisnux.mydietary.data.remotes.bodyrequests.DietProgressBodyRequest
import org.cisnux.mydietary.data.remotes.responses.UserProfileDetailResponse
import org.cisnux.mydietary.domain.models.DietProgress
import org.cisnux.mydietary.utils.fromLocalDateTimeToDayDateAndMonth
import org.cisnux.mydietary.utils.getCurrentDateTimeInISOFormat

class UserProfileRepositoryImpl @Inject constructor(
    private val userProfileRemoteSource: UserProfileRemoteSource,
    private val userProfileLocalSource: UserProfileLocalSource,
) : UserProfileRepository {

    override fun addUserProfile(
        accessToken: String,
        userId: String,
        addUserProfile: AddUserProfile
    ): Flow<UiState<Nothing>> =
        flow {
            emit(UiState.Loading)
            userProfileRemoteSource.addUserProfile(
                accessToken = accessToken,
                userProfile = NewUserProfileBodyRequest(
                    userAccountId = userId,
                    username = addUserProfile.username,
                    activityLevel = addUserProfile.activityLevel,
                    goal = addUserProfile.goal,
                    weightTarget = addUserProfile.weightTarget,
                    gender = addUserProfile.gender,
                    height = addUserProfile.height,
                    weight = addUserProfile.weight,
                    age = addUserProfile.age,
                    waistCircumference = addUserProfile.waistCircumference
                )
            ).fold(
                ifLeft = { exception -> emit(UiState.Error(exception)) },
                ifRight = { id ->
                    userProfileRemoteSource.updateDietProgress(
                        accessToken = accessToken,
                        dietProgressBodyRequest = DietProgressBodyRequest(
                            id = userId,
                            weight = addUserProfile.weight,
                            waistCircumference = addUserProfile.waistCircumference,
                            updatedAt = getCurrentDateTimeInISOFormat()
                        )
                    )
                    userProfileLocalSource.updateUserProfile(
                        UserProfileDetailResponse(
                            id = id,
                            userAccountId = userId,
                            username = addUserProfile.username,
                            activityLevel = addUserProfile.activityLevel,
                            goal = addUserProfile.goal,
                            weightTarget = addUserProfile.weightTarget,
                            waistCircumference = addUserProfile.waistCircumference,
                            emailAddress = "",
                            weight = addUserProfile.weight,
                            height = addUserProfile.height,
                            gender = addUserProfile.gender,
                            age = addUserProfile.age
                        )
                    )
                    emit(UiState.Success(null))
                }
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
        userId: String,
        addUserProfile: AddUserProfile
    ): Flow<UiState<Nothing>> =
        flow {
            emit(UiState.Loading)
            userProfileRemoteSource.updateUserProfile(
                accessToken = accessToken,
                userProfile = UpdateUserProfileBodyRequest(
                    id = addUserProfile.id,
                    username = addUserProfile.username,
                    activityLevel = addUserProfile.activityLevel,
                    goal = addUserProfile.goal,
                    weightTarget = addUserProfile.weightTarget,
                    gender = addUserProfile.gender,
                    height = addUserProfile.height,
                    weight = addUserProfile.weight,
                    age = addUserProfile.age,
                    waistCircumference = addUserProfile.waistCircumference
                )
            ).fold(
                ifLeft = { exception -> emit(UiState.Error(exception)) },
                ifRight = {
                    userProfileRemoteSource.updateDietProgress(
                        accessToken = accessToken,
                        dietProgressBodyRequest = DietProgressBodyRequest(
                            id = userId,
                            weight = addUserProfile.weight,
                            waistCircumference = addUserProfile.waistCircumference,
                            updatedAt = getCurrentDateTimeInISOFormat()
                        )
                    )
                    emit(UiState.Success(null))
                }
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

    override fun getDietProgress(
        accessToken: String,
        userId: String
    ): Flow<UiState<List<DietProgress>>> = flow {
        emit(UiState.Loading)
        userProfileRemoteSource.getDietProgress(
            accessToken = accessToken,
            userAccountId = userId,
        ).fold(
            ifLeft = { exception -> emit(UiState.Error(exception)) },
            ifRight = {
                emit(
                    UiState.Success(
                        it.map { dietProgressResponse ->
                            DietProgress(
                                weight = dietProgressResponse.weight,
                                waistCircumference = dietProgressResponse.waistCircumference,
                                description = dietProgressResponse.updatedAt.fromLocalDateTimeToDayDateAndMonth
                            )
                        }
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