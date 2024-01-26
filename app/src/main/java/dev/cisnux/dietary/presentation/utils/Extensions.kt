package dev.cisnux.dietary.presentation.utils

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import dev.cisnux.dietary.domain.models.UserProfile
import dev.cisnux.dietary.domain.models.UserProfileDetail
import dev.cisnux.dietary.presentation.addmyprofile.MyProfile

val Context.activity: AppCompatActivity?
    get() {
        var currentContext = this
        while (currentContext is ContextWrapper) {
            if (currentContext is AppCompatActivity) {
                return currentContext
            }
            currentContext = currentContext.baseContext
        }
        return null
    }

val MyProfile.asUserProfile: UserProfile
    get() = UserProfile(
        username = username,
        age = age.toInt(),
        weight = weight.toFloat(),
        height = height.toFloat(),
        gender = gender,
        goal = goal,
        weightTarget = weightTarget.toFloat(),
        activityLevel = activityLevel
    )

val UserProfileDetail.asMyProfile: MyProfile
    get() = MyProfile(
        username = username,
        age = age.toString(),
        weight = weight.toDouble().toString(),
        height = height.toDouble().toString(),
        gender = gender,
        goal = goal,
        weightTarget = weightTarget.toDouble().toString(),
        activityLevel = activityLevel
    )