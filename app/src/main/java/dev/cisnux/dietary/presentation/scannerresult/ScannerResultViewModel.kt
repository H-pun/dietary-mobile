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
    val title = checkNotNull(value = savedStateHandle["title"]) as String
    val foodDiaryCategory = checkNotNull(value = savedStateHandle["foodDiaryCategory"]) as String
}