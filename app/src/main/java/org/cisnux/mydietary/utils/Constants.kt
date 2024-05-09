package org.cisnux.mydietary.utils

const val DIETARY_API = "https://api.dietary.cloud"
const val IMAGE_LOCATION = "https://storage.googleapis.com/dietary-app-407405.appspot.com"
// nilai faktor untuk masing-masing level aktivitas
val ACTIVITY_FACTOR = mapOf(
    "sedentary" to 0.8,
    "low active" to 0.9,
    "active" to 1.0, // default
    "very active" to 1.1
)
// nilai faktor untuk masing-masing goals
val GOALS_FACTOR = mapOf(
    "menurunkan berat badan" to 0.9,
    "pertahankan berat saat ini" to 1.0, // default
    "menambah berat badan" to 1.1,
)

// Jetpack Compose
const val SplashWaitTimeMillis = 1500L