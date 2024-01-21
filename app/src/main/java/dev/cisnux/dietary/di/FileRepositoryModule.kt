package dev.cisnux.dietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cisnux.dietary.domain.repositories.FileRepository
import dev.cisnux.dietary.data.repositories.FileRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FileRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindFileRepositoryImpl(fileRepositoryImpl: FileRepositoryImpl): FileRepository
}