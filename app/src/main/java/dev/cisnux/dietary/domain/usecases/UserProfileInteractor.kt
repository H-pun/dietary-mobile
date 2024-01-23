package dev.cisnux.dietary.domain.usecases

import dev.cisnux.dietary.domain.repositories.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserProfileInteractor @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) : UserProfileUseCase {
    override val isUserProfileExist: Flow<Boolean>
        get() = userProfileRepository.isUserProfileExist
}