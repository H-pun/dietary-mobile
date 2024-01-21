package dev.cisnux.dietary.presentation.navigation

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import dev.cisnux.dietary.presentation.utils.AppDestination

class NavComponentAction(
    navController: NavHostController,
) {
    val navigateToFoodScanner: () -> Unit = {
        navController.navigate(route = AppDestination.FoodScannerRoute.route)
    }
    val navigateToScannerResult: (
        foodPicture: String,
    ) -> Unit = { foodPicture ->
        navController.navigate(route = AppDestination.ScannerResultRoute.createRouteUrl(foodPicture = foodPicture))
    }
    val navigateUp: () -> Unit = {
        navController.navigateUp()
    }
    val navigateToHome: () -> Unit = {
        navController.navigate(route = AppDestination.HomeRoute.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            restoreState = true
            launchSingleTop = true
        }
    }
    val navigateToSignUp: () -> Unit = {
        navController.navigate(AppDestination.SignUpRoute.route)
    }
    val navigateToSignIn: () -> Unit = {
        navController.navigate(route = AppDestination.SignInRoute.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            restoreState = true
            launchSingleTop = true
        }
    }
    val navigateToResetPassword: () -> Unit = {
        navController.navigate(AppDestination.ResetPasswordRoute.route)
    }
    val navigateToAddMyProfile: () -> Unit = {
        navController.navigate(route = AppDestination.AddMyProfileRoute.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            restoreState = true
            launchSingleTop = true
        }
    }
    val bottomNavigation: (destination: AppDestination, currentRoute: AppDestination) -> Unit =
        { destination, currentRoute ->
            if (destination.route != currentRoute.route)
                navController.navigate(route = destination.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    restoreState = true
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
