package org.cisnux.mydietary.domain.repositories

import org.cisnux.mydietary.domain.models.UserNutrition
import org.cisnux.mydietary.domain.models.EditableUserProfile
import org.cisnux.mydietary.domain.models.UserProfileDetail
import org.cisnux.mydietary.utils.UiState
import kotlinx.coroutines.flow.Flow
import org.cisnux.mydietary.domain.models.DietProgress

interface UserProfileRepository {
    fun addUserProfile(
        accessToken: String,
        userId: String,
        editableUserProfile: EditableUserProfile
    ): Flow<UiState<Nothing>>

    fun getUserProfile(accessToken: String, userId: String): Flow<UiState<Nothing>>
    fun updateUserProfile(accessToken: String, userId: String, editableUserProfile: EditableUserProfile, isUsernameChanged: Boolean = false): Flow<UiState<Nothing>>
    fun getUserNutrition(accessToken: String, userId: String, date: String): Flow<UiState<UserNutrition>>
    fun getDietProgress(accessToken: String, userId: String): Flow<UiState<List<DietProgress>>>
    suspend fun deleteCurrentUserProfile()

    val userProfileDetail: Flow<UserProfileDetail>
}
