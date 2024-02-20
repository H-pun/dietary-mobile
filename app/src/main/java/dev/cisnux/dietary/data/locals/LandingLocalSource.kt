package dev.cisnux.dietary.data.locals

import kotlinx.coroutines.flow.Flow

interface LandingLocalSource {
    val hasLandingShowed: Flow<Boolean>
    suspend fun updateLandingStatus(hasLandingShowed: Boolean)
}