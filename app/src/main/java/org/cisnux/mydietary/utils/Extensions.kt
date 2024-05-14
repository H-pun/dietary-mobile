package org.cisnux.mydietary.utils

import android.content.Context
import android.content.ContextWrapper
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import org.cisnux.mydietary.data.remotes.bodyrequests.UserAccountBodyRequest
import org.cisnux.mydietary.domain.models.UserAccount
import org.cisnux.mydietary.domain.models.UserNutrition
import org.cisnux.mydietary.domain.models.AddUserProfile
import org.cisnux.mydietary.domain.models.UserProfileDetail
import org.cisnux.mydietary.presentation.addmyprofile.MyProfile
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

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

val Context.isDevModeActive: Boolean
    get() = Settings.Secure.getInt(
        this.contentResolver,
        Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
        0
    ) != 0

val MyProfile.asAddUserProfile: AddUserProfile
    get() = AddUserProfile(
        username = username,
        age = age.toInt(),
        weight = weight.toFloat(),
        height = height.toFloat(),
        gender = gender,
        goal = goal,
        weightTarget = weightTarget.toFloat(),
        activityLevel = activityLevel,
        waistCircumference = waistCircumference.toFloat()
    )

val UserAccount.userAccountBodyRequest
    get() = UserAccountBodyRequest(
        emailAddress = emailAddress, password = password
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

fun String.isIntAnswerValid(): Boolean = try {
    val value = this.toInt()
    value > 0
} catch (e: NumberFormatException) {
    false
}

fun String.isGreaterThanZeroValid(): Boolean = try {
    val number = this.toFloat()
    number > 0
} catch (e: NumberFormatException) {
    false
}

fun String.isFloatAnswerValid(): Boolean = try {
    val value = this.toFloat()
    value >= 0
} catch (e: NumberFormatException) {
    false
}

fun Instant.dayDateMonthYear(): String {
    val locale = Locale("id", "ID")
    val clock = Clock.fixed(this, ZoneId.systemDefault())
    val localDate = LocalDate.now(clock)
    return DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy", locale).format(localDate)
}

fun Instant.hoursAndMinutes(): String {
    val locale = Locale("id", "ID")
    val clock = Clock.fixed(this, ZoneId.systemDefault())
    val localTime = LocalTime.now(clock)
    return DateTimeFormatter.ofPattern("HH:mm", locale).format(localTime)
}


val Int.foodDiaryCategory: FoodDiaryCategory
    get() = when (this) {
        0 -> FoodDiaryCategory.BREAKFAST
        1 -> FoodDiaryCategory.LUNCH
        else -> FoodDiaryCategory.DINNER
    }

val Int.reportCategory: ReportCategory
    get() = when (this) {
        0 -> ReportCategory.WEEKLY
        else -> ReportCategory.WEEK
    }

fun convertISOToInstant(isoDateTime: String): Instant {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    val parsedDateTime = LocalDateTime.parse(isoDateTime, formatter)

    // Convert LocalDateTime to milliseconds
    return parsedDateTime.atZone(ZoneId.systemDefault()).toInstant()
}

fun UserProfileDetail.calculateMaxDailyNutrition(userNutrition: UserNutrition): UserNutrition {
    // user profile
    val userProfile = this
    val height = userProfile.height // tinggi badan
    val weight = userProfile.weight // berat badan
    val age = userProfile.age // umur
    val isWoman =
        userProfile.gender.lowercase() == "wanita" // menentukan pria atau wanita
    // menghitung bmr berdasarkan tinggi, berat badan, umur dan jenis kelamin
    val bmr = if (!isWoman)
        66 + (13.7 * weight) + (5 * height) - (6.8 * age)
    else 655 + (9.6 * weight) + (1.8 * height) - (4.7 * age)
    // mengambil nilai faktor level aktivitas
    val activityFactor =
        ACTIVITY_FACTOR[userProfile.activityLevel.lowercase()] ?: 1.0
    // mengambil nilai faktor goals
    val goalsFactor = GOALS_FACTOR[userProfile.goal] ?: 1.0
    // menghitung maksimal kalori
    val userMaxDailyCalories =
        (bmr * activityFactor * goalsFactor).toFloat()
    // menghitung maksimal protein (15% dari maksimal kalori_
    val userMaxDailyProtein = 0.15f * userMaxDailyCalories
    // menghitung maksimal lemak (25% dari maksimal kalori)
    val userMaxDailyFat = 0.25f * userMaxDailyCalories
    // menghitung maksimal karbohidrat (60% dari maksimal kalori)
    val userMaxDailyCarbohydrate = 0.6f * userMaxDailyCalories

    return userNutrition.copy(
        maxDailyCalories = userMaxDailyCalories,
        maxDailyProtein = userMaxDailyProtein,
        maxDailyFat = userMaxDailyFat,
        maxDailyCarbohydrate = userMaxDailyCarbohydrate,
    )
}

val String.asDateAndMonth: String
    get() =
        DateTimeFormatter.ofPattern("dd/MM/yyyy").run {
            val locale = Locale("id", "ID")
            LocalDate.parse(this@asDateAndMonth, this@run)
                .format(DateTimeFormatter.ofPattern("dd MMMM", locale))
        }
val String.fromLocalDateTimeToDayDateAndMonth: String
    get() = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").run {
        val locale = Locale("id", "ID")
        LocalDate.parse(this@fromLocalDateTimeToDayDateAndMonth, this@run)
            .format(DateTimeFormatter.ofPattern("EEEE, dd MMM", locale))
    }

val String.fromLocalDateToDayDateAndMonth: String
    get() = DateTimeFormatter.ofPattern("dd/MM/yyyy").run {
        val locale = Locale("id", "ID")
        LocalDate.parse(this@fromLocalDateToDayDateAndMonth, this@run)
            .format(DateTimeFormatter.ofPattern("EEEE, dd MMM", locale))
    }

val String.asDay: String
    get() =
        DateTimeFormatter.ofPattern("dd/MM/yyyy").run {
            val locale = Locale("id", "ID")
            LocalDate.parse(this@asDay, this@run)
                .format(DateTimeFormatter.ofPattern("EE", locale))
        }

fun getCurrentDateTimeInISOFormat(): String {
    // Convert current time to Instant
    val currentInstant = Clock.systemDefaultZone()

    // Format Instant to custom Date Time Format without the offset
    val formatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").withLocale(Locale.getDefault())

    return formatter.format(LocalDateTime.now(currentInstant))
}

val Instant.currentLocalDateTimeInBasicISOFormat: String
    get() {
        // Convert current time to Instant
        val currentInstant = Clock.fixed(this, ZoneId.systemDefault())

        // Format Instant to custom Date Time Format without the offset
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE

        return formatter.format(LocalDateTime.now(currentInstant))
    }

inline fun View.afterMeasured(crossinline block: () -> Unit) {
    if (measuredWidth > 0 && measuredHeight > 0) block()
    else viewTreeObserver.addOnGlobalLayoutListener {
        if (measuredWidth > 0 && measuredHeight > 0) {
            block()
        }
    }
}

fun String.isHttps(): Boolean =
    Regex("^https://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(?:[/?#][^ \\t\\n\\r]*|\$)").matches(
        input = this
    )