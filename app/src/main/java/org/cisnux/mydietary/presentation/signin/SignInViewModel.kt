package org.cisnux.mydietary.presentation.signin

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.cisnux.mydietary.domain.models.UserAccount
import org.cisnux.mydietary.domain.usecases.AuthenticationUseCase
import org.cisnux.mydietary.utils.UiState
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase,
) : ViewModel() {
    private val _signInWithEmailAndPasswordState: MutableStateFlow<UiState<Nothing>> =
        MutableStateFlow(UiState.Initialize)
    val signInWithEmailAndPasswordState get() = _signInWithEmailAndPasswordState.asStateFlow()
    private val _signInWithGoogleState: MutableStateFlow<UiState<Nothing>> =
        MutableStateFlow(UiState.Initialize)
    val signInWithGoogleState get() = _signInWithGoogleState.asStateFlow()
    val authenticationState = authenticationUseCase.getAuthenticationState(scope = viewModelScope)

    fun signInWithEmailAndPassword(emailAddress: String, password: String) {
        val userAccount = UserAccount(emailAddress = emailAddress, password = password)
        viewModelScope.launch {
            authenticationUseCase.signInWithEmailAndPassword(userAccount = userAccount, scope = viewModelScope)
                .collectLatest { uiState ->
                    _signInWithEmailAndPasswordState.value = uiState
                }
        }
    }

    fun signInWithGoogle(context: Activity) = viewModelScope.launch {
        authenticationUseCase.signInWithGoogle(scope = viewModelScope, context = context)
            .collectLatest { uiState ->
                _signInWithGoogleState.value = uiState
            }
    }

    fun clearAllStates() {
        _signInWithEmailAndPasswordState.value = UiState.Initialize
        _signInWithGoogleState.value = UiState.Initialize
    }
}