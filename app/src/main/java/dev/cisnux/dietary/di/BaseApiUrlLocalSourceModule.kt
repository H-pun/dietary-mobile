package dev.cisnux.dietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.cisnux.dietary.data.locals.BaseApiUrlLocalSource
import dev.cisnux.dietary.data.locals.BaseApiUrlLocalSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BaseApiUrlLocalSourceModule {
    @Singleton
    @Binds
    abstract fun bindbaseApiUrlLocalSourceImpl(baseApiUrlLocalSourceImpl: BaseApiUrlLocalSourceImpl): BaseApiUrlLocalSource
}