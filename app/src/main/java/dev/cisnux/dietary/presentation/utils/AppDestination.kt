package dev.cisnux.dietary.presentation.utils

import androidx.compose.runtime.Immutable

@Immutable
sealed class AppDestination(val route: String) {
    data object HomeRoute : AppDestination(route = "home")
    data object ReportRoute : AppDestination(route = "report")
    data object MyProfileRoute : AppDestination(route = "my_profile")
    data object FoodScannerRoute : AppDestination(route = "food_scanner")
    data object ScannerResultRoute :
        AppDestination(route = "scanner_result?foodPicture={foodPicture}") {
        fun createRouteUrl(foodPicture: String): String = "scanner_result?foodPicture=$foodPicture"
    }

    data object SignInRoute : AppDestination(route = "sign_in")
    data object SignUpRoute : AppDestination(route = "sign_up")
    data object AddMyProfileRoute : AppDestination(route = "add_my_profile")
    data object ResetPasswordRoute : AppDestination(route = "reset_password")
    data object NewPasswordRoute :
        AppDestination(route = "new_password?emailAddress={emailAddress}") {
        val deepLinkPattern = "https://www.dietary.xyz/$route"
        fun createDeepLinkUrl(emailAddress: String): String =
            "new_password?emailAddress=$emailAddress"
    }
}