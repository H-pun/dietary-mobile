package org.cisnux.mydietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.cisnux.mydietary.data.remotes.UserProfileRemoteSource
import org.cisnux.mydietary.data.remotes.UserProfileRemoteSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserProfileRemoteSourceModule {

    @Singleton
    @Binds
    abstract fun bindUserProfileRemoteSourceImpl(userProfileRemoteSourceImpl: UserProfileRemoteSourceImpl): UserProfileRemoteSource
}