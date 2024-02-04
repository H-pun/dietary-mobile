package dev.cisnux.dietary.data.remotes.responses

import java.io.File

// The body request to add a new food diary
data class FoodDiaryBodyRequest(
    val title: String,
    // the category of food diary can be sarapan, makan siang or makan malam.
    val category: String,
    val addedAt: Long = System.currentTimeMillis(),
    val foodPicture: File
)
