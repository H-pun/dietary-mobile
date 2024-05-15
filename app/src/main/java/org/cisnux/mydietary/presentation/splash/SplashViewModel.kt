package org.cisnux.mydietary.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import org.cisnux.mydietary.domain.usecases.AuthenticationUseCase
import org.cisnux.mydietary.domain.usecases.LandingUseCase
import org.cisnux.mydietary.utils.AuthenticationState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase,
    landingUseCase: LandingUseCase
) : ViewModel() {
    val hasLandingShowed = landingUseCase.hasLandingShowed.stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = SharingStarted.Eagerly
    )
    val authenticationState = authenticationUseCase.authenticationState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AuthenticationState.INITIALIZE
    )

    fun signOut() = viewModelScope.launch {
        authenticationUseCase.signOut()
    }
}