package org.cisnux.mydietary.domain.usecases

import org.cisnux.mydietary.domain.models.UserNutrition
import org.cisnux.mydietary.domain.models.AddUserProfile
import org.cisnux.mydietary.domain.models.UserProfileDetail
import org.cisnux.mydietary.domain.repositories.UserProfileRepository
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
import org.cisnux.mydietary.utils.currentLocalDateTimeInBasicISOFormat
import kotlinx.coroutines.flow.combine
import org.cisnux.mydietary.domain.models.DietProgress
import org.cisnux.mydietary.utils.calculateMaxDailyNutrition
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
                            val userNutrition = uiState.data!!

                            UiState.Success(
                                data = userProfile.calculateMaxDailyNutrition(userNutrition = userNutrition)
                            )
                        } else uiState
                    }
            } ?: flow { emit(UiState.Error(Failure.UnauthorizedFailure())) }
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)

    override fun addUserProfile(addUserProfile: AddUserProfile): Flow<UiState<Nothing>> =
        authenticationUseCase.isAccessTokenAndUserIdExists.flatMapLatest {
            it?.let {
                userProfileRepository.addUserProfile(
                    accessToken = it.second,
                    userId = it.first,
                    addUserProfile = addUserProfile
                )
            } ?: flow { emit(UiState.Error(Failure.UnauthorizedFailure())) }
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)


    override fun updateUserProfile(addUserProfile: AddUserProfile): Flow<UiState<Nothing>> =
        authenticationUseCase.isAccessTokenAndUserIdExists.flatMapLatest {
            it?.let {
                userProfileRepository.updateUserProfile(
                    accessToken = it.second,
                    userId = it.first,
                    addUserProfile = addUserProfile
                )
            } ?: flow { emit(UiState.Error(Failure.UnauthorizedFailure())) }
        }

    override fun getDietProgress(): Flow<UiState<List<DietProgress>>> =
        authenticationUseCase.isAccessTokenAndUserIdExists.distinctUntilChanged().flatMapLatest {
            it?.let {
                userProfileRepository.getDietProgress(
                    accessToken = it.second,
                    userId = it.first
                ).distinctUntilChanged()
            } ?: flow { emit(UiState.Error(Failure.UnauthorizedFailure())) }.distinctUntilChanged()
        }.distinctUntilChanged()

    override fun refreshUserProfile(): Flow<UiState<Nothing>> =
        authenticationUseCase.isAccessTokenAndUserIdExists.flatMapLatest {
            it?.let {
                userProfileRepository.getUserProfile(userId = it.first, accessToken = it.second)
            } ?: flow { emit(UiState.Error(Failure.UnauthorizedFailure())) }
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)
}