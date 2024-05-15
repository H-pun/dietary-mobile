package org.cisnux.mydietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.cisnux.mydietary.data.locals.LandingLocalSource
import org.cisnux.mydietary.data.locals.LandingLocalSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LandingLocalSourceModule {

    @Singleton
    @Binds
    abstract fun bindLandingLocalSourceImpl(landingLocalSourceImpl: LandingLocalSourceImpl): LandingLocalSource
}