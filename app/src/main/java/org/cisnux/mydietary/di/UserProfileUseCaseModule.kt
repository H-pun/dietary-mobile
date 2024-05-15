package org.cisnux.mydietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.cisnux.mydietary.domain.usecases.UserProfileInteractor
import org.cisnux.mydietary.domain.usecases.UserProfileUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserProfileUseCaseModule {

    @Singleton
    @Binds
    abstract fun bindUserProfileInteractor(userProfileInteractor: UserProfileInteractor): UserProfileUseCase
}