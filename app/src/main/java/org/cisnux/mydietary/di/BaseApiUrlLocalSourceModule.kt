package org.cisnux.mydietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.cisnux.mydietary.data.locals.BaseApiUrlLocalSource
import org.cisnux.mydietary.data.locals.BaseApiUrlLocalSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BaseApiUrlLocalSourceModule {
    @Singleton
    @Binds
    abstract fun bindbaseApiUrlLocalSourceImpl(baseApiUrlLocalSourceImpl: BaseApiUrlLocalSourceImpl): BaseApiUrlLocalSource
}