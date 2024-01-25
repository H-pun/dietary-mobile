package dev.cisnux.dietary.domain.repositories

import kotlinx.coroutines.flow.Flow

interface LandingRepository {
    val hasLandingShowed: Flow<Boolean>
}
