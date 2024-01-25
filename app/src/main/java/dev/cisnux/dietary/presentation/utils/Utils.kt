package dev.cisnux.dietary.presentation.utils

fun String.isEmailValid(): Boolean {
    val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\$"
    return Regex(emailRegex).matches(this)
}

fun String.isPasswordSecure(): Boolean = this.trim().length >= 8

fun String.isUsernameValid(): Boolean = !this.contains(' ')

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

const val SplashWaitTimeMillis = 1500L