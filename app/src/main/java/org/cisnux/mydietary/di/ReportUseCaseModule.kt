package org.cisnux.mydietary.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.cisnux.mydietary.domain.usecases.ReportInteractor
import org.cisnux.mydietary.domain.usecases.ReportUseCase
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class ReportUseCaseModule {
    @Singleton
    @Binds
    abstract fun bindReportInteractor(reportInteractor: ReportInteractor): ReportUseCase
}