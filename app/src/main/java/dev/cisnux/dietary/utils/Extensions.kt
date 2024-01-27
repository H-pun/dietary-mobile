package dev.cisnux.dietary.utils

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import dev.cisnux.dietary.domain.models.UserProfile
import dev.cisnux.dietary.domain.models.UserProfileDetail
import dev.cisnux.dietary.presentation.addmyprofile.MyProfile
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

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

fun String.isEmailValid(): Boolean {
    val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\$"
    return Regex(emailRegex).matches(this)
}

fun String.isPasswordSecure(): Boolean = this.trim().length >= 8

fun String.isUsernameValid(): Boolean {
    val usernameRegex = "^[a-z0-9_]{6,20}\$"
    return Regex(usernameRegex).matches(this)
}

fun String.isAgeValid(): Boolean = try {
    val age = this.toInt()
    age > 0
} catch (e: NumberFormatException) {
    false
}

fun String.isHeightOrWeightValid(): Boolean = try {
    val number = this.toFloat()
    number > 0
} catch (e: NumberFormatException) {
    false
}

fun String.isTargetWeightValid(): Boolean = try {
    val targetWeight = this.toFloat()
    targetWeight >= 0
} catch (e: NumberFormatException) {
    false
}

fun Long.withDateFormat(): String {
    val date = Date(this)
    return DateFormat.getDateInstance(DateFormat.FULL).format(date)
}

fun Long.withTimeFormat(): String {
    val date = Date(this)
    return SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT, Locale.getDefault())
        .format(date)
}

val Long.asDays: Long get() = TimeUnit.MILLISECONDS.toDays(this)

val Int.diaryFoodCategory: DiaryFoodCategory
    get() = when (this) {
        0 -> DiaryFoodCategory.BREAKFAST
        1 -> DiaryFoodCategory.LUNCH
        else -> DiaryFoodCategory.DINNER
    }