package org.cisnux.mydietary.commons.utils

const val DIETARY_API = "https://api.dietary.cloud"
const val IMAGE_LOCATION = "https://storage.googleapis.com/dietary-app-407405.appspot.com"
const val WEB_CLIENT_ID = "871244307697-17fmcgek3i0ol6jp7a3ik214kjlqdk14.apps.googleusercontent.com"
const val DATE_MONTH_YEAR = "dd/MM/yyyy"
const val SHORT_DATE_MONTH = "dd MMM"
const val FULL_DATE_MONTH = "dd MMMM"
const val DAY_DATE_MONTH = "EEEE, $SHORT_DATE_MONTH"
const val DAY_DATE_MONTH_YEAR = "$DAY_DATE_MONTH yyyy"
const val HOURS_AND_MINUTES = "HH:mm"

// nilai faktor untuk masing-masing level aktivitas
val ACTIVITY_FACTOR = mapOf(
    "sedentary" to 1.2,
    "low active" to 1.375,
    "active" to 1.55, // default
    "very active" to 1.725
)
// nilai faktor untuk masing-masing goals
val GOAL_FACTOR = mapOf(
    "menurunkan berat badan" to 0.9,
    "pertahankan berat saat ini" to 1.0, // default
    "menambah berat badan" to 1.1,
)

// Jetpack Compose
const val SplashWaitTimeMillis = 1500L
