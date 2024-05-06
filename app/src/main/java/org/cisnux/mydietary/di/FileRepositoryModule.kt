package org.cisnux.mydietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.cisnux.mydietary.domain.repositories.FileRepository
import org.cisnux.mydietary.data.repositories.FileRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FileRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindFileRepositoryImpl(fileRepositoryImpl: FileRepositoryImpl): FileRepository
}