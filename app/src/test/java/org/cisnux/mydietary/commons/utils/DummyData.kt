package org.cisnux.mydietary.commons.utils

import arrow.core.Either
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.cisnux.mydietary.commons.utils.Failure
import org.cisnux.mydietary.data.remotes.bodyrequests.ChangePasswordBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.DietProgressBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.FoodDiaryBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.GetFoodDiaryParams
import org.cisnux.mydietary.data.remotes.bodyrequests.GoogleTokenBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.NewPasswordBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.NewUserProfileBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.ReportBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.ResetPasswordBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.UpdateEmailBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.UpdateUserProfileWithUsernameBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.UserAccountBodyRequest
import org.cisnux.mydietary.data.remotes.bodyrequests.VerifyEmailBodyRequest
import org.cisnux.mydietary.data.remotes.responses.AddedUserAccountResponse
import org.cisnux.mydietary.data.remotes.responses.BoundResponse
import org.cisnux.mydietary.data.remotes.responses.BoundResponses
import org.cisnux.mydietary.data.remotes.responses.CommonResponse
import org.cisnux.mydietary.data.remotes.responses.DetectedFoodResponse
import org.cisnux.mydietary.data.remotes.responses.DetectedResponse
import org.cisnux.mydietary.data.remotes.responses.DietProgressResponse
import org.cisnux.mydietary.data.remotes.responses.FoodDiaryDetailResponse
import org.cisnux.mydietary.data.remotes.responses.FoodDiaryResponse
import org.cisnux.mydietary.data.remotes.responses.KeywordResponse
import org.cisnux.mydietary.data.remotes.responses.NutrientResponse
import org.cisnux.mydietary.data.remotes.responses.OptionResponse
import org.cisnux.mydietary.data.remotes.responses.PredictedFoodDetailResponse
import org.cisnux.mydietary.data.remotes.responses.QuestionResponse
import org.cisnux.mydietary.data.remotes.responses.ServingResponse
import org.cisnux.mydietary.data.remotes.responses.UserProfileDetailResponse
import org.cisnux.mydietary.data.remotes.responses.WeeklyReportResponse
import java.io.File
import java.nio.file.Path

const val DUMMY_ACCESS_TOKEN = "123"
const val DUMMY_USER_ACCOUNT_ID = "123"
const val DUMMY_DATE = "2022-01-01"
const val DUMMY_FOOD_DIARY_ID = "123"
val dummyFile: File = Path.of("file.jpg").toFile()
val dummyGetFoodDiaryParams = GetFoodDiaryParams(
    userId = DUMMY_USER_ACCOUNT_ID,
    date = DUMMY_DATE,
    query = "query",
    category = "category"
)

val dummyUserAccountBodyRequest = UserAccountBodyRequest(
    emailAddress = "test@example.com",
    password = "123",
)

val dummyReportBodyRequest = ReportBodyRequest(
    userId = DUMMY_USER_ACCOUNT_ID,
    reportType = "weekly"
)

val expectedGetFoodDiaryReports200Response = Either.Right(
    listOf(
        WeeklyReportResponse(
            date = DUMMY_DATE,
            averageCarbohydrate = 211f,
            averageFat = 510f,
            averageProtein = 211f,
            averageCalories = 250.2f,
        ),
        WeeklyReportResponse(
            date = DUMMY_DATE,
            averageCarbohydrate = 271f,
            averageFat = 515f,
            averageProtein = 221f,
            averageCalories = 210.2f,
        )
    )
)

val dummyGetFoodDiaryReports200ResponseJson = Json.encodeToString(
    CommonResponse(message = "success", data = expectedGetFoodDiaryReports200Response.value)
)

val dummyNewUserProfileBodyRequest = NewUserProfileBodyRequest(
    userAccountId = "123",
    username = "test",
    age = 20,
    weight = 60f,
    height = 170f,
    waistCircumference = 80f,
    gender = "male",
    goal = "menurunkan berat badan",
    weightTarget = 50f,
    activityLevel = "sedang",
)

val dummyUpdateUserProfileBodyRequest = UpdateUserProfileWithUsernameBodyRequest(
    id = "1",
    username = "test",
    age = 20,
    weight = 60f,
    height = 170f,
    waistCircumference = 80f,
    gender = "male",
    goal = "menurunkan berat badan",
    weightTarget = 50f,
    activityLevel = "sedang",
)

