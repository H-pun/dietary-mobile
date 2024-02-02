package dev.cisnux.dietary.utils

import androidx.compose.runtime.Immutable

@Immutable
sealed class AppDestination(val route: String) {
    data object HomeRoute : AppDestination(route = "home")
    data object MyProfileRoute : AppDestination(route = "my_profile")
    data object FoodScannerRoute :
        AppDestination(route = "food_scanner?title={title}&foodDiaryCategory={foodDiaryCategory}") {
        fun createRouteUrl(title: String, foodDiaryCategory: String) =
            "food_scanner?title=$title&foodDiaryCategory=$foodDiaryCategory"
    }

    data object DiaryDetailRoute :
        AppDestination(route = "food_diary/{foodDiaryId}") {
        fun createRouteUrl(foodDiaryId: String) =
            "food_diary/$foodDiaryId"
    }

    data object ScannerResultRoute :
        AppDestination(route = "scanner_result?title={title}&foodDiaryCategory={foodDiaryCategory}") {
        fun createRouteUrl(title: String, foodDiaryCategory: String): String =
            "scanner_result?title=$title&foodDiaryCategory=$foodDiaryCategory"
    }

    data object AddDiaryRoute : AppDestination(route = "add_diary")
    data object SignInRoute : AppDestination(route = "sign_in")
    data object SignUpRoute : AppDestination(route = "sign_up")
    data object AddMyProfileRoute : AppDestination(route = "add_my_profile")
    data object ResetPasswordRoute : AppDestination(route = "reset_password")
    data object NewPasswordRoute :
        AppDestination(route = "new_password/{token}") {
        val deepLinkPattern = "https://www.dietary.xyz/$route"
        fun createDeepLinkUrl(token: String): String =
            "new_password/$token"
    }

    data object SplashRoute : AppDestination(route = "splash")
    data object LandingRoute : AppDestination(route = "landing")
    data object IntroductionRoute : AppDestination(route = "introduction")
}