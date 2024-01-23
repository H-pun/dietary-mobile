package dev.cisnux.dietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cisnux.dietary.data.repositories.AuthenticationRepositoryImpl
import dev.cisnux.dietary.domain.repositories.AuthenticationRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthenticationRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindAuthenticationRepositoryImpl(authenticationRepositoryImpl: AuthenticationRepositoryImpl): AuthenticationRepository
}