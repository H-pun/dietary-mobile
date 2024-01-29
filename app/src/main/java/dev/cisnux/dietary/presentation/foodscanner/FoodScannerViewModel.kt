package dev.cisnux.dietary.presentation.foodscanner

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cisnux.dietary.domain.usecases.FileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FoodScannerViewModel @Inject constructor(
    private val fileUseCase: FileUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val title = checkNotNull(value = savedStateHandle["title"]) as String
    val foodDiaryCategory = checkNotNull(value = savedStateHandle["foodDiaryCategory"]) as String

    private var _cameraFile: MutableStateFlow<File?> = MutableStateFlow(null)
    val cameraFile get() = _cameraFile.asStateFlow()
    private var _galleryFile: MutableStateFlow<File?> = MutableStateFlow(null)
    val galleryFile get() = _galleryFile.asStateFlow()

    fun createFile() = viewModelScope.launch {
        _cameraFile.value = fileUseCase.createFile()
    }

    fun fileFromUri(image: Uri) = viewModelScope.launch {
        _galleryFile.value = fileUseCase.fileFromUri(image = image)
    }

    fun clearFileStates() {
        _cameraFile.value = null
        _galleryFile.value = null
    }
}