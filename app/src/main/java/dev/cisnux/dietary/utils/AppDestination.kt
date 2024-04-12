package dev.cisnux.dietary.utils

import androidx.compose.runtime.Immutable

@Immutable
sealed class AppDestination(val route: String) {
    data object HomeRoute : AppDestination(route = "home")
    data object ReportRoute : AppDestination(route = "report")
    data object MyProfileRoute : AppDestination(route = "my_profile")

    data object FoodScannerRoute : AppDestination(route = "food_scanner")

    data object DiaryDetailRoute :
        AppDestination(route = "food_diary/{foodDiaryId}") {
        fun createRouteUrl(foodDiaryId: String) =
            "food_diary/$foodDiaryId"
    }

    data object AddedDietaryRoute :
        AppDestination(route = "added_dietary?title={title}&foodDiaryCategory={foodDiaryCategory}") {
        fun createRouteUrl(title: String, foodDiaryCategory: String): String =
            "added_dietary?title=$title&foodDiaryCategory=$foodDiaryCategory"
    }

    data object AddDiaryRoute : AppDestination(route = "add_diary")
    data object PredictedResultRoute : AppDestination(route = "predicted_result")
    data object SignInRoute : AppDestination(route = "sign_in")
    data object SignUpRoute : AppDestination(route = "sign_up")
    data object AddMyProfileRoute : AppDestination(route = "add_my_profile")
    data object ResetPasswordRoute : AppDestination(route = "reset_password")
    data object NewPasswordRoute :
        AppDestination(route = "new_password?email_address={email_address}") {
        val deepLinkPattern = "https://www.dietary.xyz/$route"
        fun createDeepLinkUrl(emailAddress: String): String =
            "new_password?email_address$emailAddress"
    }

    data object SplashRoute : AppDestination(route = "splash")
    data object LandingRoute : AppDestination(route = "landing")
    data object IntroductionRoute : AppDestination(route = "introduction")
}