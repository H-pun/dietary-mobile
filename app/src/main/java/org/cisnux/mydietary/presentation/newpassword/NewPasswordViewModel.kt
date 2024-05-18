package org.cisnux.mydietary.presentation.newpassword

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.cisnux.mydietary.domain.models.ForgotPassword
import org.cisnux.mydietary.domain.usecases.AuthenticationUseCase
import org.cisnux.mydietary.utils.UiState
import javax.inject.Inject

@HiltViewModel
class NewPasswordViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authenticationUseCase: AuthenticationUseCase
) : ViewModel() {
    val code: String = checkNotNull(savedStateHandle["code"]) as String
    val emailAddress: String = checkNotNull(savedStateHandle["emailAddress"]) as String
    private val _changePasswordState = MutableStateFlow<UiState<String>>(UiState.Initialize)
    val changePasswordState = _changePasswordState.asStateFlow()

    init {
        Log.d(NewPasswordViewModel::class.simpleName, emailAddress)
        Log.d(NewPasswordViewModel::class.simpleName, code)
    }

    fun changePassword(newPassword: String) =
        viewModelScope.launch {
            authenticationUseCase.forgotPassword(
                forgotPassword = ForgotPassword(
                    code = code,
                    emailAddress = emailAddress,
                    newPassword = newPassword
                )
            )
                .collectLatest {
                    _changePasswordState.value = it
                }
        }
}
