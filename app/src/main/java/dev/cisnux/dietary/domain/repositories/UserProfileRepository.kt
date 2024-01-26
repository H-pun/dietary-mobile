package dev.cisnux.dietary.domain.repositories

import dev.cisnux.dietary.domain.models.UserProfile
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun addUserProfile(userProfile: UserProfile): Flow<UiState<Nothing>>

    val isUserProfileExist: Flow<Boolean>
}
