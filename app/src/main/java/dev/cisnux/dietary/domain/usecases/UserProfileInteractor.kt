package dev.cisnux.dietary.domain.usecases

import dev.cisnux.dietary.domain.models.UserProfile
import dev.cisnux.dietary.domain.models.UserProfileDetail
import dev.cisnux.dietary.domain.repositories.UserProfileRepository
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserProfileInteractor @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) : UserProfileUseCase {
    override val isUserProfileExist: Flow<Boolean>
        get() = userProfileRepository.isUserProfileExist
    override val userProfileDetail: Flow<UserProfileDetail>
        get() = userProfileRepository.userProfileDetail

    override fun addUserProfile(userProfile: UserProfile): Flow<UiState<Nothing>> =
        userProfileRepository.addUserProfile(userProfile)

    override fun updateUserProfile(userProfile: UserProfile): Flow<UiState<Nothing>> =
        userProfileRepository.updateUserProfile(userProfile)

    override fun refreshUserProfile(): Flow<UiState<Nothing>> = userProfileRepository.getUserProfile()
}