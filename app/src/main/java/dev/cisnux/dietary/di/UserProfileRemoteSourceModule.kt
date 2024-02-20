package dev.cisnux.dietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cisnux.dietary.data.remotes.UserProfileRemoteSource
import dev.cisnux.dietary.data.remotes.UserProfileRemoteSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserProfileRemoteSourceModule {

    @Singleton
    @Binds
    abstract fun bindUserProfileRemoteSourceImpl(userProfileRemoteSourceImpl: UserProfileRemoteSourceImpl): UserProfileRemoteSource
}