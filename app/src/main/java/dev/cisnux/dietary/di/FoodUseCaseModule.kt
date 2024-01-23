package dev.cisnux.dietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cisnux.dietary.domain.usecases.FoodInteractor
import dev.cisnux.dietary.domain.usecases.FoodUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FoodUseCaseModule {

    @Singleton
    @Binds
    abstract fun bindFoodInteractor(foodInteractor: FoodInteractor): FoodUseCase
}