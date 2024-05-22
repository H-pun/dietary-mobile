package org.cisnux.mydietary.presentation.addmyprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.cisnux.mydietary.domain.usecases.AuthenticationUseCase
import org.cisnux.mydietary.domain.usecases.UserProfileUseCase
import org.cisnux.mydietary.utils.asEditableUserProfile
import org.cisnux.mydietary.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMyProfileViewModel @Inject constructor(
    private val userProfileUseCase: UserProfileUseCase,
    private val authenticationUseCase: AuthenticationUseCase,
) : ViewModel() {
    private val _addMyProfileState: MutableStateFlow<UiState<Nothing>> =
        MutableStateFlow(UiState.Initialize)
    val addMyProfileState get() = _addMyProfileState.asStateFlow()

    fun addMyProfile(myProfile: MyProfile) {
        val addUserProfile = myProfile.asEditableUserProfile
        viewModelScope.launch {
            userProfileUseCase.addUserProfile(editableUserProfile = addUserProfile, scope = viewModelScope).collectLatest { uiState ->
                _addMyProfileState.value = uiState
            }
        }
    }

    fun signOut() = CoroutineScope(context = SupervisorJob() + Dispatchers.IO).launch {
        authenticationUseCase.signOut()
    }
}