val dummyNewPasswordBodyRequest = NewPasswordBodyRequest(
    code = "123",
    emailAddress = "test@example.com",
    newPassword = "newpassword",
)

val dummySendResetPasswordBodyRequest = ResetPasswordBodyRequest(
    emailAddress = "test@example.com",
)

val dummyGoogleTokenBodyRequest = GoogleTokenBodyRequest(
    idToken = "123",
)

val dummyChangePasswordBodyRequest = ChangePasswordBodyRequest(
    id = "1",
    oldPassword = "oldpassword",
    newPassword = "newpassword",
)

val dummyUpdateEmailBodyRequest = UpdateEmailBodyRequest(
    id = "1",
    emailAddress = "test@example.com",
)

val dummyVerifyEmailBodyRequest = VerifyEmailBodyRequest(
    emailAddress = "test@example.com"
)

val dummyDietProgressBodyRequest = DietProgressBodyRequest(
    id = DUMMY_USER_ACCOUNT_ID,
    waistCircumference = 1000f,
    weight = 1000f,
    updatedAt = "2021-01-01T00:00:00.000Z"
)

val dummyFoodDiaryBodyRequest = FoodDiaryBodyRequest(
    userAccountId = DUMMY_USER_ACCOUNT_ID,
    title = "title",
    feedback = listOf("not bad", "not good"),
    totalFat = 100f,
    totalProtein = 210f,
    totalCalories = 211f,
    totalCarbohydrate = 22.7f,
    foodPicture = Path.of("food.jpg").toFile(),
    category = "food",
    foodIds = listOf("1", "2", "3"),
    addedAt = "2021-01-01T00:00:00.000Z"
)

val expectedDetected200Response = Either.Right(
    DetectedResponse(
        totalFat = 510f,
        totalCalories = 211f,
        totalProtein = 200f,
        totalCarbohydrate = 41f,
        imagePath = "file.jpg",
        foods = listOf(
            DetectedFoodResponse(
                foodDetail = PredictedFoodDetailResponse(
                    id = "1",
                    name = "food",
                    serving = listOf(
                        ServingResponse(
                            calories = 200f,
                            carbohydrate = 100f,
                            sugar = 20.5f,
                            fat = 50.2f,
                            protein = 27.5f,
                            metricServingAmount = 100f
                        ),
                    ),
                    questions = listOf(
                        QuestionResponse(
                            id = "1",
                            options = listOf(
                                OptionResponse(
                                    id = "1",
                                    answer = "fatty",
                                    reference = "1"
                                )
                            ),
                            question = "why?",
                            type = "question"
                        )
                    )
                ),
                bounds = BoundResponses(
                    bound = BoundResponse(
                        x = 2.2,
                        y = 2.7,
                        width = 50.5,
                        height = 12.5
                    )
                )
            )
        )
    )
)

val dummyDetected200ResponseJson = Json.encodeToString(
    CommonResponse(
        message = "success",
        data = expectedDetected200Response.value
    )
)

val expectedAddedFoodDiary201Response = Either.Right(
    "food diary has already been added"
)

val dummyAddedFoodDiary201ResponseJson = Json.encodeToString(
    CommonResponse(
        message = "success",
        data = expectedAddedFoodDiary201Response.value
    )
)

val expectedGetFoodDiary200Response = Either.Right(
    FoodDiaryDetailResponse(
        id = "1",
        totalCalories = 100f,
        status = "Good",
        filePath = "test1.jpg",
        title = "title1",
        foods = listOf(
            PredictedFoodDetailResponse(
                id = "1",
                name = "food",
                serving = listOf(
                    ServingResponse(
                        calories = 200f,
                        carbohydrate = 100f,
                        sugar = 20.5f,
                        fat = 50.2f,
                        protein = 27.5f,
                        metricServingAmount = 100f
                    ),
                ),
                questions = listOf()
            )
        ),
        feedback = listOf(),
        totalFat = 100f,
        totalProtein = 500f,
        totalCarbohydrate = 240f
    )
)

val dummyGetFoodDiaryResponse200Json = Json.encodeToString(
    CommonResponse(
        message = "success",
        data = expectedGetFoodDiary200Response.value
    )
)

