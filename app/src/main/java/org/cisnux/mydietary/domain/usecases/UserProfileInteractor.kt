package org.cisnux.mydietary.domain.usecases

import kotlinx.coroutines.CoroutineScope
import org.cisnux.mydietary.domain.models.EditableUserProfile
import org.cisnux.mydietary.domain.models.UserProfileDetail
import org.cisnux.mydietary.domain.repositories.UserProfileRepository
import org.cisnux.mydietary.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class UserProfileInteractor @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val authenticationUseCase: AuthenticationUseCase
) : UserProfileUseCase {

    override fun getUserProfileDetail(scope: CoroutineScope): Flow<UserProfileDetail> =
        userProfileRepository.userProfileDetail
            .flowOn(Dispatchers.IO)
            .shareIn(scope = scope, started = SharingStarted.WhileSubscribed())

    override fun addUserProfile(
        editableUserProfile: EditableUserProfile,
        scope: CoroutineScope
    ): Flow<UiState<Nothing>> =
        authenticationUseCase.getAccessTokenAndUserId(scope = scope)
            .filterNotNull()
            .flatMapLatest {
                userProfileRepository.addUserProfile(
                    accessToken = it.second,
                    userId = it.first,
                    editableUserProfile = editableUserProfile
                )
                    .flowOn(Dispatchers.IO)
                    .stateIn(
                        scope = scope,
                        started = SharingStarted.Lazily,
                        initialValue = UiState.Initialize
                    )
            }
            .flowOn(Dispatchers.IO)
            .stateIn(
                scope = scope,
                started = SharingStarted.Lazily,
                initialValue = UiState.Initialize
            )


    override fun updateUserProfile(
        newUserProfile: EditableUserProfile,
        oldUserProfileDetail: UserProfileDetail,
        scope: CoroutineScope,
    ): Flow<UiState<Nothing>> =
        authenticationUseCase.getAccessTokenAndUserId(scope = scope)
            .filterNotNull()
            .flatMapLatest {
                userProfileRepository.updateUserProfile(
                    accessToken = it.second,
                    userId = it.first,
                    editableUserProfile = newUserProfile,
                    isUsernameChanged = newUserProfile.username.lowercase() != oldUserProfileDetail.username.lowercase()
                )
                    .flowOn(Dispatchers.IO)
                    .stateIn(
                        scope = scope,
                        started = SharingStarted.Lazily,
                        initialValue = UiState.Initialize
                    )
            }
            .flowOn(Dispatchers.IO)
            .stateIn(
                scope = scope,
                started = SharingStarted.Lazily,
                initialValue = UiState.Initialize
            )


    override fun refreshUserProfile(scope: CoroutineScope): Flow<UiState<Nothing>> =
        authenticationUseCase.getAccessTokenAndUserId(scope = scope)
            .filterNotNull()
            .flatMapLatest {
                userProfileRepository.getUserProfile(userId = it.first, accessToken = it.second)
                    .flowOn(Dispatchers.IO)
                    .stateIn(
                        scope = scope,
                        started = SharingStarted.Lazily,
                        initialValue = UiState.Initialize
                    )
            }
            .flowOn(Dispatchers.IO)
            .stateIn(
                scope = scope,
                started = SharingStarted.Lazily,
                initialValue = UiState.Initialize
            )
}
