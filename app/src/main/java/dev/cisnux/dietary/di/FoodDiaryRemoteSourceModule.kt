package dev.cisnux.dietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cisnux.dietary.data.remotes.FoodDiaryRemoteSource
import dev.cisnux.dietary.data.remotes.FoodDiaryRemoteSourceImpl
import dev.cisnux.dietary.data.remotes.UserProfileRemoteSource
import dev.cisnux.dietary.data.remotes.UserProfileRemoteSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FoodDiaryRemoteSourceModule {

    @Singleton
    @Binds
    abstract fun bindFoodDiaryRemoteSourceImpl(foodDiaryRemoteSourceImpl: FoodDiaryRemoteSourceImpl): FoodDiaryRemoteSource
}