package dev.cisnux.dietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cisnux.dietary.data.locals.LandingLocalSource
import dev.cisnux.dietary.data.locals.LandingLocalSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LandingLocalSourceModule {

    @Singleton
    @Binds
    abstract fun bindLandingLocalSourceImpl(landingLocalSourceImpl: LandingLocalSourceImpl): LandingLocalSource
}