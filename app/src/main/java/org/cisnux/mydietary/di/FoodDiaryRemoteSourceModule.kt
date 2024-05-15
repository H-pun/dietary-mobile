package org.cisnux.mydietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.cisnux.mydietary.data.remotes.FoodDiaryRemoteSource
import org.cisnux.mydietary.data.remotes.FoodDiaryRemoteSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FoodDiaryRemoteSourceModule {

    @Singleton
    @Binds
    abstract fun bindFoodDiaryRemoteSourceImpl(foodDiaryRemoteSourceImpl: FoodDiaryRemoteSourceImpl): FoodDiaryRemoteSource
}