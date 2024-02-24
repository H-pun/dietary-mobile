package dev.cisnux.dietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cisnux.dietary.data.remotes.ImageRemoteSource
import dev.cisnux.dietary.data.remotes.ImageRemoteSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ImageRemoteSourceModule {

    @Singleton
    @Binds
    abstract fun bindImageRemoteSourceImpl(
        imageRemoteSourceImpl: ImageRemoteSourceImpl
    ): ImageRemoteSource
}