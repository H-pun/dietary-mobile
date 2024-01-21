package dev.cisnux.dietary.presentation.addmyprofile

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize

@Parcelize
@Stable
data class MyProfile(
    val username: String,
    val age: String,
    val weight: String,
    val height: String,
    val gender: String,
    val goal: String,
    val targetWeight: String,
    val activityLevel: String,
) : Parcelable