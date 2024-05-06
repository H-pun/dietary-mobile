package org.cisnux.mydietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.cisnux.mydietary.data.repositories.AuthenticationRepositoryImpl
import org.cisnux.mydietary.domain.repositories.AuthenticationRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthenticationRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindAuthenticationRepositoryImpl(authenticationRepositoryImpl: AuthenticationRepositoryImpl): AuthenticationRepository
}