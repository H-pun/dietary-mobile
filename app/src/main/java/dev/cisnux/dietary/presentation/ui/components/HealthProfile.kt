package dev.cisnux.dietary.presentation.ui.components

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize

@Parcelize
@Stable
data class HealthProfile(
    val age: String,
    val weight: String,
    val height: String,
    val gender: String,
) : Parcelable