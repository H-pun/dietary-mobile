package org.cisnux.mydietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.cisnux.mydietary.data.remotes.UserAccountRemoteSource
import org.cisnux.mydietary.data.remotes.UserAccountRemoteSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserAccountRemoteSourceModule {

    @Singleton
    @Binds
    abstract fun bindUserAccountRemoteSourceImpl(userAccountRemoteSourceImpl: UserAccountRemoteSourceImpl): UserAccountRemoteSource
}