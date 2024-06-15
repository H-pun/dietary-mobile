package org.cisnux.mydietary.domain.models

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserProfileDetailTest {
    private lateinit var userProfileDetail: UserProfileDetail

    @BeforeEach
    fun setUp() {
        userProfileDetail = UserProfileDetail(
            id = "2",
            username = "test",
            age = 20,
            weight = 60f,
            height = 170f,
            waistCircumference = 80f,
            gender = "male",
            goal = "menurunkan berat badan",
            weightTarget = 50f,
            activityLevel = "low active",
            emailAddress = "test@example.com",
            userAccountId = "1",
            isVerified = true
        )
    }

    @Test
    fun `initialize test`() {
        Assertions.assertNotNull(userProfileDetail)
    }

    @Test
    fun `calculate max daily nutrition test`() {
        // arrange
        val userNutrition = UserNutrition(
            totalCaloriesToday = 120f,
            totalProteinToday = 50.6f,
            totalFatToday = 27.2f,
            totalCarbohydrateToday = 21.2f
        )
        val expectedUserNutrition = userNutrition.copy(
            maxDailyCalories = 1982.475f,
            maxDailyProtein = 297.37125f,
            maxDailyFat = 495.61874f,
            maxDailyCarbohydrate = 1189.485f,
        )

        // act
        val actualUserNutrition = userProfileDetail.calculateMaxDailyNutrition(
            userNutrition = userNutrition
        )

        // assert
        Assertions.assertEquals(expectedUserNutrition, actualUserNutrition)
    }
}