package dev.cisnux.dietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cisnux.dietary.domain.usecases.UserProfileInteractor
import dev.cisnux.dietary.domain.usecases.UserProfileUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserProfileUseCase {

    @Singleton
    @Binds
    abstract fun bindUserProfileInteractor(userProfileInteractor: UserProfileInteractor): UserProfileUseCase
}