val dummyDeleteFoodDiaryResponse200Json = Json.encodeToString(CommonResponse(message = "success", data = "the food diary has been deleted"))

val dummyNutrient200ResponseJson = """
    {
        "message": "success",
        "data": {
            "calories": 2000,
            "protein": 100,
            "fat": 50,
            "carbohydrate": 150
        }
    }
""".trimIndent()

val expectedGetFoodDiaries200Response = Either.Right(
    listOf(
        FoodDiaryResponse(
            id = "1",
            title = "title2",
            addedAt = "2020-01-01T00:00:00.000Z",
            category = "makan siang",
            totalCalories = 120f,
            filePath = "test1.jpg"
        ),
        FoodDiaryResponse(
            id = "2",
            title = "title1",
            addedAt = "2021-01-01T00:00:00.000Z",
            totalCalories = 100f,
            filePath = "test2.jpg",
            category = "makan malam"
        )
    )
)

val expectedGetSuggestionKeywords200Response = Either.Right(
    expectedGetFoodDiaries200Response.value.map {
        KeywordResponse(id = it.id, title = it.title)
    }
)

val dummyGetFoodDiaries200ResponseJson = Json.encodeToString(
    CommonResponse(
        message = "success",
        data = expectedGetFoodDiaries200Response.value
    )
)

val dummyGetDietProgress200ResponseJson = """
    {
        "message": "success",
        "data": [
            {
                "weight": 80.2,
                "waistCircumference": 87.2,
                "updatedAt": "2021-01-01T00:00:00.000Z"
            },
            {
                "weight": 91.2,
                "waistCircumference": 82.2,
                "updatedAt": "2022-01-01T00:00:00.000Z"
            }
        ]
    }
""".trimIndent()

val dummyUpdateDietProgress200ResponseJson = """
    {
        "message": "success",
        "data": "you have updated your diet progress"
    }
""".trimIndent()

val dummyAddedUserAccount200ResponseJson = """
    {
        "message": "success",
        "data": {
            "id": "1",
            "email": "test@example.com",
            "appToken": "123"
        } 
    }
""".trimIndent()

val dummyAddedUserAccount201ResponseJson = """
    {
        "message": "created",
        "data": {
            "id": "1",
            "email": "test@example.com",
        } 
    }
""".trimIndent()

val dummyVerifyEmail200ResponseJson = """
    {
        "message": "success",
        "data": "the verification email has been sent"
    }
""".trimIndent()

val dummyChangePassword200ResponseJson = """
    {
        "message": "success",
        "data": "your password has been changed"
    }
""".trimIndent()

val dummyForgotPassword200ResponseJson = """
    {
        "message": "your password has been changed",
        "data": null
    }
""".trimIndent()

val dummyForgotPassword401ResponseJson = """
    {
        "message": "your OTP has expired",
        "data": null
    }
""".trimIndent()

val dummySendResetPassword200ResponseJson = """
    {
        "message": "the OTP to reset your password has been sent",
        "data": null
    }
""".trimIndent()

val dummySendResetPassword401ResponseJson = """
    {
        "message": "Your email address has not been verified",
        "data": null
    }
""".trimIndent()


val dummyAddUserProfile200ResponseJson = """
    {
        "message": "success",
        "data": "your profile has been added"
    }
""".trimIndent()

val dummyUpdateUserProfile200ResponseJson = """
    {
        "message": "success",
        "data": "your profile has been updated
    }
""".trimIndent()

val dummyUpdateEmail200ResponseJson = """
    {
        "message": "success",
        "data": "the email address has been updated"
    }
""".trimIndent()

val dummyEmailAddress400ResponseJson = """
    {
        "message": "the email address has been taken",
        "data": null
    }
""".trimIndent()

val dummyUserProfile400ResponseJson = """
    {
        "message": "the username has been taken",
        "data": null
    }
""".trimIndent()

val dummyUserAccount404ResponseJson = """
    {
        "message": "the user account has not been found",
        "data": null
    }
""".trimIndent()

val dummyFoodDiary404ResponseJson = """
    {
        "message": "the food diary has not been found",
        "data": null
    }
""".trimIndent()

