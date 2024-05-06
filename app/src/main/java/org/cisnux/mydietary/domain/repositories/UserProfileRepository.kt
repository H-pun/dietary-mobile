package org.cisnux.mydietary.domain.repositories

import org.cisnux.mydietary.domain.models.UserNutrition
import org.cisnux.mydietary.domain.models.UserProfile
import org.cisnux.mydietary.domain.models.UserProfileDetail
import org.cisnux.mydietary.utils.UiState
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun addUserProfile(
        accessToken: String,
        userId: String,
        userProfile: UserProfile
    ): Flow<UiState<Nothing>>

    fun getUserProfile(accessToken: String, userId: String): Flow<UiState<Nothing>>
    fun updateUserProfile(accessToken: String, userProfile: UserProfile): Flow<UiState<Nothing>>
    fun getUserNutrition(accessToken: String, userId: String, date: String): Flow<UiState<UserNutrition>>

    val userProfileDetail: Flow<UserProfileDetail>
}
