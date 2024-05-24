package org.cisnux.mydietary.data.repositories

import org.cisnux.mydietary.data.locals.UserProfileLocalSource
import org.cisnux.mydietary.data.remotes.UserProfileRemoteSource
import org.cisnux.mydietary.data.remotes.bodyrequests.NewUserProfileBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.UpdateUserProfileWithoutUsernameBodyRequest
import org.cisnux.mydietary.domain.models.UserNutrition
import org.cisnux.mydietary.domain.models.EditableUserProfile
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
import kotlinx.coroutines.withContext
import org.cisnux.mydietary.data.remotes.bodyrequests.DietProgressBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.UpdateUserProfileWithUsernameBodyRequest
import org.cisnux.mydietary.data.remotes.responses.UserProfileDetailResponse
import org.cisnux.mydietary.domain.models.DietProgress
import org.cisnux.mydietary.utils.currentLocalDateTime
import org.cisnux.mydietary.utils.fromIsoOffsetDateTimeToDayDateMonth

class UserProfileRepositoryImpl @Inject constructor(
    private val userProfileRemoteSource: UserProfileRemoteSource,
    private val userProfileLocalSource: UserProfileLocalSource,
) : UserProfileRepository {

    override fun addUserProfile(
        accessToken: String,
        userId: String,
        editableUserProfile: EditableUserProfile
    ): Flow<UiState<Nothing>> =
        flow {
            emit(UiState.Loading)
            userProfileRemoteSource.addUserProfile(
                accessToken = accessToken,
                userProfile = NewUserProfileBodyRequest(
                    userAccountId = userId,
                    username = editableUserProfile.username,
                    activityLevel = editableUserProfile.activityLevel,
                    goal = editableUserProfile.goal,
                    weightTarget = editableUserProfile.weightTarget,
                    gender = editableUserProfile.gender,
                    height = editableUserProfile.height,
                    weight = editableUserProfile.weight,
                    age = editableUserProfile.age,
                    waistCircumference = editableUserProfile.waistCircumference
                )
            ).fold(
                ifLeft = { exception -> emit(UiState.Error(exception)) },
                ifRight = { id ->
                    userProfileRemoteSource.updateDietProgress(
                        accessToken = accessToken,
                        dietProgressBodyRequest = DietProgressBodyRequest(
                            id = userId,
                            weight = editableUserProfile.weight,
                            waistCircumference = editableUserProfile.waistCircumference,
                            updatedAt = currentLocalDateTime
                        )
                    )
                    userProfileLocalSource.updateUserProfile(
                        UserProfileDetailResponse(
                            id = id,
                            userAccountId = userId,
                            username = editableUserProfile.username,
                            activityLevel = editableUserProfile.activityLevel,
                            goal = editableUserProfile.goal,
                            weightTarget = editableUserProfile.weightTarget,
                            waistCircumference = editableUserProfile.waistCircumference,
                            emailAddress = "",
                            weight = editableUserProfile.weight,
                            height = editableUserProfile.height,
                            gender = editableUserProfile.gender,
                            age = editableUserProfile.age
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
        editableUserProfile: EditableUserProfile,
        isUsernameChanged: Boolean
    ): Flow<UiState<Nothing>> =
        flow {
            emit(UiState.Loading)
            userProfileRemoteSource.updateUserProfile(
                accessToken = accessToken,
                userProfile = if (!isUsernameChanged)
                    UpdateUserProfileWithoutUsernameBodyRequest(
                        id = editableUserProfile.id,
                        activityLevel = editableUserProfile.activityLevel,
                        goal = editableUserProfile.goal,
                        weightTarget = editableUserProfile.weightTarget,
                        gender = editableUserProfile.gender,
                        height = editableUserProfile.height,
                        weight = editableUserProfile.weight,
                        age = editableUserProfile.age,
                        waistCircumference = editableUserProfile.waistCircumference
                    )
                else UpdateUserProfileWithUsernameBodyRequest(
                    id = editableUserProfile.id,
                    activityLevel = editableUserProfile.activityLevel,
                    goal = editableUserProfile.goal,
                    username = editableUserProfile.username,
                    weightTarget = editableUserProfile.weightTarget,
                    gender = editableUserProfile.gender,
                    height = editableUserProfile.height,
                    weight = editableUserProfile.weight,
                    age = editableUserProfile.age,
                    waistCircumference = editableUserProfile.waistCircumference,
                )
            ).fold(
                ifLeft = { exception -> emit(UiState.Error(exception)) },
                ifRight = {
                    userProfileRemoteSource.updateDietProgress(
                        accessToken = accessToken,
                        dietProgressBodyRequest = DietProgressBodyRequest(
                            id = userId,
                            weight = editableUserProfile.weight,
                            waistCircumference = editableUserProfile.waistCircumference,
                            updatedAt = currentLocalDateTime
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
                                description = dietProgressResponse.updatedAt.fromIsoOffsetDateTimeToDayDateMonth
                            )
                        }
                    )
                )
            }
        )
    }.flowOn(Dispatchers.IO)
        .distinctUntilChanged()

    override suspend fun deleteCurrentUserProfile() = withContext(Dispatchers.IO) {
        userProfileLocalSource.delete()
    }

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
                waistCircumference = it.waistCircumference,
                isVerified = it.isVerified
            )
        }.flowOn(Dispatchers.IO)
            .distinctUntilChanged()
}