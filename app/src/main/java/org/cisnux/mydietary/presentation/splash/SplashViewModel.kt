package org.cisnux.mydietary.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import org.cisnux.mydietary.domain.usecases.AuthenticationUseCase
import org.cisnux.mydietary.domain.usecases.LandingUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase,
    landingUseCase: LandingUseCase
) : ViewModel() {
    val hasLandingShowed = landingUseCase.hasLandingShowed.shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed()
    )
    val authenticationState = authenticationUseCase
        .getAuthenticationState(scope = viewModelScope)

    fun signOut() = viewModelScope.launch {
        authenticationUseCase.signOut()
    }
}