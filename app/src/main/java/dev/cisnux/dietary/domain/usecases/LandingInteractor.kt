package dev.cisnux.dietary.domain.usecases

import dev.cisnux.dietary.domain.repositories.LandingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LandingInteractor @Inject constructor(
    private val landingRepository: LandingRepository
) : LandingUseCase {
    override val hasLandingShowed: Flow<Boolean>
        get() = landingRepository.hasLandingShowed
}