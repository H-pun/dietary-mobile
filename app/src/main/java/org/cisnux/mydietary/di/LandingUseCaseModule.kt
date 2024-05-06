package org.cisnux.mydietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.cisnux.mydietary.domain.usecases.LandingInteractor
import org.cisnux.mydietary.domain.usecases.LandingUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LandingUseCaseModule {

    @Singleton
    @Binds
    abstract fun bindingLandingInteractor(landingInteractor: LandingInteractor): LandingUseCase
}