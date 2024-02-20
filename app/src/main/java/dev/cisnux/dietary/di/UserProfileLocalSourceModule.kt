package dev.cisnux.dietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cisnux.dietary.data.locals.UserProfileLocalSource
import dev.cisnux.dietary.data.locals.UserProfileLocalSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserProfileLocalSourceModule {

    @Singleton
    @Binds
    abstract fun bindUserProfileLocalSourceImpl(userProfileLocalSourceImpl: UserProfileLocalSourceImpl): UserProfileLocalSource
}