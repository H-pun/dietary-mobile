package org.cisnux.mydietary.presentation.verifycode

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.cisnux.mydietary.domain.usecases.AuthenticationUseCase
import org.cisnux.mydietary.commons.utils.UiState
import javax.inject.Inject

@HiltViewModel
class VerifyCodeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authenticationUseCase: AuthenticationUseCase
) : ViewModel() {
    private val _resetPasswordState: MutableStateFlow<UiState<String>> =
        MutableStateFlow(UiState.Initialize)
    val resetPasswordState get() = _resetPasswordState.asStateFlow()
    val emailAddress: String = checkNotNull(savedStateHandle["emailAddress"]) as String
    val code: String? = savedStateHandle["code"]

    fun resetPassword() =
        viewModelScope.launch {
            authenticationUseCase.sendResetPassword(emailAddress = emailAddress, scope = viewModelScope)
                .collectLatest { uiState ->
                    _resetPasswordState.value = uiState
                }
        }
}
