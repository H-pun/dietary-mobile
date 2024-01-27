package dev.cisnux.dietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cisnux.dietary.domain.usecases.DiaryFoodInteractor
import dev.cisnux.dietary.domain.usecases.DiaryFoodUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DiaryFoodUseCaseModule {

    @Singleton
    @Binds
    abstract fun bindDiaryFoodInteractor(foodInteractor: DiaryFoodInteractor): DiaryFoodUseCase
}