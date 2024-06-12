package org.cisnux.mydietary.presentation.devmode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import org.cisnux.mydietary.domain.usecases.FoodDiaryUseCase
import org.cisnux.mydietary.commons.utils.DIETARY_API
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DevModeViewModel @Inject constructor(
    private val foodDiaryUseCase: FoodDiaryUseCase
) : ViewModel() {
    val baseUrl
        get() = foodDiaryUseCase.baseUrl.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = DIETARY_API
        )

    fun updateBaseUrl(baseUrl: String) = viewModelScope.launch {
        foodDiaryUseCase.updateBaseUrlApi(baseUrl = baseUrl)
    }
}