package dev.cisnux.dietary.presentation.foodscanner

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cisnux.dietary.domain.usecases.FileUseCase
import dev.cisnux.dietary.domain.usecases.UserProfileUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FoodScannerViewModel @Inject constructor(
    private val fileUseCase: FileUseCase,
    private val userProfileUseCase: UserProfileUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val title = checkNotNull(value = savedStateHandle["title"]) as String
    val foodDiaryCategory = checkNotNull(value = savedStateHandle["foodDiaryCategory"]) as String

    val userProfileDetail = userProfileUseCase.userProfileDetail.shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed()
    )
    private val refreshUserProfile = MutableStateFlow(false)

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

    @OptIn(ExperimentalCoroutinesApi::class)
    val userProfileState = refreshUserProfile.asStateFlow().flatMapMerge {
        if (it)
            userProfileUseCase.refreshUserProfile().also {
                refreshUserProfile.value = false
            }
        else flow { }
    }.shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed()
    )

    fun updateRefreshUserProfile(isRefresh: Boolean) {
        refreshUserProfile.value = isRefresh
    }
}