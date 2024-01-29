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
abstract class DiaryFoodRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindDiaryFoodRepositoryImpl(foodDiaryRepositoryImpl: FoodDiaryRepositoryImpl): FoodRepository
}