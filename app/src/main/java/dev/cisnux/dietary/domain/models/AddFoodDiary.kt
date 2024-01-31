package dev.cisnux.dietary.domain.models

import java.io.File

data class AddFoodDiary(
    val title: String,
    val category: String,
    val foodPicture: File,
)
