package org.cisnux.mydietary.presentation.myprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.cisnux.mydietary.domain.usecases.AuthenticationUseCase
import org.cisnux.mydietary.domain.usecases.UserProfileUseCase
import org.cisnux.mydietary.presentation.addmyprofile.MyProfile
import org.cisnux.mydietary.utils.UiState
import org.cisnux.mydietary.utils.asAddUserProfile
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase,
    private val userProfileUseCase: UserProfileUseCase
) : ViewModel() {
    val userProfileDetail
        get() = userProfileUseCase.getUserProfileDetail(viewModelScope).shareIn(
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
            userProfileUseCase.refreshUserProfile(scope = viewModelScope).also {
                refreshUserProfile.value = false
            }
        else flow { }
    }.shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed()
    )

    init {
        viewModelScope.launch {
            userProfileUseCase.refreshUserProfile(scope = viewModelScope).firstOrNull()
        }
    }

    fun updateRefreshUserProfile(isRefresh: Boolean) {
        refreshUserProfile.value = isRefresh
    }

    fun updateMyProfile(id: String, myProfile: MyProfile) {
        val addUserProfile = myProfile.asAddUserProfile.copy(id = id)
        viewModelScope.launch {
            userProfileUseCase.updateUserProfile(addUserProfile = addUserProfile, scope = viewModelScope)
                .collectLatest { uiState ->
                    _updateMyProfileState.value = uiState
                    if (uiState is UiState.Success)
                        refreshUserProfile.value = true
                }
        }
    }

    fun signOut() = CoroutineScope(context = SupervisorJob() + Dispatchers.IO).launch {
        authenticationUseCase.signOut()
    }
}