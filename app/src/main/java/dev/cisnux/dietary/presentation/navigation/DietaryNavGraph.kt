package dev.cisnux.dietary.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import dev.cisnux.dietary.presentation.adddiary.AddDiaryScreen
import dev.cisnux.dietary.presentation.addmyprofile.AddMyProfileScreen
import dev.cisnux.dietary.presentation.foodscanner.FoodScannerScreen
import dev.cisnux.dietary.presentation.home.HomeScreen
import dev.cisnux.dietary.presentation.landing.LandingScreen
import dev.cisnux.dietary.presentation.myprofile.MyProfileScreen
import dev.cisnux.dietary.presentation.newpassword.NewPasswordScreen
import dev.cisnux.dietary.presentation.resetpassword.ResetPasswordScreen
import dev.cisnux.dietary.presentation.scannerresult.ScannerResultScreen
import dev.cisnux.dietary.presentation.signin.SignInScreen
import dev.cisnux.dietary.presentation.signup.SignUpScreen
import dev.cisnux.dietary.presentation.splash.SplashScreen
import dev.cisnux.dietary.utils.AppDestination

@Composable
fun DietaryNavGraph(
    navController: NavHostController = rememberNavController(),
    navComponentAction: NavComponentAction = rememberNavComponentAction(
        navController = navController,
    ),
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.SplashRoute.route
    ) {
        composable(
            route = AppDestination.SplashRoute.route,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        100, easing = LinearEasing
                    )
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        100, easing = LinearEasing
                    )
                )
            },
        ) {
            SplashScreen(
                navigateToLanding = navComponentAction.navigateToLanding,
                navigateToSignIn = navComponentAction.navigateToSignIn,
                navigateToAddMyProfile = navComponentAction.navigateToAddMyProfile,
                navigateToHome = navComponentAction.navigateToHome
            )
        }
        composable(
            route = AppDestination.LandingRoute.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(durationMillis = 300)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(durationMillis = 300)
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                )
            }
        ) {
            LandingScreen(
                navigateToSignIn = navComponentAction.navigateToSignIn,
                navigateToIntroduction = {},
            )
        }
        composable(
            route = AppDestination.SignInRoute.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(durationMillis = 300)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(durationMillis = 300)
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                )
            }
        ) {
            SignInScreen(
                navigateToHome = navComponentAction.navigateToHome,
                navigateToAddMyProfile = navComponentAction.navigateToAddMyProfile,
                navigateToResetPassword = navComponentAction.navigateToResetPassword,
                navigateToSignUp = navComponentAction.navigateToSignUp
            )
        }
        composable(
            route = AppDestination.SignUpRoute.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(durationMillis = 300)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(durationMillis = 300)
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                )
            }
        ) {
            SignUpScreen(navigateToSignIn = navComponentAction.navigateUp)
        }
        composable(
            route = AppDestination.ResetPasswordRoute.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(durationMillis = 300)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(durationMillis = 300)
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                )
            }
        ) {
            ResetPasswordScreen(navigateToSignIn = navComponentAction.navigateUp)
        }
        composable(
            route = AppDestination.AddMyProfileRoute.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(durationMillis = 300)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(durationMillis = 300)
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                )
            }
        ) {
            AddMyProfileScreen(navigateToHome = navComponentAction.navigateToHome)
        }
        composable(
            route = AppDestination.SignUpRoute.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(durationMillis = 300)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(durationMillis = 300)
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                )
            }
        ) {
            SignUpScreen(navigateToSignIn = navComponentAction.navigateUp)
        }
        composable(
            route = AppDestination.NewPasswordRoute.route,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        100, easing = LinearEasing
                    )
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        100, easing = LinearEasing
                    )
                )
            },
            arguments = listOf(
                navArgument(name = "token") {
                    nullable = false
                    type = NavType.StringType
                },
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = AppDestination.NewPasswordRoute.deepLinkPattern
                },
            ),
        ) {
            NewPasswordScreen(navigateToSignIn = navComponentAction.navigateToSignIn)
        }
        composable(
            route = AppDestination.HomeRoute.route,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        100, easing = LinearEasing
                    )
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        100, easing = LinearEasing
                    )
                )
            },
            popEnterTransition = {
                fadeIn(
                    animationSpec = tween(
                        100, easing = LinearEasing
                    )
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(
                        100, easing = LinearEasing
                    )
                )
            }
        ) {
            HomeScreen(
                navigateForBottomNav = navComponentAction.bottomNavigation,
                onFabFoodScanner = navComponentAction.navigateToAddDiary
            )
        }
        composable(
            route = AppDestination.MyProfileRoute.route,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        100, easing = LinearEasing
                    )
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        100, easing = LinearEasing
                    )
                )
            },
            popEnterTransition = {
                fadeIn(
                    animationSpec = tween(
                        100, easing = LinearEasing
                    )
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(
                        100, easing = LinearEasing
                    )
                )
            }
        ) {
            MyProfileScreen(
                navigateForBottomNav = navComponentAction.bottomNavigation,
                navigateToSignIn = navComponentAction.navigateToSignInForSignOut
            )
        }
        composable(
            route = AppDestination.AddDiaryRoute.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(durationMillis = 300)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(durationMillis = 300)
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                )
            }
        ) {
            AddDiaryScreen(
                onNavigateUp = navComponentAction.navigateUp,
                navigateToFoodScanner = navComponentAction.navigateToFoodScanner
            )
        }
        composable(
            route = AppDestination.FoodScannerRoute.route,
            arguments = listOf(
                navArgument(name = "title") {
                    nullable = false
                    type = NavType.StringType
                },
                navArgument(name = "foodDiaryCategory") {
                    nullable = false
                    type = NavType.StringType
                },
            ),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(durationMillis = 300)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(durationMillis = 300)
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
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
                navArgument(name = "title") {
                    nullable = false
                    type = NavType.StringType
                },
                navArgument(name = "foodDiaryCategory") {
                    nullable = false
                    type = NavType.StringType
                },
            ),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(durationMillis = 300)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(durationMillis = 300)
                )
            },
            popExitTransition = {
                fadeOut(
                    animationSpec = tween(
                        300, easing = LinearEasing
                    )
                )
            }
        ) {
            ScannerResultScreen(onNavigateUp = navComponentAction.navigateToHomeFromScannerResult)
        }
    }
}