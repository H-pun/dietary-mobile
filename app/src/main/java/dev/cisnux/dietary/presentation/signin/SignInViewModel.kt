package dev.cisnux.dietary.presentation.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cisnux.dietary.domain.models.UserAccount
import dev.cisnux.dietary.domain.usecases.AuthenticationUseCase
import dev.cisnux.dietary.domain.usecases.UserProfileUseCase
import dev.cisnux.dietary.utils.AuthenticationState
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase,
    userProfileUseCase: UserProfileUseCase
) : ViewModel() {
    private val _signInWithEmailAndPasswordState: MutableStateFlow<UiState<Nothing>> =
        MutableStateFlow(UiState.Initialize)
    val signInWithEmailAndPasswordState get() = _signInWithEmailAndPasswordState.asStateFlow()
    private val _signInWithGoogleState: MutableStateFlow<UiState<Nothing>> =
        MutableStateFlow(UiState.Initialize)
    val signInWithGoogleState get() = _signInWithGoogleState.asStateFlow()
    val isUserProfileExist = userProfileUseCase.isUserProfileExist.shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
    )
    val authenticationState = authenticationUseCase.authenticationState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AuthenticationState.INITIALIZE
    )

    fun signInWithEmailAndPassword(emailAddress: String, password: String) {
        val userAccount = UserAccount(emailAddress = emailAddress, password = password)
        viewModelScope.launch {
            authenticationUseCase.signInWithEmailAndPassword(userAccount = userAccount)
                .collectLatest { uiState ->
                    _signInWithEmailAndPasswordState.value = uiState
                }
        }
    }

    fun signInWithGoogle() = viewModelScope.launch {
        authenticationUseCase.signInWithGoogle()
            .collectLatest { uiState ->
                _signInWithGoogleState.value = uiState
            }
    }

    fun clearAllStates() {
        _signInWithEmailAndPasswordState.value = UiState.Initialize
        _signInWithGoogleState.value = UiState.Initialize
    }
}