package dev.cisnux.dietary.domain.repositories

import kotlinx.coroutines.flow.Flow

interface TokenRepository {
    val hasAuthTokenExpired: Flow<Boolean>
    suspend fun removeTokenState()
}