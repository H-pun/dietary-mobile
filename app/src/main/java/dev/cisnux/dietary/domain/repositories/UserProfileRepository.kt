package dev.cisnux.dietary.domain.repositories

import dev.cisnux.dietary.domain.models.UserProfile
import dev.cisnux.dietary.domain.models.UserProfileDetail
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun addUserProfile(userProfile: UserProfile): Flow<UiState<Nothing>>
    fun getUserProfile(): Flow<UiState<Nothing>>
    fun updateUserProfile(userProfile: UserProfile): Flow<UiState<Nothing>>

    val userProfileDetail: Flow<UserProfileDetail>
    val isUserProfileExist: Flow<Boolean>
}
