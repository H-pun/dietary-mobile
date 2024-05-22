package org.cisnux.mydietary.domain.usecases

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.cisnux.mydietary.domain.models.EditableUserProfile
import org.cisnux.mydietary.domain.models.UserProfileDetail
import org.cisnux.mydietary.utils.UiState
import kotlinx.coroutines.flow.Flow

interface UserProfileUseCase {
    fun getUserProfileDetail(scope: CoroutineScope = CoroutineScope(context = SupervisorJob() + Dispatchers.IO)): Flow<UserProfileDetail>
    fun addUserProfile(
        editableUserProfile: EditableUserProfile, scope: CoroutineScope
    ): Flow<UiState<Nothing>>

    fun updateUserProfile(
        newUserProfile: EditableUserProfile, oldUserProfileDetail: UserProfileDetail, scope: CoroutineScope,
    ): Flow<UiState<Nothing>>

    fun refreshUserProfile(scope: CoroutineScope): Flow<UiState<Nothing>>
}