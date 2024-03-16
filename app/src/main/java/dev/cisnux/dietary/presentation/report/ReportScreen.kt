package dev.cisnux.dietary.presentation.report

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarStyle
import com.valentinilk.shimmer.shimmer
import dev.cisnux.dietary.R
import dev.cisnux.dietary.domain.models.FoodDiaryReport
import dev.cisnux.dietary.presentation.ui.components.BottomBar
import dev.cisnux.dietary.presentation.ui.theme.DietaryTheme
import dev.cisnux.dietary.presentation.ui.theme.placeholder
import dev.cisnux.dietary.utils.AppDestination
import dev.cisnux.dietary.utils.Failure
import dev.cisnux.dietary.utils.UiState
import dev.cisnux.dietary.utils.dateAndMonth
import java.time.Instant
import kotlin.random.Random

@Composable
fun ReportScreen(
    navigateForBottomNav: (destination: AppDestination, currentRoute: AppDestination) -> Unit,
    navigateToSignIn: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReportViewModel = hiltViewModel(),
) {
    var tabState by rememberSaveable { mutableIntStateOf(0) }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val reportState by viewModel.reportState.collectAsState()
    val report by viewModel.report.collectAsState()
    val oneTimeReport by rememberUpdatedState(viewModel::getReports)
    val context = LocalContext.current
    val maxRange by viewModel.maxRange.collectAsState()

    LaunchedEffect(Unit) {
        oneTimeReport(tabState)
    }

    if (reportState is UiState.Error) {
        (reportState as UiState.Error).error?.let { exception ->
            LaunchedEffect(snackbarHostState) {
                exception.message?.let {
                    val snackbarResult = snackbarHostState.showSnackbar(
                        message = it,
                        actionLabel = context.getString(R.string.retry),
                        withDismissAction = true,
                        duration = SnackbarDuration.Long
                    )
                    if (snackbarResult == SnackbarResult.ActionPerformed) viewModel.getReports(
                        index = tabState
                    )
                }
            }
            if (exception is Failure.UnauthorizedFailure) {
                viewModel.signOut()
                navigateToSignIn(AppDestination.ReportRoute.route)
            }
        }
    }

    ReportContent(
        onSelectedDestination = navigateForBottomNav,
        body = {
            ReportBody(
                totalUserCaloriesToday = report?.totalUserCaloriesToday ?: 0f,
                maxDailyBmiCalorie = report?.maxDailyBmiCalorie ?: 0f,
                foods = report?.foods ?: listOf(),
                tabState = tabState,
                onTabChange = { index ->
                    tabState = index
                    viewModel.getReports(index = tabState)
                },
                modifier = Modifier.padding(it),
                isLoading = reportState is UiState.Loading || reportState is UiState.Error,
                maxRange = maxRange
            )
        },
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
)
@Composable
private fun ReportContentPreview() {
    var tabState by rememberSaveable { mutableIntStateOf(0) }

    DietaryTheme {
        Surface {
            ReportContent(onSelectedDestination = { _, _ -> }, body = {
                ReportBody(
                    totalUserCaloriesToday = 500.7892f,
                    maxDailyBmiCalorie = 800.6798f,
                    foods = List(12) { index ->
                        FoodDiaryReport(
                            id = index.toString(),
                            title = "Warteg $it",
                            totalFoodCalories = Random.nextDouble(50.0, 500.0).toFloat(),
                            label = Instant.now().dateAndMonth()
                        )
                    },
                    tabState = tabState,
                    onTabChange = { index -> tabState = index },
                    modifier = Modifier.padding(it),
                )
            }, snackbarHostState = SnackbarHostState()
            )
        }
    }
}

@Composable
fun ReportContent(
    onSelectedDestination: (destination: AppDestination, currentRoute: AppDestination) -> Unit,
    body: @Composable (PaddingValues) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(bottomBar = {
        BottomBar(
            currentRoute = AppDestination.ReportRoute,
            onSelectedDestination = onSelectedDestination
        )
    }, snackbarHost = {
        SnackbarHost(hostState = snackbarHostState)
    }, modifier = modifier
    ) { innerPadding ->
        body(innerPadding)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ReportBody(
    totalUserCaloriesToday: Float,
    maxDailyBmiCalorie: Float,
    tabState: Int,
    onTabChange: (Int) -> Unit,
    foods: List<FoodDiaryReport>,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    maxRange: Int = 0,
) {
    val dailyCalorieProgress = totalUserCaloriesToday / maxDailyBmiCalorie

    val reportCategories = stringArrayResource(id = R.array.report_category)
    val scrollState = rememberScrollState()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { reportCategories.size })

    LaunchedEffect(pagerState.currentPage) {
        onTabChange(pagerState.currentPage)
    }
    LaunchedEffect(tabState) {
        pagerState.animateScrollToPage(tabState)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .verticalScroll(state = scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Lihat Laporan Kalori\n& Diet Progress:\nTrack Kesuksesan Anda! \uD83D\uDCCA",
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (!isLoading) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = String.format("%.2f kcal", totalUserCaloriesToday),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.W600
                )
                Text(
                    text = String.format("%.2f kcal", maxDailyBmiCalorie),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.W600
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { dailyCalorieProgress }, modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Kalorimu Hari Ini",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.W600,
                )
                Text(
                    text = "Maks Kalori",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.W600,
                )
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = placeholder,
                    modifier = Modifier
                        .height(16.dp)
                        .width(100.dp)
                        .shimmer()
                ) {}
                Surface(
                    color = placeholder,
                    modifier = Modifier
                        .height(16.dp)
                        .width(100.dp)
                        .shimmer()
                ) {}
            }
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                color = placeholder,
                modifier = Modifier
                    .height(4.dp)
                    .fillMaxWidth()
                    .shimmer()
            ) {}
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = placeholder,
                    modifier = Modifier
                        .height(16.dp)
                        .width(100.dp)
                        .shimmer()
                ) {}
                Surface(
                    color = placeholder,
                    modifier = Modifier
                        .height(16.dp)
                        .width(100.dp)
                        .shimmer()
                ) {}
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        TabRow(selectedTabIndex = tabState) {
            reportCategories.mapIndexed { index, category ->
                Tab(
                    selected = tabState == index,
                    onClick = {
                        onTabChange(index)
                    },
                    text = {
                        Text(
                            text = category,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = if (tabState == index) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                if (!isLoading && foods.isNotEmpty()) {
                    val barData = foods.mapIndexed { index, foodDiaryReport ->
                        BarData(
                            point = Point(index.toFloat(), foodDiaryReport.totalFoodCalories),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    val yStepSize = 10

                    val xAxisData =
                        AxisData.Builder().axisLabelColor(MaterialTheme.colorScheme.onSurface)
                            .axisLineColor(MaterialTheme.colorScheme.onSurface)
                            .backgroundColor(MaterialTheme.colorScheme.surface).steps(barData.size)
                            .axisStepSize(30.dp)
                            .axisLabelAngle(20f)
                            .bottomPadding(40.dp).startDrawPadding(28.dp).endPadding(28.dp)
                            .labelData { index -> foods[index].label }.build()

                    val yAxisData =
                        AxisData.Builder().axisLabelColor(MaterialTheme.colorScheme.onSurface)
                            .axisLineColor(MaterialTheme.colorScheme.onSurface)
                            .backgroundColor(MaterialTheme.colorScheme.surface).steps(yStepSize)
                            .labelAndAxisLinePadding(20.dp).axisOffset(20.dp)
                            .labelData { index -> (index * (maxRange / yStepSize)).toString() }
                            .build()

                    val barChartData = BarChartData(
                        chartData = barData,
                        xAxisData = xAxisData,
                        yAxisData = yAxisData,
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        barStyle = BarStyle(
                            paddingBetweenBars = 24.dp, barWidth = 26.dp
                        ),
                        showYAxis = true,
                        showXAxis = true,
                        horizontalExtraSpace = 28.dp,
                    )
                    Text(
                        text = "Kalorie (kcal)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    BarChart(
                        modifier = Modifier
                            .height(350.dp)
                            .fillMaxWidth(), barChartData = barChartData
                    )
                }
                if (isLoading) {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(top = 60.dp)
                    ) {
                        repeat(12) {
                            Surface(
                                color = placeholder,
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .width(26.dp)
                                    .height(Random.nextInt(200, 250).dp)
                                    .shimmer()
                            ) {}
                        }
                    }
                }
            }
        }
    }
}