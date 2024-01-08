package dev.cisnux.dietary.presentation.scannerresult

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ScannerResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private var _foodPicture =
        MutableStateFlow(checkNotNull(value = savedStateHandle["foodPicture"]) as String)
    val foodPicture get() = _foodPicture.asStateFlow()
}