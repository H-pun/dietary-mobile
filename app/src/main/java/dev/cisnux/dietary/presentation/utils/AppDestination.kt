package dev.cisnux.dietary.presentation.utils

import androidx.compose.runtime.Immutable

@Immutable
sealed class AppDestination(val route: String) {
    data object HomeRoute : AppDestination(route = "home")
    data object ReportRoute : AppDestination(route = "report")
    data object MyProfileRoute : AppDestination(route = "my_profile")
    data object FoodScannerRoute : AppDestination(route = "food_scanner")
    data object ScannerResultRoute : AppDestination(route = "scanner_result?foodPicture={foodPicture}")
    data object SignInRoute : AppDestination(route = "sign_in")
    data object SignUpRoute : AppDestination(route = "signup")
    data object AddUserProfileRoute : AppDestination(route = "add_user_profile")
    data object ResetPasswordRoute : AppDestination(route = "reset_password")
    data object NewPasswordRoute : AppDestination(route = "new_password")
}