val dummyExpiredAccessTokenResponseJson = """
    {
        "message": "the access token has expired",
        "data": null
    }
""".trimIndent()

val dummyUserProfileDetail200ResponseJson = """
    {
        "message": "success",
        "data": {
            "id": "2"
            "idUser": "1",
            "email": "test@example.com"
            "username": "test",
            "age": 20,
            "weight": 60,
            "height": 170,
            "waistCircumference": 80,
            "gender": "male",
            "goal": "menurunkan berat badan",
            "weightTarget": 50,
            "activityLevel": "sedang",
            "isVerified": true
        }
    }
""".trimIndent()

val dummyChangePassword401ResponseJson = """
    {
        "message": "your old password is incorrect",
        "data": null
    }
""".trimIndent()

val dummyAddedUserAccountIncorrectPasswordResponseJson = """
    {
        "message": "the password is incorrect",
        "data": null
    }
""".trimIndent()

val dummyAddedUserAccountIncorrectEmailAddressResponseJson = """
    {
        "message": "the email address is incorrect",
        "data": null
    }
""".trimIndent()

val dummyInternalServerErrorResponseJson = """
    {
        "message": "our server has encountered an error",
        "data": null
    }
""".trimIndent()

val expectedAddedUserAccount200Response = Either.Right(
    AddedUserAccountResponse(
        id = "1",
        emailAddress = "test@example.com",
        accessToken = "123"
    )
)

val expectedAddedUserAccountIncorrectPasswordResponse = Either.Left(
    Failure.UnauthorizedFailure(message = "the password is incorrect")
)

val expectedSendResetPassword200Response =
    Either.Right("the OTP to reset your password has been sent")

val expectedAddedUserAccountIncorrectEmailAddressResponse = Either.Left(
    Failure.UnauthorizedFailure(message = "the email address is incorrect")
)

val expectedEmail400Response = Either.Left(
    Failure.BadRequestFailure(message = "the email address has been taken")
)

val expectedUserProfile400Response = Either.Left(
    Failure.BadRequestFailure(message = "the username has been taken")
)

val expectedUserAccount404Response = Either.Left(
    Failure.NotFoundFailure(message = "the user account has not been found")
)

val expectedFoodDiary404Response = Either.Left(
    Failure.NotFoundFailure(message = "the food diary has not been found")
)

val expectedInternalServerErrorResponse = Either.Left(
    Failure.ServerFailure(message = "our server has encountered an error")
)

val expectedChangePassword401Response = Either.Left(
    Failure.UnauthorizedFailure(message = "your old password is incorrect")
)

val expectedSendResetPassword401Response = Either.Left(
    Failure.UnauthorizedFailure(message = "Your email address has not been verified")
)
val expectedForgotPassword401Response = Either.Left(
    Failure.UnauthorizedFailure(message = "your OTP has expired")
)
val expectedUserProfileDetail200Response = Either.Right(
    UserProfileDetailResponse(
        id = "2",
        username = "test",
        age = 20,
        weight = 60f,
        height = 170f,
        waistCircumference = 80f,
        gender = "male",
        goal = "menurunkan berat badan",
        weightTarget = 50f,
        activityLevel = "sedang",
        emailAddress = "test@example.com",
        userAccountId = "1",
        isVerified = true
    )
)
val expectedGetDietProgress200Response = Either.Right(
    listOf(
        DietProgressResponse(
            weight = 80.2f,
            waistCircumference = 87.2f,
            updatedAt = "2021-01-01T00:00:00.000Z"
        ),
        DietProgressResponse(
            weight = 91.2f,
            waistCircumference = 82.2f,
            updatedAt = "2022-01-01T00:00:00.000Z"
        )
    )
)
val expectedNutrient200Response = Either.Right(
    NutrientResponse(
        calories = 2000f,
        protein = 100f,
        fat = 50f,
        carbohydrate = 150f
    )
)

val expectedVerifyEmail200Response = Either.Right("the verification email has been sent")
val expectedUpdateEmail200Response = Either.Right("the email address has been updated")
val expectedChangePassword200Response = Either.Right("your password has been changed")
val expectedForgotPassword200Response = Either.Right("your password has been changed")
val expectedAddUserProfile200Response = Either.Right("your profile has been added")