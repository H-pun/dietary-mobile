package dev.cisnux.dietary.presentation.scannerresult

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cisnux.dietary.domain.models.AddFoodDiary
import dev.cisnux.dietary.domain.models.Answer
import dev.cisnux.dietary.domain.models.Food
import dev.cisnux.dietary.domain.models.FoodDiaryQuestion
import dev.cisnux.dietary.domain.models.FoodQuestion
import dev.cisnux.dietary.domain.models.FoodDiaryDetail
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
class ScannerResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val useCase: FoodDiaryUseCase,
    private val authenticationUseCase: AuthenticationUseCase,
) : ViewModel() {
    private val title = checkNotNull(value = savedStateHandle["title"]) as String
    private val foodDiaryCategory =
        checkNotNull(value = savedStateHandle["foodDiaryCategory"]) as String
    private val _scannerResultState = MutableStateFlow<UiState<FoodDiaryDetail>>(UiState.Initialize)
    val scannerResultState get() = _scannerResultState.asStateFlow()
    private val _removeState = MutableStateFlow<UiState<Nothing>>(UiState.Initialize)
    val removeState get() = _removeState.asStateFlow()

    fun addFoodDiary(foodPicture: File) = viewModelScope.launch {
        val addFoodDiary = AddFoodDiary(
            title = title,
            category = foodDiaryCategory,
            foodPicture = foodPicture
        )

        useCase.addFoodDiary(addFoodDiary = addFoodDiary).collectLatest { uiState ->
            _scannerResultState.value = uiState
        }
    }

    fun deleteFoodDiaryById(foodDiaryId: String) = viewModelScope.launch {
        useCase.deleteFoodDiaryById(foodDiaryId = foodDiaryId).collectLatest { uiState ->
            _removeState.value = uiState
        }
    }

    fun updateFoodDiaryBaseOnQuestion(
        foodDiaryDetail: FoodDiaryDetail,
        foodQuestions: List<Food>,
        answeredQuestions: List<List<AnsweredQuestion>>
    ) {
        val foodDiaryQuestion = FoodDiaryQuestion(
            foodDiaryId = foodDiaryDetail.foodDiaryId,
            foodQuestions = foodQuestions.mapIndexed { index, food ->
                FoodQuestion(
                    foodId = food.id,
                    foodName = food.name,
                    answers = answeredQuestions[index].map { answeredQuestion ->
                        Answer(
                            questionId = answeredQuestion.questionId,
                            question = answeredQuestion.question,
                            answer = answeredQuestion.answer,
                            questionType = answeredQuestion.type
                        )
                    }
                )
            }
        )

        viewModelScope.launch {
            useCase.updateFoodDiaryBaseOnAnsweredQuestion(foodDiaryQuestion)
                .collectLatest { uiState ->
                    _scannerResultState.value = uiState
                }
        }
    }

    fun signOut() = viewModelScope.launch {
        authenticationUseCase.signOut()
    }
}