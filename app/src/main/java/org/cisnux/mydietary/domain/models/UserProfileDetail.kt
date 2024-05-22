package org.cisnux.mydietary.domain.models

import org.cisnux.mydietary.utils.ACTIVITY_FACTOR
import org.cisnux.mydietary.utils.GOALS_FACTOR

data class UserProfileDetail(
    val id: String,
    val userAccountId: String,
    val username: String,
    val emailAddress: String,
    val age: Int,
    val weight: Float,
    val height: Float,
    val gender: String,
    val goal: String,
    val weightTarget: Float = 0f,
    val activityLevel: String,
    val waistCircumference: Float,
    val isVerified: Boolean = false
) {
    fun calculateMaxDailyNutrition(userNutrition: UserNutrition): UserNutrition {
        // user profile
        val userProfile = this
        val height = userProfile.height // tinggi badan
        val weight = userProfile.weight // berat badan
        val age = userProfile.age // umur
        val isWoman =
            userProfile.gender.lowercase() == "wanita" || userProfile.gender.lowercase() == "female" // menentukan pria atau wanita
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
}
