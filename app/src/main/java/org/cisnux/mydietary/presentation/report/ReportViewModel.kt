package org.cisnux.mydietary.presentation.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.cisnux.mydietary.domain.usecases.AuthenticationUseCase
import org.cisnux.mydietary.utils.UiState
import org.cisnux.mydietary.utils.reportCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.cisnux.mydietary.domain.models.DietProgress
import org.cisnux.mydietary.domain.models.MonthlyNutritionReport
import org.cisnux.mydietary.domain.models.WeeklyNutritionReport
import org.cisnux.mydietary.domain.models.UserNutrition
import org.cisnux.mydietary.domain.usecases.ReportUseCase
import org.cisnux.mydietary.utils.ReportCategory
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val authenticationUseCase: AuthenticationUseCase,
    private val reportUseCase: ReportUseCase
) : ViewModel() {
    private val _weeklyNutritionReportState =
        MutableStateFlow<UiState<WeeklyNutritionReport>>(UiState.Initialize)
    val weeklyNutritionReportState get() = _weeklyNutritionReportState.asSharedFlow()

    private val _monthlyNutritionReportState =
        MutableStateFlow<UiState<MonthlyNutritionReport>>(UiState.Initialize)
    val monthlyNutritionReportState get() = _monthlyNutritionReportState.asSharedFlow()

    val hasAccess
        get() = authenticationUseCase.getAccessToken(scope = viewModelScope).map { it?.isNotBlank() == true }

    private val _userDailyNutritionState =
        MutableStateFlow<UiState<UserNutrition>>(UiState.Initialize)
    val userDailyNutritionState = _userDailyNutritionState.asSharedFlow()

    private val _dietProgressState =
        MutableStateFlow<UiState<List<DietProgress>>>(UiState.Initialize)
    val dietProgressState get() = _dietProgressState.asSharedFlow()

    init {
        getReports(0)
        viewModelScope.launch {
            reportUseCase.getDietProgress(scope = viewModelScope).distinctUntilChanged()
                .collectLatest {
                    _dietProgressState.value = it
                }
        }
        viewModelScope.launch {
            reportUseCase.getDailyNutrition(scope = viewModelScope).distinctUntilChanged()
                .collectLatest {
                    _userDailyNutritionState.value = it
                }
        }
    }


    fun getReports(index: Int = 0) = viewModelScope.launch {
        val reportCategory = index.reportCategory
        if (reportCategory == ReportCategory.WEEKLY)
            reportUseCase.getWeeklyNutrition(scope = viewModelScope).distinctUntilChanged()
                .collectLatest { uiState ->
                    _weeklyNutritionReportState.value = uiState
                }
        else
            reportUseCase.getMonthlyNutrition(scope = viewModelScope).distinctUntilChanged()
                .collectLatest { uiState ->
                    _monthlyNutritionReportState.value = uiState
                }
    }

    fun signOut() = CoroutineScope(context = SupervisorJob() + Dispatchers.IO).launch {
        authenticationUseCase.signOut()
    }
}
