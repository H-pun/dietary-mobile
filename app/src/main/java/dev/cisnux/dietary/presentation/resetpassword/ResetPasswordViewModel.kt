package dev.cisnux.dietary.presentation.resetpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cisnux.dietary.domain.usecases.AuthenticationUseCase
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase
) : ViewModel() {
    private val _resetPasswordState: MutableStateFlow<UiState<Nothing>> =
        MutableStateFlow(UiState.Initialize)
    val resetPasswordState get() = _resetPasswordState.asStateFlow()

    fun resetPassword(emailAddress: String) =
        viewModelScope.launch {
            authenticationUseCase.resetPassword(emailAddress)
                .collectLatest { uiState ->
                    _resetPasswordState.value = uiState
                }
        }
}