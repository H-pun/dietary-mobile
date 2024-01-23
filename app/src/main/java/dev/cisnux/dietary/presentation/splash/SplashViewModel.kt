package dev.cisnux.dietary.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cisnux.dietary.domain.usecases.AuthenticationUseCase
import dev.cisnux.dietary.domain.usecases.UserProfileUseCase
import dev.cisnux.dietary.utils.UserState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    authenticationUseCase: AuthenticationUseCase,
    userProfileUseCase: UserProfileUseCase
) : ViewModel() {
    val userState = authenticationUseCase.hasAuthTokenExpired
        .combine(authenticationUseCase.hasFoodSecretTokenExpired) { hasAuthTokenExpired, hasFoodSecretTokenExpired ->
            hasAuthTokenExpired or hasFoodSecretTokenExpired
        }
        .combine(userProfileUseCase.isUserProfileExist) { hasTokenExpired, isUserProfileExist ->
            when {
                hasTokenExpired -> UserState.NOT_LOGIN
                !isUserProfileExist -> UserState.NOT_HAVE_PROFILE
                else -> UserState.LOGIN_WITH_PROFILE
            }
        }.stateIn(
            scope = viewModelScope,
            initialValue = UserState.INITIAL,
            started = SharingStarted.Eagerly
        )
}