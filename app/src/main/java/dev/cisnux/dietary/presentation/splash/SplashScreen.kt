package dev.cisnux.dietary.presentation.splash

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import dev.cisnux.dietary.R
import dev.cisnux.dietary.presentation.ui.theme.DietaryTheme
import dev.cisnux.dietary.utils.AppDestination
import dev.cisnux.dietary.utils.AuthenticationState
import dev.cisnux.dietary.utils.SplashWaitTimeMillis
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navigateToLanding: () -> Unit,
    navigateToSignIn: (String) -> Unit,
    navigateToAddMyProfile: (String) -> Unit,
    navigateToHome: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val onNavigateToLanding by rememberUpdatedState(navigateToLanding)
    val onNavigateToSignIn by rememberUpdatedState(navigateToSignIn)
    val onNavigateToAddMyProfile by rememberUpdatedState(navigateToAddMyProfile)
    val onNavigateToHome by rememberUpdatedState(navigateToHome)
    val hasLandingShowed by viewModel.hasLandingShowed.collectAsState()
    val authenticationState by viewModel.authenticationState.collectAsState()

    LaunchedEffect(Unit) {
        delay(SplashWaitTimeMillis)
        hasLandingShowed?.let { hasLandingShowed ->
            when {
                !hasLandingShowed -> onNavigateToLanding()
                authenticationState == AuthenticationState.HAS_NOT_SIGNED_IN -> {
                    viewModel.signOut()
                    onNavigateToSignIn(AppDestination.SplashRoute.route)
                }

                authenticationState == AuthenticationState.HAS_NOT_USER_PROFILE -> onNavigateToAddMyProfile(
                    AppDestination.SplashRoute.route
                )

                authenticationState == AuthenticationState.HAS_SIGNED_IN_AND_USER_PROFILE -> onNavigateToHome(
                    AppDestination.SplashRoute.route
                )

                else -> {
                    viewModel.signOut()
                    onNavigateToSignIn(AppDestination.SplashRoute.route)
                }
            }
        }
    }

    val lottieComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.loading_anim))

    SplashContent(lottieComposition = lottieComposition, modifier = modifier)
}

@Preview(
    showBackground = true,
    name = "light",
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.NONE
)
@Composable
private fun SplashContentPreview() {
    val lottieComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.loading_anim))

    DietaryTheme {
        SplashContent(lottieComposition = lottieComposition)
    }
}

@Preview(
    showBackground = true,
    name = "dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.NONE,
    device = "spec:parent=pixel_7_pro,orientation=landscape"
)
@Composable
private fun SplashContentDarkPreview() {
    val lottieComposition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.loading_anim))

    DietaryTheme {
        SplashContent(lottieComposition = lottieComposition)
    }
}

@Composable
private fun SplashContent(lottieComposition: LottieComposition?, modifier: Modifier = Modifier) {
    Scaffold {
        Box(
            contentAlignment = Alignment.Center, modifier = modifier
                .fillMaxSize()
                .padding(it)
        ) {
            LottieAnimation(
                composition = lottieComposition,
                iterations = LottieConstants.IterateForever,
            )
        }
    }
}
