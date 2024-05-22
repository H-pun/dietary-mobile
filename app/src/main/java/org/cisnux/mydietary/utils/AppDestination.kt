package org.cisnux.mydietary.utils

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.core.net.toUri

@Immutable
sealed class AppDestination(val route: String) {
    data object HomeRoute : AppDestination(route = "home")
    data object ReportRoute : AppDestination(route = "report") {
        val deepLinkPattern = "$DIETARY_API/$route"
        fun createDeepLinkUrl(): Uri = "$DIETARY_API/$route".toUri()
    }

    data object MyProfileRoute : AppDestination(route = "my_profile")

    data object FoodScannerRoute : AppDestination(route = "food_scanner")

    data object DiaryDetailRoute :
        AppDestination(route = "food_diary/{foodDiaryId}?isWidget={isWidget}") {
        val deepLinkPattern = "$DIETARY_API/$route"
        fun createRouteUrl(foodDiaryId: String, isWidget: Boolean) =
            "food_diary/$foodDiaryId?isWidget=$isWidget"

        fun createDeepLinkUrl(foodDiaryId: String, isWidget: Boolean): Uri =
            "$DIETARY_API/food_diary/$foodDiaryId?isWidget=$isWidget".toUri()
    }

    data object SignInRoute : AppDestination(route = "sign_in")
    data object SignUpRoute : AppDestination(route = "sign_up")
    data object AddMyProfileRoute : AppDestination(route = "add_my_profile")
    data object ResetPasswordRoute : AppDestination(route = "reset_password")
    data object AccountSecurityRoute : AppDestination(route = "user/email-verified") {
        val deepLinkPattern = "$DIETARY_API/$route"
    }

    data object NewPasswordRoute :
        AppDestination(route = "user/changePassword?email={emailAddress}&otp={code}") {
        fun createRouteUrl(emailAddress: String, code: String): String =
            "user/changePassword?email=${emailAddress}&otp=${code}"
    }

    data object VerifyCodeRoute :
        AppDestination(route = "user/verify-otp?email={emailAddress}&otp={code}") {
        val deepLinkPattern = "$DIETARY_API/$route"
        fun createRouteUrl(emailAddress: String): String =
            "user/verify-otp?email=${emailAddress}"
    }

    data object DevModeRoute :
        AppDestination(route = "dev_mode") {
        val deepLinkPattern = "https://www.dietary.xyz/$route"
    }

    data object SplashRoute : AppDestination(route = "splash")
    data object LandingRoute : AppDestination(route = "landing")
    data object IntroductionRoute : AppDestination(route = "introduction")
}