package org.cisnux.mydietary.presentation.introduction

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.cisnux.mydietary.R
import org.cisnux.mydietary.presentation.ui.theme.DietaryTheme

@Composable
fun IntroductionScreen(
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit,
    onSignIn: () -> Unit
) {
    IntroductionContent {
        IntroductionBody(
            modifier = modifier.padding(it),
            onNavigateUp = navigateUp,
            onDone = onSignIn
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun IntroductionContentPreview() {
    DietaryTheme {
        IntroductionContent {
            IntroductionBody(modifier = Modifier.padding(it))
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL, locale = "in",
)
@Composable
private fun IntroductionContentDarkPreview() {
    DietaryTheme {
        IntroductionContent {
            IntroductionBody(modifier = Modifier.padding(it))
        }
    }
}

@Composable
private fun IntroductionContent(
    modifier: Modifier = Modifier,
    body: @Composable (PaddingValues) -> Unit = {}
) {
    Scaffold {
        body(it)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun IntroductionBody(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit = {},
    onDone: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val introductionIllustrations = listOf(
        painterResource(id = R.drawable.food_illustration),
        painterResource(id = R.drawable.take_picture_illustration),
        painterResource(id = R.drawable.report_illustration)
    )
    val introductionTitles = listOf(
        stringResource(R.string.food_introduction_title),
        stringResource(R.string.take_picture_introduction_title),
        stringResource(R.string.report_introduction_title),
    )
    val introductionDescriptions = listOf(
        stringResource(R.string.food_introduction_description),
        stringResource(R.string.take_picture_introduction_description),
        stringResource(R.string.report_introduction_description),
    )
    var currentPage by rememberSaveable {
        mutableIntStateOf(0)
    }
    val pagerState = rememberPagerState {
        introductionIllustrations.size
    }

    LaunchedEffect(pagerState) {
        currentPage = pagerState.currentPage
    }

    LaunchedEffect(currentPage) {
        pagerState.animateScrollToPage(page = currentPage)
    }

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(PaddingValues(16.dp))
    ) {
        FilledIconButton(
            onClick = onNavigateUp,
            modifier = Modifier.align(Alignment.Start),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((configuration.screenHeightDp * 0.8).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = introductionIllustrations[it],
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((configuration.screenHeightDp * 0.4f).dp)
                )
                AnimatedVisibility(visible = it == 1) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Text(
                    text = introductionTitles[it],
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(300.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = introductionDescriptions[it],
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(300.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        Column {
            Row(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color =
                        if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(12.dp)
                    )
                }
            }
            Row(
                Modifier
                    .padding(PaddingValues(horizontal = 8.dp))
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onDone) {
                    Text(text = stringResource(R.string.skip))
                }
                Button(onClick = {
                    if (currentPage < (pagerState.pageCount - 1))
                        currentPage++
                    else onDone()
                }) {
                    Text(text = stringResource(R.string.next))
                }
            }
        }
    }
}