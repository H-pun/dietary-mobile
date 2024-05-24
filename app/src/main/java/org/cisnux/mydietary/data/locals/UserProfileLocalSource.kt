package org.cisnux.mydietary.data.locals

import org.cisnux.mydietary.UserProfile
import org.cisnux.mydietary.data.remotes.responses.UserProfileDetailResponse
import kotlinx.coroutines.flow.Flow

interface UserProfileLocalSource {
    val userProfile: Flow<UserProfile>
    suspend fun updateUserProfile(userProfileDetailResponse: UserProfileDetailResponse)
    suspend fun delete()
}