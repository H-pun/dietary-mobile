package dev.cisnux.dietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cisnux.dietary.data.repositories.TokenRepositoryImpl
import dev.cisnux.dietary.domain.repositories.TokenRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TokenRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindTokenRepositoryImpl(tokenRepositoryImpl: TokenRepositoryImpl): TokenRepository
}