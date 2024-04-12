package dev.cisnux.dietary.presentation.predictedresult

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cisnux.dietary.domain.models.PredictedFood
import dev.cisnux.dietary.domain.usecases.AuthenticationUseCase
import dev.cisnux.dietary.domain.usecases.FoodDiaryUseCase
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PredictedResultViewModel @Inject constructor(
    private val foodDiaryUseCase: FoodDiaryUseCase,
    private val authenticationUseCase: AuthenticationUseCase
) : ViewModel() {
    private val _predictedResultState =
        MutableStateFlow<UiState<List<PredictedFood>>>(UiState.Initialize)
    val predictedResultState get() = _predictedResultState.asStateFlow()

    fun predictFoods(foodPicture: File) = viewModelScope.launch {
        foodDiaryUseCase.predictFoods(foodPicture).collectLatest { uiState ->
            _predictedResultState.value = uiState
        }
    }

    fun signOut() = viewModelScope.launch {
        authenticationUseCase.signOut()
    }
}