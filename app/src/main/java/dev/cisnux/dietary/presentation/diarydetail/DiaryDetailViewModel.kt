package dev.cisnux.dietary.presentation.diarydetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cisnux.dietary.domain.models.FoodDiaryDetail
import dev.cisnux.dietary.domain.usecases.AuthenticationUseCase
import dev.cisnux.dietary.domain.usecases.FoodDiaryUseCase
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryDetailViewModel @Inject constructor(
    private val useCase: FoodDiaryUseCase,
    private val authenticationUseCase: AuthenticationUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val foodDiaryId = checkNotNull(value = savedStateHandle["foodDiaryId"]) as String
    private val _foodDiaryDetailState =
        MutableStateFlow<UiState<FoodDiaryDetail>>(UiState.Initialize)
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