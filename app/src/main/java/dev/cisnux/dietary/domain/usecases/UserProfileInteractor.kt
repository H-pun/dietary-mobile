package dev.cisnux.dietary.domain.usecases

import dev.cisnux.dietary.domain.models.UserProfile
import dev.cisnux.dietary.domain.models.UserProfileDetail
import dev.cisnux.dietary.domain.repositories.UserProfileRepository
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