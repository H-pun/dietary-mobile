package org.cisnux.mydietary.presentation.securityaccount

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import org.cisnux.mydietary.domain.models.ChangePassword
import org.cisnux.mydietary.domain.usecases.AuthenticationUseCase
import org.cisnux.mydietary.domain.usecases.UserProfileUseCase
import org.cisnux.mydietary.presentation.widgets.ReportWidget
import org.cisnux.mydietary.utils.UiState
import javax.inject.Inject

@HiltViewModel
class SecurityAccountViewModel @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase,
    private val userProfileUseCase: UserProfileUseCase,
    @ApplicationContext private val context: Context
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
    val changeEmailState = _changeEmailState.asStateFlow()
    private val _changePasswordState = MutableStateFlow<UiState<String>>(UiState.Initialize)
    val changePasswordState = _changePasswordState.asStateFlow()
    private val _verifyEmailState = MutableStateFlow<UiState<String>>(UiState.Initialize)
    val verifyEmailState = _verifyEmailState.asStateFlow()

    init {
        viewModelScope.launch {
            userProfileUseCase.refreshUserProfile(scope = viewModelScope).firstOrNull()
        }
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

    fun signOut() = CoroutineScope(context = SupervisorJob() + Dispatchers.IO).launch {
        authenticationUseCase.signOut()
        ReportWidget().updateAll(context = context)
    }
}
