package dev.cisnux.dietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cisnux.dietary.domain.usecases.LandingInteractor
import dev.cisnux.dietary.domain.usecases.LandingUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LandingUseCaseModule {

    @Singleton
    @Binds
    abstract fun bindingLandingInteractor(landingInteractor: LandingInteractor): LandingUseCase
}