package dev.cisnux.dietary.domain.usecases

import dev.cisnux.dietary.domain.models.UserNutrition
import dev.cisnux.dietary.domain.models.UserProfile
import dev.cisnux.dietary.domain.models.UserProfileDetail
import dev.cisnux.dietary.domain.repositories.UserProfileRepository
import dev.cisnux.dietary.utils.ACTIVITY_FACTOR
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import dev.cisnux.dietary.utils.Failure
import dev.cisnux.dietary.utils.GOALS_FACTOR
import dev.cisnux.dietary.utils.currentLocalDateTimeInBasicISOFormat
import kotlinx.coroutines.flow.combine
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class UserProfileInteractor @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val authenticationUseCase: AuthenticationUseCase
) : UserProfileUseCase {
    override val isUserProfileExist: Flow<Boolean>
        get() = authenticationUseCase.isAccessTokenAndUserIdExists.flatMapLatest {
            it?.let {
                userProfileRepository.getUserProfile(userId = it.first, accessToken = it.second)
                    .map { uiState ->
                        if (uiState is UiState.Error)
                            when (uiState.error) {
                                is Failure.NotFoundFailure -> false
                                else -> true
                            }
                        else true
                    }
            } ?: flow { emit(false) }
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)
    override val userProfileDetail: Flow<UserProfileDetail>
        get() = userProfileRepository.userProfileDetail
    override val userDailyNutrition: Flow<UiState<UserNutrition>>
        get() = authenticationUseCase.accessToken.combine(userProfileDetail) { accessToken, userProfile ->
            Pair(first = accessToken, userProfile)
        }.flatMapLatest {
            it.first?.let { accessToken ->
                userProfileRepository.getUserNutrition(
                    userId = it.second.userAccountId,
                    accessToken = accessToken,
                    date = Instant.now().currentLocalDateTimeInBasicISOFormat
                )
                    .map { uiState ->
                        if (uiState is UiState.Success) {
                            val height = it.second.height
                            val weight = it.second.weight
                            val age = it.second.age
                            val isWoman = it.second.gender.lowercase() == "wanita"
                            val bmr = if (!isWoman)
                                66 + (13.7 * weight) + (5 * height) - (6.8 * age)
                            else 655 + (9.6 * weight) + (1.8 * height) - (4.7 * age)
                            val activityFactor =
                                ACTIVITY_FACTOR[it.second.activityLevel.lowercase()] ?: 1.0
                            val goalsFactor = GOALS_FACTOR[it.second.goal] ?: 1.0
                            val stressFactor = 1.0
                            val userMaxDailyCalories =
                                (bmr * activityFactor * goalsFactor * stressFactor).toFloat()
                            val userMaxDailyProtein = 0.15f * userMaxDailyCalories
                            val userMaxDailyFat = 0.25f * userMaxDailyCalories
                            val userMaxDailyCarbohydrate = 0.6f * userMaxDailyCalories
                            val currentDataState = uiState.data!!

                            UiState.Success(
                                data = currentDataState.copy(
                                    maxDailyCalories = userMaxDailyCalories,
                                    maxDailyProtein = userMaxDailyProtein,
                                    maxDailyFat = userMaxDailyFat,
                                    maxDailyCarbohydrate = userMaxDailyCarbohydrate,
                                )
                            )
                        } else uiState
                    }
            } ?: flow { emit(UiState.Error(Failure.UnauthorizedFailure())) }
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)

    override fun addUserProfile(userProfile: UserProfile): Flow<UiState<Nothing>> =
        authenticationUseCase.isAccessTokenAndUserIdExists.flatMapLatest {
            it?.let {
                userProfileRepository.addUserProfile(
                    accessToken = it.second,
                    userId = it.first,
                    userProfile = userProfile
                )
            } ?: flow { emit(UiState.Error(Failure.UnauthorizedFailure())) }
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)


    override fun updateUserProfile(userProfile: UserProfile): Flow<UiState<Nothing>> =
        authenticationUseCase.accessToken.flatMapLatest {
            it?.let { id ->
                userProfileRepository.updateUserProfile(
                    accessToken = it,
                    userProfile = userProfile.copy(id = id)
                )
            } ?: flow { emit(UiState.Error(Failure.UnauthorizedFailure())) }
        }

    override fun refreshUserProfile(): Flow<UiState<Nothing>> =
        authenticationUseCase.isAccessTokenAndUserIdExists.flatMapLatest {
            it?.let {
                userProfileRepository.getUserProfile(userId = it.first, accessToken = it.second)
            } ?: flow { emit(UiState.Error(Failure.UnauthorizedFailure())) }
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)
}