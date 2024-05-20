package org.cisnux.mydietary.presentation.securityaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import org.cisnux.mydietary.domain.models.ChangePassword
import org.cisnux.mydietary.domain.usecases.AuthenticationUseCase
import org.cisnux.mydietary.domain.usecases.UserProfileUseCase
import org.cisnux.mydietary.utils.UiState
import javax.inject.Inject

@HiltViewModel
class SecurityAccountViewModel @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase,
    private val userProfileUseCase: UserProfileUseCase,
) : ViewModel() {
    val emailAddress
        get() = userProfileUseCase.getUserProfileDetail().map { it.emailAddress }
            .shareIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed())
    val isVerified
        get() = userProfileUseCase.getUserProfileDetail().map { it.isVerified }
            .shareIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed())
    val userProfile
        get() = userProfileUseCase.getUserProfileDetail().shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed()
        )
    private val _changeEmailState = MutableStateFlow<UiState<String>>(UiState.Initialize)
    val changeEmailState = _changeEmailState.asSharedFlow()
    private val _changePasswordState = MutableStateFlow<UiState<String>>(UiState.Initialize)
    val changePasswordState = _changePasswordState.asSharedFlow()
    private val _verifyEmailState = MutableStateFlow<UiState<String>>(UiState.Initialize)
    val verifyEmailState = _verifyEmailState.asSharedFlow()

    init {
        userProfileUseCase.refreshUserProfile(scope = viewModelScope)
    }

    fun verifyEmail(email: String) = viewModelScope.launch {
        authenticationUseCase.verifyEmail(email = email, scope = viewModelScope)
            .distinctUntilChanged()
            .collectLatest {
                _verifyEmailState.value = it
            }
    }

    fun changeEmail(newEmail: String) = viewModelScope.launch {
        authenticationUseCase.changeEmail(newEmail = newEmail, scope = viewModelScope)
            .distinctUntilChanged()
            .collectLatest {
                _changeEmailState.value = it
            }
    }

    fun changePassword(oldPassword: String, newPassword: String) = viewModelScope.launch {
        authenticationUseCase.changePassword(
            changePassword = ChangePassword(
                oldPassword = oldPassword,
                newPassword = newPassword
            ),
            scope = viewModelScope
        )
            .distinctUntilChanged()
            .collectLatest {
                _changePasswordState.value = it
            }
    }

    fun signOut() = viewModelScope.launch {
        authenticationUseCase.signOut()
    }
}
