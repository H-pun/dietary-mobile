package dev.cisnux.dietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cisnux.dietary.data.repositories.LandingRepositoryImpl
import dev.cisnux.dietary.domain.repositories.LandingRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LandingRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindLandingRepositoryImpl(landingRepositoryImpl: LandingRepositoryImpl): LandingRepository
}