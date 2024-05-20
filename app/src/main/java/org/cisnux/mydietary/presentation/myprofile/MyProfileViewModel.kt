package org.cisnux.mydietary.presentation.myprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import org.cisnux.mydietary.domain.usecases.AuthenticationUseCase
import org.cisnux.mydietary.domain.usecases.UserProfileUseCase
import org.cisnux.mydietary.presentation.addmyprofile.MyProfile
import org.cisnux.mydietary.utils.UiState
import org.cisnux.mydietary.utils.asAddUserProfile
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase,
    private val useCase: UserProfileUseCase
) : ViewModel() {
    val userProfileDetail
        get() = useCase.getUserProfileDetail().shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
        )
    private val _updateMyProfileState: MutableStateFlow<UiState<Nothing>> =
        MutableStateFlow(UiState.Initialize)
    val updateMyProfileState get() = _updateMyProfileState.asStateFlow()
    private val refreshUserProfile = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val userProfileState = refreshUserProfile.asStateFlow().flatMapMerge {
        if (it)
            useCase.refreshUserProfile(scope = viewModelScope).also {
                refreshUserProfile.value = false
            }
        else flow { }
    }.shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed()
    )

    init {
        useCase.refreshUserProfile(scope = viewModelScope)
    }

    fun updateRefreshUserProfile(isRefresh: Boolean) {
        refreshUserProfile.value = isRefresh
    }

    fun updateMyProfile(id: String, myProfile: MyProfile) {
        val addUserProfile = myProfile.asAddUserProfile.copy(id = id)
        viewModelScope.launch {
            useCase.updateUserProfile(addUserProfile = addUserProfile, scope = viewModelScope)
                .collectLatest { uiState ->
                    _updateMyProfileState.value = uiState
                    if (uiState is UiState.Success)
                        refreshUserProfile.value = true
                }
        }
    }

    fun signOut() = viewModelScope.launch {
        authenticationUseCase.signOut()
    }
}