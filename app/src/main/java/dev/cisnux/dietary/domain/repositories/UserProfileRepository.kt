package dev.cisnux.dietary.domain.repositories

import dev.cisnux.dietary.domain.models.UserProfile
import dev.cisnux.dietary.domain.models.UserProfileDetail
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun addUserProfile(
        accessToken: String,
        userId: String,
        userProfile: UserProfile
    ): Flow<UiState<Nothing>>

    fun getUserProfile(accessToken: String, userId: String): Flow<UiState<Nothing>>
    fun updateUserProfile(accessToken: String, userProfile: UserProfile): Flow<UiState<Nothing>>

    val userProfileDetail: Flow<UserProfileDetail>
}
