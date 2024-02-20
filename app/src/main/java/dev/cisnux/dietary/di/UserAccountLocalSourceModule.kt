package dev.cisnux.dietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cisnux.dietary.data.locals.UserAccountLocalSource
import dev.cisnux.dietary.data.locals.UserAccountLocalSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserAccountLocalSourceModule {
    @Singleton
    @Binds
    abstract fun bindUserAccountLocalSourceImpl(userAccountLocalSourceImpl: UserAccountLocalSourceImpl): UserAccountLocalSource
}