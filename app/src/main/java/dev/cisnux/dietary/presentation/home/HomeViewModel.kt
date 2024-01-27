package dev.cisnux.dietary.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cisnux.dietary.domain.models.DiaryFood
import dev.cisnux.dietary.domain.usecases.DiaryFoodUseCase
import dev.cisnux.dietary.utils.DiaryFoodCategory
import dev.cisnux.dietary.utils.UiState
import dev.cisnux.dietary.utils.asDays
import dev.cisnux.dietary.utils.diaryFoodCategory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val diaryFoodUseCase: DiaryFoodUseCase
) : ViewModel() {
    private var selectedDate = System.currentTimeMillis()
    private var diaryFoodCategory = DiaryFoodCategory.BREAKFAST
    private val _diaryFoodState = MutableStateFlow<UiState<List<DiaryFood>>>(UiState.Initialize)
    val diaryFoodState get() = _diaryFoodState.asSharedFlow()
    private val _searchedDiaryFoodState =
        MutableStateFlow<UiState<List<DiaryFood>>>(UiState.Initialize)
    val searchedDiaryFoodState get() = _searchedDiaryFoodState.asSharedFlow()

    init {
        getDiaryFoods()
    }

//    @OptIn(ExperimentalCoroutinesApi::class)
//    val diaryFoodState =
//        selectedDate.combine(diaryFoodCategory) { selectedDate, diaryFoodCategory ->
//            Pair(selectedDate.asDays, diaryFoodCategory)
//        }.flatMapLatest {
//            foodUseCase.getDiaryFoodsByDays(days = it.first, category = it.second)
//        }.shareIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(),
//        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val diaryFoods = _diaryFoodState.mapLatest { state ->
        when (state) {
            is UiState.Success -> state.data
            else -> null
        }
    }.shareIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchedDiaryFoods = _searchedDiaryFoodState.mapLatest { state ->
        when (state) {
            is UiState.Success -> {
                Log.d("HomeViewModel", state.data.toString())
                state.data
            }

            else -> null
        }
    }.shareIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed()
    )

    private val _keywordSuggestionState =
        MutableStateFlow<UiState<List<String>>>(UiState.Initialize)
    val keywordSuggestionState get() = _keywordSuggestionState.asSharedFlow()

    @ExperimentalCoroutinesApi
    val keywordSuggestions = _keywordSuggestionState.mapLatest { state ->
        when (state) {
            is UiState.Success -> state.data
            else -> listOf()
        }
    }.shareIn(
        scope = viewModelScope, started = SharingStarted.Eagerly,
    )

    fun updateSelectedDate(dateTimeMillis: Long? = System.currentTimeMillis()) {
        selectedDate = dateTimeMillis!!
        getDiaryFoods()
    }

    fun updateFoodDiary(index: Int) {
        diaryFoodCategory = index.diaryFoodCategory
        getDiaryFoods()
    }

    fun getDiaryFoods() =
        viewModelScope.launch {
            diaryFoodUseCase.getDiaryFoodsByDays(
                days = selectedDate.asDays,
                category = diaryFoodCategory
            ).collectLatest {
                _diaryFoodState.value = it
            }
        }

    @OptIn(FlowPreview::class)
    fun getKeywordSuggestions(query: String) = viewModelScope.launch {
        if (query.isBlank())
            _keywordSuggestionState.value = UiState.Success(listOf())
        else
            diaryFoodUseCase.getKeywordSuggestionsByQuery(query)
                .debounce(300L)
                .collectLatest {
                    _keywordSuggestionState.value = it
                }
    }

    fun getDiaryFoodsByQuery(query: String) = viewModelScope.launch {
        diaryFoodUseCase.getDiaryFoodsByQuery(query).collectLatest {
            _searchedDiaryFoodState.value = it
        }
    }
}