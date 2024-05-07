package org.cisnux.mydietary.presentation.report

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.rememberLegendItem
import com.patrykandpatrick.vico.compose.common.rememberVerticalLegend
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.compose.common.shader.verticalGradient
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shape.Shape
import org.cisnux.mydietary.R
import org.cisnux.mydietary.domain.models.FoodDiaryReport
import org.cisnux.mydietary.domain.models.UserNutrition
import org.cisnux.mydietary.presentation.ui.components.BottomBar
import org.cisnux.mydietary.presentation.ui.components.UserNutritionCard
import org.cisnux.mydietary.presentation.ui.components.UserNutritionCardShimmer
import org.cisnux.mydietary.presentation.ui.theme.DietaryTheme
import org.cisnux.mydietary.utils.AppDestination
import org.cisnux.mydietary.utils.Failure
import org.cisnux.mydietary.utils.UiState
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
    val userNutritionState: UiState<*> = UiState.Loading
//    val userNutritionState by viewModel.userDailyNutritionState.collectAsState()
    val context = LocalContext.current

    when {
        reportState is UiState.Error -> {
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

        userNutritionState is UiState.Error -> {
            (userNutritionState as UiState.Error).error?.let { exception ->
                LaunchedEffect(snackbarHostState) {
                    exception.message?.let {
                        snackbarHostState.showSnackbar(
                            message = it,
                            withDismissAction = true,
                            duration = SnackbarDuration.Long
                        )
                    }
                }
                if (exception is Failure.UnauthorizedFailure) {
                    viewModel.signOut()
                    navigateToSignIn(AppDestination.ReportRoute.route)
                }
            }
        }
    }

    ReportContent(
        onSelectedDestination = navigateForBottomNav,
        body = {
            val userDailyNutrition = if (userNutritionState is UiState.Success)
                (userNutritionState as UiState.Success<UserNutrition>).data!!
            else null
            val reports = if (reportState is UiState.Success)
                (reportState as UiState.Success<List<FoodDiaryReport>>).data!!
            else listOf()

            ReportBody(
                reportFilter = tabState,
                onReportFilterChange = { index ->
                    tabState = index
                    viewModel.getReports(index = tabState)
                },
                modifier = Modifier.padding(it),
                maxDailyProtein = userDailyNutrition?.maxDailyProtein ?: 0f,
                maxDailyFat = userDailyNutrition?.maxDailyFat ?: 0f,
                maxDailyCarbohydrate = userDailyNutrition?.maxDailyCarbohydrate ?: 0f,
                maxDailyCalories = userDailyNutrition?.maxDailyCalories ?: 0f,
                totalCaloriesToday = userDailyNutrition?.totalCaloriesToday ?: 0f,
                totalFatToday = userDailyNutrition?.totalFatToday ?: 0f,
                totalProteinToday = userDailyNutrition?.totalProteinToday ?: 0f,
                totalCarbohydrateToday = userDailyNutrition?.totalCarbohydrateToday ?: 0f,
                reports = reports,
                isReportLoading = reportState is UiState.Loading || reportState is UiState.Error,
                isUserNutritionLoading = true
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
            ReportContent(
                onSelectedDestination = { _, _ -> },
                body = {
                    ReportBody(
                        totalCaloriesToday = 200f,
                        totalFatToday = 211f,
                        totalProteinToday = 242.23f,
                        totalCarbohydrateToday = 241.11f,
                        reports = List(20) { index ->
                            FoodDiaryReport(
                                averageProtein = Random.nextDouble(100.0, 200.0).toFloat(),
                                averageFat = Random.nextDouble(100.0, 250.0).toFloat(),
                                averageCalories = Random.nextDouble(100.0, 320.0).toFloat(),
                                averageCarbohydrate = Random.nextDouble(100.0, 270.0).toFloat(),
                                label = "${index + 1}",
                                date = "17 Feb - 21 Feb"
                            )
                        },
                        reportFilter = tabState,
                        onReportFilterChange = { index -> tabState = index },
                        maxDailyCarbohydrate = 1000f,
                        maxDailyFat = 1000f,
                        maxDailyProtein = 1000f,
                        maxDailyCalories = 1000f,
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
    totalCaloriesToday: Float,
    totalCarbohydrateToday: Float,
    totalProteinToday: Float,
    totalFatToday: Float,
    maxDailyCalories: Float,
    maxDailyCarbohydrate: Float,
    maxDailyProtein: Float,
    maxDailyFat: Float,
    reportFilter: Int,
    onReportFilterChange: (Int) -> Unit,
    reports: List<FoodDiaryReport>,
    modifier: Modifier = Modifier,
    isReportLoading: Boolean = false,
    isUserNutritionLoading: Boolean = false,
) {
    val reportCategories = stringArrayResource(id = R.array.report_category)
    val scrollState = rememberScrollState()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { reportCategories.size })

//    LaunchedEffect(pagerState.currentPage) {
//        onTabChange(pagerState.currentPage)
//    }
//    LaunchedEffect(tabState) {
//        pagerState.animateScrollToPage(tabState)
//    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .verticalScroll(state = scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "✦ Nutrisimu Saat Ini",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (!isUserNutritionLoading) {
            UserNutritionCard(
                totalCaloriesToday = totalCaloriesToday,
                totalCarbohydrateToday = totalCarbohydrateToday,
                totalProteinToday = totalProteinToday,
                totalFatToday = totalFatToday,
                maxDailyCalories = maxDailyCalories,
                maxDailyCarbohydrate = maxDailyCarbohydrate,
                maxDailyProtein = maxDailyProtein,
                maxDailyFat = maxDailyFat
            )
        } else {
            UserNutritionCardShimmer()
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (!isReportLoading) {
            val model = remember { CartesianChartModelProducer.build() }
            val labelListKey = remember {
                ExtraStore.Key<List<String>>()
            }
            LaunchedEffect(Unit) {
                model.tryRunTransaction {
                    lineSeries {
                        series(reports.map { it.averageCalories })
                    }
                    lineSeries {
                        series(reports.map { it.averageCarbohydrate })
                    }
                    lineSeries {
                        series(reports.map { it.averageProtein })
                    }
                    lineSeries {
                        series(reports.map { it.averageFat })
                    }
                    updateExtras {
                        it[labelListKey] = reports.map { report -> report.label }
                    }
                }
            }
            CartesianChartHost(
                marker = rememberDefaultCartesianMarker(
                    label = rememberAxisLabelComponent(),
                    valueFormatter = { _, targets ->
                        reports[targets[0].x.toInt()].date
                    }
                ),
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(
                        listOf(
                            rememberLineSpec(
                                shader = DynamicShader.color(Color.Blue),
                                backgroundShader =
                                DynamicShader.verticalGradient(
                                    arrayOf(
                                        Color.Blue.copy(alpha = 0.4f),
                                        Color.Blue.copy(alpha = 0f)
                                    ),
                                ),
                            ),
                        ),
                    ),
                    rememberLineCartesianLayer(
                        listOf(
                            rememberLineSpec(
                                shader = DynamicShader.color(Color.Cyan),
                                backgroundShader =
                                DynamicShader.verticalGradient(
                                    arrayOf(
                                        Color.Cyan.copy(alpha = 0.4f),
                                        Color.Cyan.copy(alpha = 0f)
                                    ),
                                ),
                            ),
                        ),
                    ),
                    rememberLineCartesianLayer(
                        listOf(
                            rememberLineSpec(
                                shader = DynamicShader.color(Color.Yellow),
                                backgroundShader =
                                DynamicShader.verticalGradient(
                                    arrayOf(
                                        Color.Yellow.copy(alpha = 0.4f),
                                        Color.Yellow.copy(alpha = 0f)
                                    ),
                                ),
                            ),
                        ),
                    ),
                    rememberLineCartesianLayer(
                        listOf(
                            rememberLineSpec(
                                shader = DynamicShader.color(Color.Magenta),
                                backgroundShader =
                                DynamicShader.verticalGradient(
                                    arrayOf(
                                        Color.Magenta.copy(alpha = 0.4f),
                                        Color.Magenta.copy(alpha = 0f)
                                    ),
                                ),
                            ),
                        ),
                    ),
                    persistentMarkers = mapOf(),
                    startAxis = rememberStartAxis(),
                    bottomAxis = rememberBottomAxis(
                        title = "Minggu",
                        titleComponent = rememberAxisLabelComponent(),
                        valueFormatter = { x, chartValues, _ -> chartValues.model.extraStore[labelListKey][x.toInt()] }
                    ),
                    legend = rememberVerticalLegend(
                        items = listOf(
                            rememberLegendItem(
                                icon = rememberShapeComponent(
                                    shape = Shape.rounded(
                                        allPercent = 50
                                    ),
                                    color = Color.Blue
                                ),
                                label = rememberAxisLabelComponent(),
                                labelText = "Kalori"
                            ),
                            rememberLegendItem(
                                icon = rememberShapeComponent(
                                    shape = Shape.rounded(
                                        allPercent = 50
                                    ),
                                    color = Color.Cyan
                                ),
                                label = rememberAxisLabelComponent(),
                                labelText = "Karbohidrat"
                            ),
                            rememberLegendItem(
                                icon = rememberShapeComponent(
                                    shape = Shape.rounded(
                                        allPercent = 50
                                    ),
                                    color = Color.Yellow
                                ),
                                label = rememberAxisLabelComponent(),
                                labelText = "Protein"
                            ),
                            rememberLegendItem(
                                icon = rememberShapeComponent(
                                    shape = Shape.rounded(
                                        allPercent = 50
                                    ),
                                    color = Color.Magenta
                                ),
                                label = rememberAxisLabelComponent(),
                                labelText = "Lemak"
                            ),

                            ),
                        iconSize = 12.dp,
                        iconPadding = 4.dp
                    )
                ),
                modelProducer = model,
                modifier = Modifier.height(300.dp)
            )
        } else
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
    }
}
