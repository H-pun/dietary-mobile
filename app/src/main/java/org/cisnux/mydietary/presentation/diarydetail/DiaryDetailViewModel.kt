package org.cisnux.mydietary.presentation.diarydetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import org.cisnux.mydietary.domain.models.FoodDiaryDetail
import org.cisnux.mydietary.domain.usecases.AuthenticationUseCase
import org.cisnux.mydietary.domain.usecases.FoodDiaryUseCase
import org.cisnux.mydietary.domain.usecases.UserProfileUseCase
import org.cisnux.mydietary.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.cisnux.mydietary.domain.models.UserNutrition
import javax.inject.Inject

@HiltViewModel
class DiaryDetailViewModel @Inject constructor(
    private val useCase: FoodDiaryUseCase,
    private val userProfileUseCase: UserProfileUseCase,
    private val authenticationUseCase: AuthenticationUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val foodDiaryId = checkNotNull(value = savedStateHandle["foodDiaryId"]) as String
    private val _foodDiaryDetailState =
        MutableStateFlow<UiState<FoodDiaryDetail>>(UiState.Initialize)
    private val _userDailyNutritionState =
        MutableStateFlow<UiState<UserNutrition>>(UiState.Initialize)
    val userDailyNutritionState = _userDailyNutritionState.asSharedFlow()

    init {
        viewModelScope.launch {
            userProfileUseCase.userDailyNutrition.distinctUntilChanged().collectLatest {
                _userDailyNutritionState.value = it
            }
        }
    }
    val foodDiaryDetailState get() = _foodDiaryDetailState.asStateFlow()
    private val _duplicateState =
        MutableStateFlow<UiState<FoodDiaryDetail>>(UiState.Initialize)
    private val _removeState = MutableStateFlow<UiState<Nothing>>(UiState.Initialize)
    val removeState get() = _removeState.asStateFlow()

    init {
        getFoodDiaryDetailById()
    }

    fun getFoodDiaryDetailById() = viewModelScope.launch {
        useCase.getFoodDiaryDetailById(foodDiaryId = foodDiaryId).collectLatest { uiState ->
            _foodDiaryDetailState.value = uiState
        }
    }

    fun deleteFoodDiaryById() = viewModelScope.launch {
        useCase.deleteFoodDiaryById(foodDiaryId = foodDiaryId).collectLatest { uiState ->
            _removeState.value = uiState
        }
    }

    fun signOut() = viewModelScope.launch {
        authenticationUseCase.signOut()
    }
}