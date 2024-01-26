package dev.cisnux.dietary.domain.usecases

import dev.cisnux.dietary.domain.models.UserProfile
import dev.cisnux.dietary.domain.repositories.UserProfileRepository
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserProfileInteractor @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) : UserProfileUseCase {
    override val isUserProfileExist: Flow<Boolean>
        get() = userProfileRepository.isUserProfileExist

    override fun addUserProfile(userProfile: UserProfile): Flow<UiState<Nothing>> = userProfileRepository.addUserProfile(userProfile)
}