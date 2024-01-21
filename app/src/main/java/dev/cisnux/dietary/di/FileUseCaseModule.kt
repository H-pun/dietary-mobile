package dev.cisnux.dietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cisnux.dietary.domain.usecases.FileInteractor
import dev.cisnux.dietary.domain.usecases.FileUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FileUseCaseModule {

    @Singleton
    @Binds
    abstract fun bindFileInteractor(fileInteractor: FileInteractor): FileUseCase
}