package org.cisnux.mydietary.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import org.cisnux.mydietary.domain.models.UserAccount
import org.cisnux.mydietary.domain.usecases.AuthenticationUseCase
import org.cisnux.mydietary.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase
) : ViewModel() {
    private val _signUpWithEmailAndPasswordState: MutableStateFlow<UiState<Nothing>> =
        MutableStateFlow(UiState.Initialize)
    val signUpWithEmailAndPasswordState get() = _signUpWithEmailAndPasswordState.asStateFlow()
    private val _signUpWithGoogleState: MutableStateFlow<UiState<Nothing>> =
        MutableStateFlow(UiState.Initialize)
    val signUpWithGoogleState get() = _signUpWithGoogleState.asStateFlow()

    fun signUpWithEmailAndPassword(emailAddress: String, password: String) {
        val userAccount = UserAccount(emailAddress = emailAddress, password = password)
        viewModelScope.launch {
            authenticationUseCase.signUpWithEmailAndPassword(userAccount = userAccount)
                .collectLatest { uiState ->
                    _signUpWithEmailAndPasswordState.value = uiState
                }
        }
    }

    fun signUpWithGoogle() = viewModelScope.launch {
        authenticationUseCase.signUpWithGoogle()
            .collectLatest { uiState ->
                _signUpWithGoogleState.value = uiState
            }
    }
}