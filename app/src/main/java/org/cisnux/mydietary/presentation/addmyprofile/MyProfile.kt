package org.cisnux.mydietary.presentation.addmyprofile

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
    val weightTarget: String,
    val activityLevel: String,
) : Parcelable