package dev.cisnux.dietary.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.cisnux.dietary.presentation.foodscanner.FoodScannerScreen
import dev.cisnux.dietary.presentation.home.HomeScreen
import dev.cisnux.dietary.presentation.myprofile.MyProfileScreen
import dev.cisnux.dietary.presentation.report.ReportScreen
import dev.cisnux.dietary.presentation.scannerresult.ScannerResultScreen
import dev.cisnux.dietary.presentation.utils.AppDestination

@Composable
fun DietaryNavGraph(
    navController: NavHostController = rememberNavController(),
    navComponentAction: NavComponentAction = rememberNavComponentAction(
        navController = navController,
    ),
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.HomeRoute.route
    ) {
        composable(
            route = AppDestination.HomeRoute.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(durationMillis = 700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(durationMillis = 700)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(durationMillis = 700)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(durationMillis = 700)
                )
            }
        ) {
            HomeScreen(
                navigateForBottomNav = navComponentAction.bottomNavigation,
                onFabFoodScanner = navComponentAction.navigateToFoodScanner
            )
        }
        composable(
            route = AppDestination.ReportRoute.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(durationMillis = 700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(durationMillis = 700)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(durationMillis = 700)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(durationMillis = 700)
                )
            }
        ) {
            ReportScreen(
                navigateForBottomNav = navComponentAction.bottomNavigation
            )
        }
        composable(
            route = AppDestination.MyProfileRoute.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(durationMillis = 700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(durationMillis = 700)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(durationMillis = 700)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(durationMillis = 700)
                )
            }
        ) {
            MyProfileScreen(
                navigateForBottomNav = navComponentAction.bottomNavigation
            )
        }
        composable(
            route = AppDestination.FoodScannerRoute.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(durationMillis = 700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(durationMillis = 700)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(durationMillis = 700)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(durationMillis = 700)
                )
            }
        ) {
            FoodScannerScreen(
                onNavigateUp = navComponentAction.navigateUp,
                onScannerResult = navComponentAction.navigateToScannerResult,
                onGalleryButton = navComponentAction.takePictureFromGallery
            )
        }
        composable(
            route = AppDestination.ScannerResultRoute.route,
            arguments = listOf(
                navArgument(name = "foodPicture") {
                    nullable = false
                    type = NavType.StringType
                },
            ),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(durationMillis = 700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(durationMillis = 700)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(durationMillis = 700)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(durationMillis = 700)
                )
            }
        ) {
            ScannerResultScreen(onNavigateUp = navComponentAction.navigateUp)
        }
    }
}