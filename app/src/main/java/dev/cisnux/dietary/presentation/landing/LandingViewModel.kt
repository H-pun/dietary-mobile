package dev.cisnux.dietary.presentation.landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cisnux.dietary.domain.usecases.LandingUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val useCase: LandingUseCase
) : ViewModel() {
    fun updateLandingStatus(hasLandingShowed: Boolean) = viewModelScope.launch {
        useCase.updateLandingStatus(hasLandingShowed)
    }
}