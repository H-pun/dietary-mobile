package org.cisnux.mydietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.cisnux.mydietary.data.locals.UserProfileLocalSource
import org.cisnux.mydietary.data.locals.UserProfileLocalSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserProfileLocalSourceModule {

    @Singleton
    @Binds
    abstract fun bindUserProfileLocalSourceImpl(userProfileLocalSourceImpl: UserProfileLocalSourceImpl): UserProfileLocalSource
}