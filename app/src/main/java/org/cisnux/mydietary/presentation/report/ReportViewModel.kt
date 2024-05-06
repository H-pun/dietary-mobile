package org.cisnux.mydietary.presentation.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import org.cisnux.mydietary.domain.models.Report
import org.cisnux.mydietary.domain.usecases.AuthenticationUseCase
import org.cisnux.mydietary.domain.usecases.FoodDiaryUseCase
import org.cisnux.mydietary.utils.UiState
import org.cisnux.mydietary.utils.reportCategory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val useCase: FoodDiaryUseCase,
    private val authenticationUseCase: AuthenticationUseCase,
) : ViewModel() {
    private val _reportState = MutableStateFlow<UiState<Report>>(UiState.Initialize)
    val reportState get() = _reportState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val report = _reportState.mapLatest { uiState ->
        when (uiState) {
            is UiState.Success -> uiState.data
            else -> null
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = null
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val maxRange = report.mapLatest { report ->
        report?.foods?.maxBy { it.totalFoodCalories }?.totalFoodCalories?.toInt() ?: 0
    }.stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(), initialValue = 0)

    fun getReports(index: Int = 0) = viewModelScope.launch {
        useCase.getFoodDiaryReports(index.reportCategory).collectLatest { uiState ->
            _reportState.value = uiState
        }
    }

    fun signOut() = viewModelScope.launch {
        authenticationUseCase.signOut()
    }
}
