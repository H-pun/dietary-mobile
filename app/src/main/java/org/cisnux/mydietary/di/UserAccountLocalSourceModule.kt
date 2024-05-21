package org.cisnux.mydietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.cisnux.mydietary.data.locals.UserAccountLocalSource
import org.cisnux.mydietary.data.locals.UserAccountLocalSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserAccountLocalSourceModule {

    @Singleton
    @Binds
    abstract fun bindUserAccountLocalSourceImpl(userAccountLocalSourceImpl: UserAccountLocalSourceImpl): UserAccountLocalSource
}