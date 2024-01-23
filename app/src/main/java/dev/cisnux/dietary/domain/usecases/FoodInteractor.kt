package dev.cisnux.dietary.domain.usecases

import dev.cisnux.dietary.domain.repositories.FoodRepository
import javax.inject.Inject

class FoodInteractor @Inject constructor(
    private val foodRepository: FoodRepository
) : FoodUseCase {
}