package org.cisnux.mydietary.presentation.navigation

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import org.cisnux.mydietary.utils.AppDestination

class NavComponentAction(
    navController: NavHostController,
) {
    val navigateToFoodScanner: () -> Unit = {
        navController.navigate(
            route = AppDestination.FoodScannerRoute.route
        )
    }
    val navigateToFoodDiaryDetail: (foodDiaryId: String) -> Unit = { foodDiaryId ->
        navController.navigate(
            route = AppDestination.DiaryDetailRoute.createRouteUrl(
                foodDiaryId = foodDiaryId
            )
        )
    }
    val navigateUp: () -> Unit = {
        navController.navigateUp()
    }
    val navigateToHome: (currentRoute: String) -> Unit = { currentRoute ->
        navController.navigate(route = AppDestination.HomeRoute.route) {
            popUpTo(currentRoute) {
                inclusive = true
            }
        }
    }
    val navigateToLanding: () -> Unit = {
        navController.navigate(route = AppDestination.LandingRoute.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = true
            }
        }
    }
    val navigateToSignUp: () -> Unit = {
        navController.navigate(AppDestination.SignUpRoute.route)
    }
    val navigateToSignIn: (currentRoute: String) -> Unit = { currentRoute ->
        navController.navigate(route = AppDestination.SignInRoute.route) {
            popUpTo(currentRoute) {
                inclusive = true
            }
        }
    }
    val navigateToSignOut: () -> Unit = {
        navController.navigate(route = AppDestination.SignInRoute.route) {
            popUpTo(AppDestination.MyProfileRoute.route) {
                inclusive = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
    val navigateToResetPassword: () -> Unit = {
        navController.navigate(AppDestination.ResetPasswordRoute.route)
    }
    val navigateToAddMyProfile: (currentRoute: String) -> Unit = { currentRoute ->
        navController.navigate(route = AppDestination.AddMyProfileRoute.route) {
            popUpTo(currentRoute) {
                inclusive = true
            }
        }
    }
    val bottomNavigation: (destination: AppDestination, currentRoute: AppDestination) -> Unit =
        { destination, currentRoute ->
            if (destination.route != currentRoute.route)
                navController.navigate(route = destination.route) {
                    popUpTo(currentRoute.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
        }
    val takePictureFromGallery: (launcher: ActivityResultLauncher<Intent>) -> Unit = { launcher ->
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcher.launch(chooser)
    }
}

@Composable
fun rememberNavComponentAction(
    navController: NavHostController,
): NavComponentAction = remember(navController) {
    NavComponentAction(navController = navController)
}
