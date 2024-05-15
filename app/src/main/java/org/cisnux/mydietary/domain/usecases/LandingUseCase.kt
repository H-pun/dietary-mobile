package org.cisnux.mydietary.domain.usecases

import kotlinx.coroutines.flow.Flow

interface LandingUseCase {
    val hasLandingShowed: Flow<Boolean>
    suspend fun updateLandingStatus(hasLandingShowed: Boolean)
}
