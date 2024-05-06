package org.cisnux.mydietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.cisnux.mydietary.data.repositories.FoodDiaryRepositoryImpl
import org.cisnux.mydietary.domain.repositories.FoodRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FoodDiaryRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindFoodDiaryRepositoryImpl(foodDiaryRepositoryImpl: FoodDiaryRepositoryImpl): FoodRepository
}