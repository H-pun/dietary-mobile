package org.cisnux.mydietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.cisnux.mydietary.domain.usecases.AuthenticationInteractor
import org.cisnux.mydietary.domain.usecases.AuthenticationUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthenticationUseCaseModule {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Singleton
    @Binds
    abstract fun bindAuthenticationInteractor(authenticationInteractor: AuthenticationInteractor): AuthenticationUseCase
}