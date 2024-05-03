package dev.cisnux.dietary.data.remotes.bodyrequests

import java.io.File

// The body request to add a new food diary
data class FoodDiaryBodyRequest(
    val userAccountId: String,
    val title: String,
    // the category of food diary can be sarapan, makan siang or makan malam.
    val category: String,
    val addedAt: String,
    val foodPicture: File,
    val feedback: List<String>,
    val foodIds: List<String>,
    val totalCalories: Float,
    val totalProtein: Float,
    val totalFat: Float,
    val totalCarbohydrate: Float,
)
