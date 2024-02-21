package dev.cisnux.dietary.utils

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import dev.cisnux.dietary.data.remotes.bodyrequests.UserAccountBodyRequest
import dev.cisnux.dietary.domain.models.FoodDiaryDetail
import dev.cisnux.dietary.domain.models.UserAccount
import dev.cisnux.dietary.domain.models.UserProfile
import dev.cisnux.dietary.presentation.addmyprofile.MyProfile
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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

val UserAccount.userAccountBodyRequest
    get() = UserAccountBodyRequest(
        emailAddress = emailAddress,
        password = password
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

fun String.isHeightOrWeightValid(): Boolean = try {
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

fun Long.withShortDateFormat(): String {
    val date = Date(this)
    val locale = Locale("id", "ID")
    val sdf = SimpleDateFormat("dd MMM", locale);
    return sdf.format(date)
}

fun Long.withFullDateFormat(): String {
    val date = Date(this)
    val locale = Locale("id", "ID")
    return DateFormat.getDateInstance(DateFormat.FULL, locale).format(date)
}

fun Long.withTimeFormat(): String {
    val date = Date(this)
    val locale = Locale("id", "ID")
    return SimpleDateFormat("HH:mm", locale)
        .format(date)
}

val Long.asDays: Long get() = TimeUnit.MILLISECONDS.toDays(this)

val Int.foodDiaryCategory: FoodDiaryCategory
    get() = when (this) {
        0 -> FoodDiaryCategory.BREAKFAST
        1 -> FoodDiaryCategory.LUNCH
        else -> FoodDiaryCategory.DINNER
    }

val Int.reportCategory: ReportCategory
    get() = when (this) {
        0 -> ReportCategory.TODAY
        1 -> ReportCategory.THIS_WEEK
        else -> ReportCategory.THIS_MONTH
    }

val String.questionType: QuestionType
    get() = when (this) {
        "float" -> QuestionType.FLOAT
        "integer" -> QuestionType.INTEGER
        "text" -> QuestionType.TEXT
        else -> QuestionType.BOOLEAN
    }

fun FoodDiaryDetail.isQuestionNotEmpty() = foods.any { food ->
    food.questions?.isNotEmpty() ?: false
}

fun convertISOToMillis(isoDateTime: String): Long {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    val parsedDateTime = LocalDateTime.parse(isoDateTime, formatter)

    // Convert LocalDateTime to milliseconds
    val millis = parsedDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    return millis
}

fun getCurrentDateTimeInISOFormat(): String {
    val currentTimeMillis = System.currentTimeMillis()

    // Get the user's time zone
    val userTimeZone = ZoneId.systemDefault()

    // Convert current time to Instant
    val currentInstant = Instant.ofEpochMilli(currentTimeMillis)

    // Format Instant to ISO Date Time Format in UTC
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .withLocale(Locale.getDefault())
        .withZone(ZoneId.of("UTC"))

    return currentInstant.atZone(userTimeZone).format(formatter)
}