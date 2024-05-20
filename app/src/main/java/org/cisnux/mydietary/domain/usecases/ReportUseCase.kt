package org.cisnux.mydietary.domain.usecases

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.cisnux.mydietary.domain.models.DietProgress
import org.cisnux.mydietary.domain.models.MonthlyNutritionReport
import org.cisnux.mydietary.domain.models.UserNutrition
import org.cisnux.mydietary.domain.models.WeeklyNutritionReport
import org.cisnux.mydietary.utils.UiState

interface ReportUseCase {
    fun getDietProgress(scope: CoroutineScope): Flow<UiState<List<DietProgress>>>
    fun getDailyNutrition(scope: CoroutineScope): Flow<UiState<UserNutrition>>
    fun getWeeklyNutrition(scope: CoroutineScope): Flow<UiState<WeeklyNutritionReport>>
    fun getMonthlyNutrition(scope: CoroutineScope): Flow<UiState<MonthlyNutritionReport>>
}