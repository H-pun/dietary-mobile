package dev.cisnux.dietary.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val _foodPicture: MutableStateFlow<File?> = MutableStateFlow(null)
    val foodPicture get() = _foodPicture.asSharedFlow()

    fun updateFoodPicture(newFile: File) {
        _foodPicture.value = newFile
    }
}