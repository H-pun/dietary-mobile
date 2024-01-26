package dev.cisnux.dietary.domain.usecases

import dev.cisnux.dietary.domain.models.UserProfile
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.flow.Flow

interface UserProfileUseCase {
    val isUserProfileExist: Flow<Boolean>
    fun addUserProfile(userProfile: UserProfile): Flow<UiState<Nothing>>
}