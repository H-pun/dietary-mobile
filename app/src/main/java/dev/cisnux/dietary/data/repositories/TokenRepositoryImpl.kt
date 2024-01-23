package dev.cisnux.dietary.data.repositories

import dev.cisnux.dietary.domain.repositories.TokenRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor() : TokenRepository {
    override val hasAuthTokenExpired: Flow<Boolean>
        get() = flow {
            emit(false)
        }
            .flowOn(Dispatchers.IO)
    override val hasFoodSecretTokenExpired: Flow<Boolean>
        get() = flow {
            emit(false)
        }
            .flowOn(Dispatchers.IO)
}