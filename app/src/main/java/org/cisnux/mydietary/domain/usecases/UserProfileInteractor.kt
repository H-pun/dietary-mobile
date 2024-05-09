package org.cisnux.mydietary.domain.usecases

import org.cisnux.mydietary.domain.models.UserNutrition
import org.cisnux.mydietary.domain.models.UserProfile
import org.cisnux.mydietary.domain.models.UserProfileDetail
import org.cisnux.mydietary.domain.repositories.UserProfileRepository
import org.cisnux.mydietary.utils.ACTIVITY_FACTOR
import org.cisnux.mydietary.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import org.cisnux.mydietary.utils.Failure
import org.cisnux.mydietary.utils.GOALS_FACTOR
import org.cisnux.mydietary.utils.currentLocalDateTimeInBasicISOFormat
import kotlinx.coroutines.flow.combine
import org.cisnux.mydietary.domain.models.DietProgress
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class UserProfileInteractor @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val authenticationUseCase: AuthenticationUseCase
) : UserProfileUseCase {
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
                            // user profile
                            val userProfile = it.second
                            val height = userProfile.height // tinggi badan
                            val weight = userProfile.weight // berat badan
                            val age = userProfile.age // umur
                            val isWoman =
                                userProfile.gender.lowercase() == "wanita" // menentukan pria atau wanita
                            // menghitung bmr berdasarkan tinggi, berat badan, umur dan jenis kelamin
                            val bmr = if (!isWoman)
                                66 + (13.7 * weight) + (5 * height) - (6.8 * age)
                            else 655 + (9.6 * weight) + (1.8 * height) - (4.7 * age)
                            // mengambil nilai faktor level aktivitas
                            val activityFactor =
                                ACTIVITY_FACTOR[userProfile.activityLevel.lowercase()] ?: 1.0
                            // mengambil nilai faktor goals
                            val goalsFactor = GOALS_FACTOR[userProfile.goal] ?: 1.0
                            // nilai faktor stres
                            val stressFactor = 1.0
                            // menghitung maksimal kalori
                            val userMaxDailyCalories =
                                (bmr * activityFactor * goalsFactor * stressFactor).toFloat()
                            // menghitung maksimal protein (15% dari maksimal kalori_
                            val userMaxDailyProtein = 0.15f * userMaxDailyCalories
                            // menghitung maksimal lemak (25% dari maksimal kalori)
                            val userMaxDailyFat = 0.25f * userMaxDailyCalories
                            // menghitung maksimal karbohidrat (60% dari maksimal kalori)
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
        authenticationUseCase.isAccessTokenAndUserIdExists.flatMapLatest {
            it?.let {
                userProfileRepository.updateUserProfile(
                    accessToken = it.second,
                    userId = it.first,
                    userProfile = userProfile
                )
            } ?: flow { emit(UiState.Error(Failure.UnauthorizedFailure())) }
        }

    override fun getDietProgress(): Flow<UiState<List<DietProgress>>> =
        authenticationUseCase.isAccessTokenAndUserIdExists.flatMapLatest {
            it?.let {
                userProfileRepository.getDietProgress(
                    accessToken = it.second,
                    userId = it.first
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