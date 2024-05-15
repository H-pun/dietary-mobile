package org.cisnux.mydietary.data.locals

import kotlinx.coroutines.flow.Flow

interface LandingLocalSource {
    val hasLandingShowed: Flow<Boolean>
    suspend fun updateLandingStatus(hasLandingShowed: Boolean)
}