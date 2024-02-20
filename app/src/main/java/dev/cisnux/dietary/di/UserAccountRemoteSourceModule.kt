package dev.cisnux.dietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cisnux.dietary.data.remotes.UserAccountRemoteSource
import dev.cisnux.dietary.data.remotes.UserAccountRemoteSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserAccountRemoteSourceModule {

    @Singleton
    @Binds
    abstract fun bindUserAccountRemoteSourceImpl(userAccountRemoteSourceImpl: UserAccountRemoteSourceImpl): UserAccountRemoteSource
}