package dev.cisnux.dietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cisnux.dietary.data.repositories.FoodDiaryRepositoryImpl
import dev.cisnux.dietary.domain.repositories.FoodRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FoodDiaryRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindFoodDiaryRepositoryImpl(foodDiaryRepositoryImpl: FoodDiaryRepositoryImpl): FoodRepository
}