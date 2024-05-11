package org.cisnux.mydietary.domain.models

import android.graphics.Bitmap

data class FoodDiary(
    val id: String,
    val title: String,
    val date: String,
    val time: String,
    val foodPictureUrl: String?,
    val totalFoodCalories: Float,
) {
    var foodPictureFile: Bitmap? = null
        private set(value) {
            field = value
        }

    constructor(
        id: String,
        title: String,
        date: String,
        time: String,
        foodPictureUrl: String?,
        totalFoodCalories: Float,
        foodPictureFile: Bitmap?
    ) : this(id, title, date, time, foodPictureUrl, totalFoodCalories) {
        this.foodPictureFile = foodPictureFile
    }
}
