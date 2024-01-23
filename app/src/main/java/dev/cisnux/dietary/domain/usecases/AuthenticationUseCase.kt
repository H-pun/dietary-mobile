package dev.cisnux.dietary.domain.usecases

import kotlinx.coroutines.flow.Flow

interface AuthenticationUseCase {
    val hasAuthTokenExpired: Flow<Boolean>
    val hasFoodSecretTokenExpired: Flow<Boolean>
}
