package dev.cisnux.dietary.domain.usecases

import dev.cisnux.dietary.data.locals.LandingLocalSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LandingInteractor @Inject constructor(
    private val landingLocalSource: LandingLocalSource
) : LandingUseCase {
    override val hasLandingShowed: Flow<Boolean>
        get() = landingLocalSource.hasLandingShowed

    override suspend fun updateLandingStatus(hasLandingShowed: Boolean) =
        landingLocalSource.updateLandingStatus(hasLandingShowed)
}