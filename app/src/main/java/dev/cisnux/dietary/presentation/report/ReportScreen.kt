package dev.cisnux.dietary.presentation.report

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import dev.cisnux.dietary.R
import dev.cisnux.dietary.presentation.ui.components.BottomBar
import dev.cisnux.dietary.presentation.ui.theme.DietaryTheme
import dev.cisnux.dietary.presentation.utils.AppDestination

@Composable
fun ReportScreen(
    navigateForBottomNav: (destination: AppDestination, currentRoute: AppDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    ReportContent(
        onSelectedDestination = navigateForBottomNav,
        body = { ReportBody(modifier = modifier.padding(it)) },
        shouldBottomBarOpen = true
    )
}

@Preview(showBackground = true)
@Composable
private fun ReportContentPreview() {
    DietaryTheme {
        ReportContent(
            onSelectedDestination = { _, _ -> },
            body = { ReportBody(modifier = Modifier.padding(it)) },
            shouldBottomBarOpen = true
        )
    }
}

@Composable
private fun ReportContent(
    onSelectedDestination: (destination: AppDestination, currentRoute: AppDestination) -> Unit,
    body: @Composable (innerPadding: PaddingValues) -> Unit,
    shouldBottomBarOpen: Boolean,
    modifier: Modifier = Modifier
) {
    Scaffold(
        bottomBar = {
            AnimatedVisibility(visible = shouldBottomBarOpen) {
                BottomBar(
                    currentRoute = AppDestination.ReportRoute,
                    onSelectedDestination = onSelectedDestination
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        body(innerPadding)
    }
}

@Composable
private fun ReportBody(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Text(
            text = stringResource(id = R.string.report_title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(alignment = Alignment.Center)
        )
    }
}