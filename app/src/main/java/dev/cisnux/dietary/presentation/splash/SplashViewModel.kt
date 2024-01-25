package dev.cisnux.dietary.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cisnux.dietary.domain.usecases.AuthenticationUseCase
import dev.cisnux.dietary.domain.usecases.LandingUseCase
import dev.cisnux.dietary.domain.usecases.UserProfileUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    authenticationUseCase: AuthenticationUseCase,
    userProfileUseCase: UserProfileUseCase,
    landingUseCase: LandingUseCase
) : ViewModel() {
    val hasTokenExpired = authenticationUseCase.hasAuthTokenExpired
        .combine(authenticationUseCase.hasFoodSecretTokenExpired) { hasAuthTokenExpired, hasFoodSecretTokenExpired ->
            hasAuthTokenExpired or hasFoodSecretTokenExpired
        }.stateIn(scope = viewModelScope, initialValue = null, started = SharingStarted.Eagerly)
    val isUserProfileExist = userProfileUseCase.isUserProfileExist.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )
    val hasLandingShowed = landingUseCase.hasLandingShowed.stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = SharingStarted.Eagerly
    )
}