package dev.cisnux.dietary.presentation.addmyprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cisnux.dietary.domain.usecases.UserProfileUseCase
import dev.cisnux.dietary.utils.asUserProfile
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMyProfileViewModel @Inject constructor(
    private val userProfileUseCase: UserProfileUseCase
) : ViewModel() {
    private val _addMyProfileState: MutableStateFlow<UiState<Nothing>> =
        MutableStateFlow(UiState.Initialize)
    val addMyProfileState get() = _addMyProfileState.asStateFlow()

    fun addMyProfile(myProfile: MyProfile) {
        val userProfile = myProfile.asUserProfile
        viewModelScope.launch {
            userProfileUseCase.addUserProfile(userProfile).collectLatest { uiState ->
                _addMyProfileState.value = uiState
            }
        }
    }
}