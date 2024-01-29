package dev.cisnux.dietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cisnux.dietary.domain.usecases.FoodDiaryInteractor
import dev.cisnux.dietary.domain.usecases.FoodDiaryUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DiaryFoodUseCaseModule {

    @Singleton
    @Binds
    abstract fun bindDiaryFoodInteractor(foodInteractor: FoodDiaryInteractor): FoodDiaryUseCase
}