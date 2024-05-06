package org.cisnux.mydietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.cisnux.mydietary.data.repositories.UserProfileRepositoryImpl
import org.cisnux.mydietary.domain.repositories.UserProfileRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserProfileRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindUserProfileRepositoryImpl(userProfileRepositoryImpl: UserProfileRepositoryImpl): UserProfileRepository
}