package dev.cisnux.dietary.presentation.foodscanner

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.cisnux.dietary.domain.models.PredictedFood
import dev.cisnux.dietary.domain.usecases.AuthenticationUseCase
import dev.cisnux.dietary.domain.usecases.FileUseCase
import dev.cisnux.dietary.domain.usecases.FoodDiaryUseCase
import dev.cisnux.dietary.domain.usecases.UserProfileUseCase
import dev.cisnux.dietary.utils.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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
    private val authenticationUseCase: AuthenticationUseCase,
    private val foodDiaryUseCase: FoodDiaryUseCase,
) : ViewModel() {
    val userProfileDetail = userProfileUseCase.userProfileDetail.shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed()
    )
    private val refreshUserProfile = MutableStateFlow(false)
    private var _currentFileState = MutableStateFlow<File?>(null)
    val currentFileState get() = _currentFileState.asStateFlow()

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

    fun clearCameraStates() {
        _cameraFile.value = null
        _galleryFile.value = null
    }

    fun resetPredictedStates() {
        _currentFileState.value = null
        _predictedResultState.value = UiState.Initialize
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

    private val _predictedResultState =
        MutableStateFlow<UiState<List<PredictedFood>>>(UiState.Initialize)
    val predictedResultState get() = _predictedResultState.asStateFlow()

    fun predictFoods(foodPicture: File) = viewModelScope.launch {
        _currentFileState.value = foodPicture
        foodDiaryUseCase.predictFoods(foodPicture).collectLatest { uiState ->
            _predictedResultState.value = uiState
            Log.d(FoodScannerViewModel::class.toString(), _predictedResultState.toString())
        }
    }

    fun updateRefreshUserProfile(isRefresh: Boolean) {
        refreshUserProfile.value = isRefresh
    }

    fun signOut() = viewModelScope.launch {
        authenticationUseCase.signOut()
    }

    fun updateCurrentFileState(file: File) {
        _currentFileState.value = file
    }
}