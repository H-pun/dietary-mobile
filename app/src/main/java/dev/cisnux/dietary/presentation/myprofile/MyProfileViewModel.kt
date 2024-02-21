package dev.cisnux.dietary.presentation.myprofile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cisnux.dietary.domain.models.UserProfileDetail
import dev.cisnux.dietary.domain.usecases.AuthenticationUseCase
import dev.cisnux.dietary.domain.usecases.UserProfileUseCase
import dev.cisnux.dietary.presentation.addmyprofile.MyProfile
import dev.cisnux.dietary.utils.UiState
import dev.cisnux.dietary.utils.asUserProfile
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase,
    private val useCase: UserProfileUseCase
) : ViewModel() {
    val userProfileDetail get() = useCase.userProfileDetail.shareIn(
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
            useCase.refreshUserProfile().also {
                refreshUserProfile.value = false
            }
        else flow { }
    }.shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed()
    )

    fun updateRefreshUserProfile(isRefresh: Boolean) {
        refreshUserProfile.value = isRefresh
    }

    fun updateMyProfile(myProfile: MyProfile) {
        val userProfile = myProfile.asUserProfile
        Log.d("userprofile", userProfile.toString())
        viewModelScope.launch {
            useCase.updateUserProfile(userProfile).collectLatest { uiState ->
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