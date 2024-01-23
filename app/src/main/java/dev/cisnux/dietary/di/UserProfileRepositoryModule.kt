package dev.cisnux.dietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cisnux.dietary.data.repositories.UserProfileRepositoryImpl
import dev.cisnux.dietary.domain.repositories.UserProfileRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserProfileRepositoryModule {

    @Singleton
    @Binds
    abstract fun bindUserProfileRepositoryImpl(userProfileRepositoryImpl: UserProfileRepositoryImpl): UserProfileRepository
}