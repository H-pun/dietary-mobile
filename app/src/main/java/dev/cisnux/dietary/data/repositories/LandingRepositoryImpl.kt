package dev.cisnux.dietary.data.repositories

import dev.cisnux.dietary.domain.repositories.LandingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LandingRepositoryImpl @Inject constructor() : LandingRepository {
    override val hasLandingShowed: Flow<Boolean>
        get() = flow {
            emit(false)
        }.distinctUntilChanged()
            .flowOn(Dispatchers.IO)
}