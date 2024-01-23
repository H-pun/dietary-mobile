package dev.cisnux.dietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cisnux.dietary.data.repositories.FoodRepositoryImpl
import dev.cisnux.dietary.domain.repositories.FoodRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FoodRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindFoodRepositoryImpl(foodRepositoryImpl: FoodRepositoryImpl): FoodRepository
}