package dev.cisnux.dietary.data.locals

import dev.cisnux.dietary.UserProfile
import dev.cisnux.dietary.data.remotes.responses.UserProfileDetailResponse
import kotlinx.coroutines.flow.Flow

interface UserProfileLocalSource {
    val userProfile: Flow<UserProfile>
    suspend fun updateUserProfile(userProfileDetailResponse: UserProfileDetailResponse)
}