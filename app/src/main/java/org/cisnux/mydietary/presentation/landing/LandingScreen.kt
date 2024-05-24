package org.cisnux.mydietary.presentation.landing

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import org.cisnux.mydietary.R
import org.cisnux.mydietary.presentation.ui.theme.DietaryTheme
import org.cisnux.mydietary.utils.AppDestination

@Composable
fun LandingScreen(
    navigateToSignIn: (String) -> Unit,
    navigateToIntroduction: () -> Unit,
    modifier: Modifier = Modifier,
    landingViewModel: LandingViewModel = hiltViewModel()
) {
    LandingContent(
        navigateToSignIn = {
            landingViewModel.updateLandingStatus(true)
            navigateToSignIn(AppDestination.LandingRoute.route)
        },
        navigateToIntroduction = {
            landingViewModel.updateLandingStatus(true)
            navigateToIntroduction()
        },
        modifier = modifier
    )
}

@Preview(
    showBackground = true, name = "light",
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.NONE, device = "id:pixel_7_pro"
)
@Composable
private fun LandingContentPreview() {
    DietaryTheme {
        LandingContent(
            navigateToSignIn = {},
            navigateToIntroduction = {},
        )
    }
}

@Preview(
    showBackground = true, name = "dark and indonesia",
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.NONE, device = "id:pixel_7_pro", locale = "in"
)
@Composable
private fun LandingContentDarkPreview() {
    DietaryTheme {
        LandingContent(
            navigateToSignIn = {},
            navigateToIntroduction = {},
        )
    }
}

@Composable
private fun LandingContent(
    navigateToSignIn: () -> Unit,
    navigateToIntroduction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var isDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }

    Scaffold {
        Column(
            modifier = modifier
                .padding(it)
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(state = scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = R.drawable.introduction_illustration),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.welcome_to))
                    withStyle(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold
                        )
                    ) {
                        append(stringResource(R.string.dietary))
                    }
                    append('\n')
                    append(stringResource(R.string.your_diet_s_best_ally))
                },
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.landing_message),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = navigateToIntroduction,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.get_started))
            }
            Spacer(modifier = Modifier.height(2.dp))
            Button(
                onClick = navigateToSignIn,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
            ) {
                Text(text = stringResource(id = R.string.sign_in))
            }
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.terms_of_use_label_button))
                    append('\n')
                    withStyle(
                        style = SpanStyle(
                            color = if (!isSystemInDarkTheme()) Color.Blue else Color.Cyan,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append(stringResource(R.string.terms_of_use))
                    }
                },
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(MaterialTheme.shapes.extraLarge)
                    .clickable(onClick = { isDialogOpen = true })
                    .padding(4.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            SubcomposeAsyncImage(
                model = "https://platform.fatsecret.com/api/static/images/powered_by_fatsecret_3x.png",
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .width(130.dp)
                    .height(18.dp)
            )
        }
        TermsOfUseDialog(isDialogOpen = isDialogOpen, onDismissRequest = { isDialogOpen = false })
    }
}

@Composable
private fun TermsOfUseDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    isDialogOpen: Boolean = false,
) {
    if (isDialogOpen)
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(
                    text = stringResource(id = R.string.terms_of_use),
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            confirmButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = "OK")
                }
            },
            iconContentColor = Color.Unspecified,
            text = {
                Text(text = stringResource(id = R.string.terms_of_use_description), style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Justify, modifier = Modifier
                    .height(400.dp)
                    .verticalScroll(
                        rememberScrollState()
                    ))
            }
        )
}
