package org.cisnux.mydietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.cisnux.mydietary.domain.usecases.FoodDiaryInteractor
import org.cisnux.mydietary.domain.usecases.FoodDiaryUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FoodDiaryUseCaseModule {

    @Singleton
    @Binds
    abstract fun bindFoodDiaryInteractor(foodDiaryInteractor: FoodDiaryInteractor): FoodDiaryUseCase
}