package org.cisnux.mydietary.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import org.cisnux.mydietary.presentation.addmyprofile.AddMyProfileScreen
import org.cisnux.mydietary.presentation.devmode.DevModeScreen
import org.cisnux.mydietary.presentation.diarydetail.DiaryDetailScreen
import org.cisnux.mydietary.presentation.foodscanner.FoodScannerScreen
import org.cisnux.mydietary.presentation.home.HomeScreen
import org.cisnux.mydietary.presentation.landing.LandingScreen
import org.cisnux.mydietary.presentation.myprofile.MyProfileScreen
import org.cisnux.mydietary.presentation.newpassword.NewPasswordScreen
import org.cisnux.mydietary.presentation.report.ReportScreen
import org.cisnux.mydietary.presentation.resetpassword.ResetPasswordScreen
import org.cisnux.mydietary.presentation.signin.SignInScreen
import org.cisnux.mydietary.presentation.signup.SignUpScreen
import org.cisnux.mydietary.presentation.splash.SplashScreen
import org.cisnux.mydietary.presentation.ui.components.NotificationDialog
import org.cisnux.mydietary.utils.AppDestination

@Composable
fun DietaryNavGraph(
    navController: NavHostController = rememberNavController(),
    navComponentAction: NavComponentAction = rememberNavComponentAction(
        navController = navController,
    ),
) {
    var isHomeNavigationInclusive by rememberSaveable {
        mutableStateOf(false)
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    NotificationDialog(snackbarHostState = snackbarHostState)
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
            AddMyProfileScreen(
                navigateToHome = navComponentAction.navigateToHome,
                navigateToSignIn = navComponentAction.navigateToSignIn
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
                navArgument(name = "email_address") {
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
                onFabFoodScanner = navComponentAction.navigateToFoodScanner,
                navigateToDiaryDetail = navComponentAction.navigateToFoodDiaryDetail,
                navigateToSignIn = navComponentAction.navigateToSignIn
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
                navigateToSignIn = navComponentAction.navigateToSignOut
            )
        }
        composable(
            route = AppDestination.ReportRoute.route,
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
            ReportScreen(
                navigateForBottomNav = navComponentAction.bottomNavigation,
                navigateToSignIn = navComponentAction.navigateToSignIn
            )
        }
        composable(
            route = AppDestination.DiaryDetailRoute.route,
            arguments = listOf(
                navArgument(name = "foodDiaryId") {
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
            DiaryDetailScreen(
                navigateUp = {
                    if (!isHomeNavigationInclusive)
                        navComponentAction.navigateUp()
                    else navComponentAction.navigateToHome(AppDestination.DiaryDetailRoute.route)
                },
                navigateToSignIn = navComponentAction.navigateToSignIn
            )
        }
        composable(
            route = AppDestination.FoodScannerRoute.route,
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
                navigateFoodDiaryDetail = {
                    isHomeNavigationInclusive = true
                    navComponentAction.navigateToFoodDiaryDetail(it)
                },
                onGalleryButton = navComponentAction.takePictureFromGallery,
                navigateToSignIn = navComponentAction.navigateToSignIn
            )
        }
        composable(
            route = AppDestination.DevModeRoute.route,
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
            },
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = AppDestination.DevModeRoute.deepLinkPattern
                },
            ),
        ) {
            DevModeScreen(navigateUp = navComponentAction.navigateUp)
        }
    }
}