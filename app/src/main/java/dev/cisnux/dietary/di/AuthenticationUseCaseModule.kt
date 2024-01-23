package dev.cisnux.dietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cisnux.dietary.domain.usecases.AuthenticationInteractor
import dev.cisnux.dietary.domain.usecases.AuthenticationUseCase
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