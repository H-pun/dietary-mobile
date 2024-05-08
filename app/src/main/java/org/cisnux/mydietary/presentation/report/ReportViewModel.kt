package org.cisnux.mydietary.presentation.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import org.cisnux.mydietary.domain.usecases.AuthenticationUseCase
import org.cisnux.mydietary.domain.usecases.FoodDiaryUseCase
import org.cisnux.mydietary.utils.UiState
import org.cisnux.mydietary.utils.reportCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.cisnux.mydietary.domain.models.FoodDiaryReport
import org.cisnux.mydietary.domain.models.UserNutrition
import org.cisnux.mydietary.domain.usecases.UserProfileUseCase
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val useCase: FoodDiaryUseCase,
    private val userProfileUseCase: UserProfileUseCase,
    private val authenticationUseCase: AuthenticationUseCase,
) : ViewModel() {
    private val _reportState = MutableStateFlow<UiState<List<FoodDiaryReport>>>(UiState.Initialize)
    val reportState get() = _reportState.asStateFlow()
    private val _userDailyNutritionState =
        MutableStateFlow<UiState<UserNutrition>>(UiState.Initialize)
    val userDailyNutritionState = _userDailyNutritionState.asSharedFlow()
    private val isRefresh = MutableStateFlow(false)

    init {
        getReports(0)
        viewModelScope.launch {
            userProfileUseCase.userDailyNutrition.distinctUntilChanged().collectLatest {
                _userDailyNutritionState.value = it
            }
        }
    }


    fun getReports(index: Int = 0) = viewModelScope.launch {
        useCase.getFoodDiaryReports(index.reportCategory).collectLatest { uiState ->
            _reportState.value = uiState
        }
    }

    fun signOut() = viewModelScope.launch {
        authenticationUseCase.signOut()
    }

    fun refresh(isRefresh: Boolean) {
        this.isRefresh.value = isRefresh
    }
}
