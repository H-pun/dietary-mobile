package org.cisnux.mydietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.cisnux.mydietary.domain.usecases.FileInteractor
import org.cisnux.mydietary.domain.usecases.FileUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FileUseCaseModule {

    @Singleton
    @Binds
    abstract fun bindFileInteractor(fileInteractor: FileInteractor): FileUseCase
}