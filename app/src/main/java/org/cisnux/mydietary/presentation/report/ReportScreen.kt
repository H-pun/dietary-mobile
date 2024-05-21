package org.cisnux.mydietary.presentation.report

import android.content.res.Configuration
import android.text.Layout
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.valentinilk.shimmer.shimmer
import org.cisnux.mydietary.R
import org.cisnux.mydietary.domain.models.DietProgress
import org.cisnux.mydietary.domain.models.FoodDiaryReport
import org.cisnux.mydietary.domain.models.MonthlyNutritionReport
import org.cisnux.mydietary.domain.models.WeeklyNutritionReport
import org.cisnux.mydietary.domain.models.UserNutrition
import org.cisnux.mydietary.presentation.ui.components.BottomBar
import org.cisnux.mydietary.presentation.ui.components.UserNutritionCard
import org.cisnux.mydietary.presentation.ui.components.UserNutritionCardShimmer
import org.cisnux.mydietary.presentation.ui.theme.DietaryTheme
import org.cisnux.mydietary.presentation.ui.theme.darkBlue
import org.cisnux.mydietary.presentation.ui.theme.darkMagenta
import org.cisnux.mydietary.presentation.ui.theme.darkProgress
import org.cisnux.mydietary.presentation.ui.theme.darkYellow
import org.cisnux.mydietary.presentation.ui.theme.lightBlue
import org.cisnux.mydietary.presentation.ui.theme.lightMagenta
import org.cisnux.mydietary.presentation.ui.theme.lightProgress
import org.cisnux.mydietary.presentation.ui.theme.lightYellow
import org.cisnux.mydietary.presentation.ui.theme.primaryContainerDark
import org.cisnux.mydietary.presentation.ui.theme.primaryContainerLight
import org.cisnux.mydietary.utils.AppDestination
import org.cisnux.mydietary.utils.Failure
import org.cisnux.mydietary.utils.ReportCategory
import org.cisnux.mydietary.utils.UiState
import org.cisnux.mydietary.utils.reportCategory
import kotlin.random.Random

@Composable
fun ReportScreen(
    navigateForBottomNav: (destination: AppDestination, currentRoute: AppDestination) -> Unit,
    navigateToSignIn: (String) -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReportViewModel = hiltViewModel(),
) {
    var tabState by rememberSaveable { mutableIntStateOf(0) }
    var rangeState by rememberSaveable { mutableIntStateOf(0) }
    val userNutritionState by viewModel.userDailyNutritionState.collectAsState(initial = UiState.Initialize)
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val weeklyNutritionReportState by viewModel.weeklyNutritionReportState.collectAsState(initial = UiState.Initialize)
    val monthlyNutritionReportState by viewModel.monthlyNutritionReportState.collectAsState(initial = UiState.Initialize)
    val dietProgressState by viewModel.dietProgressState.collectAsState(initial = UiState.Initialize)
    val context = LocalContext.current

    BackHandler {
        navigateUp()
    }

    if (weeklyNutritionReportState is UiState.Error)
        (weeklyNutritionReportState as UiState.Error).error?.let { exception ->
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

    if (monthlyNutritionReportState is UiState.Error) {
        (monthlyNutritionReportState as UiState.Error).error?.let { exception ->
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
    if (userNutritionState is UiState.Error)
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

    if (dietProgressState is UiState.Error)
        (dietProgressState as UiState.Error).error?.let { exception ->
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

    ReportContent(
        onSelectedDestination = navigateForBottomNav,
        body = {
            val dailyNutritionReports = if (userNutritionState is UiState.Success)
                (userNutritionState as UiState.Success<UserNutrition>).data!!
            else null

            val weeklyNutritionReports = if (weeklyNutritionReportState is UiState.Success)
                (weeklyNutritionReportState as UiState.Success<WeeklyNutritionReport>).data?.weekFoodDiaryReports
                    ?: listOf()
            else listOf()

            val weeks = if (weeklyNutritionReportState is UiState.Success)
                (weeklyNutritionReportState as UiState.Success<WeeklyNutritionReport>).data?.weeks
                    ?: listOf()
            else listOf()

            val monthlyNutritionReports = if (monthlyNutritionReportState is UiState.Success)
                (monthlyNutritionReportState as UiState.Success<MonthlyNutritionReport>).data?.foodDiaryReports
                    ?: listOf()
            else listOf()

            val dietProgress = if (dietProgressState is UiState.Success)
                (dietProgressState as UiState.Success<List<DietProgress>>).data!!
            else listOf()

            val reports by remember(
                weeklyNutritionReports,
                monthlyNutritionReports,
                rangeState,
                tabState
            ) {
                derivedStateOf {
                    if (tabState.reportCategory == ReportCategory.WEEKLY)
                        weeklyNutritionReports.getOrNull(rangeState) ?: listOf()
                    else
                        monthlyNutritionReports
                }
            }

            val selectedDate by remember(weeks, rangeState) {
                derivedStateOf {
                    weeks.getOrNull(rangeState)?.dateRange ?: ""
                }
            }

            ReportBody(
                selectedFilter = tabState,
                onReportFilterChange = { index ->
                    tabState = index
                    viewModel.getReports(index = tabState)
                    rangeState = 0
                },
                modifier = Modifier.padding(it),
                maxDailyProtein = dailyNutritionReports?.maxDailyProtein ?: 0f,
                maxDailyFat = dailyNutritionReports?.maxDailyFat ?: 0f,
                maxDailyCarbohydrate = dailyNutritionReports?.maxDailyCarbohydrate ?: 0f,
                maxDailyCalories = dailyNutritionReports?.maxDailyCalories ?: 0f,
                totalCaloriesToday = dailyNutritionReports?.totalCaloriesToday ?: 0f,
                totalFatToday = dailyNutritionReports?.totalFatToday ?: 0f,
                totalProteinToday = dailyNutritionReports?.totalProteinToday ?: 0f,
                totalCarbohydrateToday = dailyNutritionReports?.totalCarbohydrateToday ?: 0f,
                nutritionReports = reports,
                isNutritionReportLoading = weeklyNutritionReportState is UiState.Loading || weeklyNutritionReportState is UiState.Error || monthlyNutritionReportState is UiState.Loading || monthlyNutritionReportState is UiState.Error,
                isUserNutritionLoading = userNutritionState is UiState.Loading || userNutritionState is UiState.Error,
                isDietProgressLoading = dietProgressState is UiState.Loading || dietProgressState is UiState.Error,
                dietProgressReport = dietProgress,
                selectedDate = selectedDate,
                datePages = weeks,
                onSelectedRange = { newValue ->
                    rangeState = newValue
                }
            )
        },
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
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
                        totalProteinToday = 842.23f,
                        totalCarbohydrateToday = 1241.11f,
                        nutritionReports = List(20) { index ->
                            FoodDiaryReport(
                                averageCarbohydrate = Random.nextDouble(80.0, 100.0).toFloat(),
                                averageFat = Random.nextDouble(20.0, 100.0).toFloat(),
                                averageProtein = Random.nextDouble(60.0, 100.0).toFloat(),
                                averageCalories = Random.nextDouble(50.0, 100.0).toFloat(),
                                label = index.toString(),
                                description = "Senin, 17 Feb"
                            )
                        },
                        selectedFilter = tabState,
                        onReportFilterChange = { index -> tabState = index },
                        maxDailyCarbohydrate = 1000f,
                        maxDailyFat = 1000f,
                        maxDailyProtein = 1000f,
                        maxDailyCalories = 1000f,
                        modifier = Modifier.padding(it),
                        onSelectedRange = {},
                        dietProgressReport = List(20) {
                            DietProgress(
                                weight = Random.nextDouble(60.0, 120.0).toFloat(),
                                waistCircumference = Random.nextDouble(60.0, 120.0).toFloat(),
                                description = "Senin, 17 Feb"
                            )
                        },
                        selectedDate = "17 Feb - 21 Feb",
                        datePages = listOf(
                            WeeklyNutritionReport.DatePage(dateRange = "17 Feb - 21 Feb"),
                            WeeklyNutritionReport.DatePage(dateRange = "22 Feb - 29 Feb"),
                            WeeklyNutritionReport.DatePage(dateRange = "30 Feb - 7 Mar")
                        )
                    )
                },
                snackbarHostState = SnackbarHostState()
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
    selectedFilter: Int,
    selectedDate: String,
    onReportFilterChange: (Int) -> Unit,
    onSelectedRange: (Int) -> Unit,
    nutritionReports: List<FoodDiaryReport>,
    dietProgressReport: List<DietProgress>,
    modifier: Modifier = Modifier,
    datePages: List<WeeklyNutritionReport.DatePage> = listOf(),
    isNutritionReportLoading: Boolean = false,
    isUserNutritionLoading: Boolean = false,
    isDietProgressLoading: Boolean = false
) {
    val placeholder = if (!isSystemInDarkTheme()) lightProgress
    else darkProgress

    val caloriesColor = if (!isSystemInDarkTheme()) primaryContainerLight
    else primaryContainerDark

    val carbohydrateColor = if (!isSystemInDarkTheme()) lightBlue
    else darkBlue

    val proteinColor = if (!isSystemInDarkTheme()) lightYellow
    else darkYellow

    val fatColor = if (!isSystemInDarkTheme()) lightMagenta
    else darkMagenta

    val weightColor = if (!isSystemInDarkTheme())
        primaryContainerLight
    else primaryContainerDark

    val waistCircumferenceColor = if (!isSystemInDarkTheme()) lightMagenta
    else darkMagenta

    var isExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(state = rememberScrollState())
            .padding(PaddingValues(16.dp)),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "✦ Kemajuan",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (!isDietProgressLoading) {
            if (dietProgressReport.isEmpty()) {
                Image(
                    painter = painterResource(R.drawable.empty_report),
                    contentDescription = null,
                    modifier = Modifier.size(260.dp)
                )
            } else {
                val weightAndWaistCircumference = remember { CartesianChartModelProducer.build() }
                val labelListKey = remember {
                    ExtraStore.Key<List<String>>()
                }

                LaunchedEffect(dietProgressReport) {
                    weightAndWaistCircumference.tryRunTransaction {
                        lineSeries {
                            series(dietProgressReport.map { it.weight })
                        }
                        lineSeries {
                            series(dietProgressReport.map { it.waistCircumference })
                        }
                        updateExtras {
                            it[labelListKey] =
                                dietProgressReport.map { report -> report.description }
                        }
                    }
                }

                CartesianChartHost(
                    marker = rememberDefaultCartesianMarker(
                        label = rememberAxisLabelComponent(),
                        valueFormatter = { _, targets ->
                            dietProgressReport[targets[0].x.toInt()].description
                        }
                    ),
                    chart = rememberCartesianChart(
                        rememberLineCartesianLayer(
                            listOf(
                                rememberLineSpec(
                                    shader = DynamicShader.color(weightColor),
                                    backgroundShader =
                                    DynamicShader.verticalGradient(
                                        arrayOf(
                                            weightColor.copy(alpha = 0.4f),
                                            weightColor.copy(alpha = 0f)
                                        ),
                                    ),
                                ),
                            ),
                        ),
                        rememberLineCartesianLayer(
                            listOf(
                                rememberLineSpec(
                                    shader = DynamicShader.color(waistCircumferenceColor),
                                    backgroundShader =
                                    DynamicShader.verticalGradient(
                                        arrayOf(
                                            waistCircumferenceColor.copy(alpha = 0.4f),
                                            waistCircumferenceColor.copy(alpha = 0f)
                                        ),
                                    ),
                                ),
                            ),
                        ),
                        startAxis = rememberStartAxis(
                            title = "Berat dan Lingkar Pinggang",
                            titleComponent = rememberAxisLabelComponent(
                                textAlignment = Layout.Alignment.ALIGN_CENTER,
                                lineCount = 2
                            ),
                        ),
                        bottomAxis = rememberBottomAxis(
                            title = "Hari",
                            titleComponent = rememberAxisLabelComponent(),
                            valueFormatter = { x, _, _ -> (x.toInt() + 1).toString() }
                        ),
                        legend = rememberVerticalLegend(
                            items = listOf(
                                rememberLegendItem(
                                    icon = rememberShapeComponent(
                                        shape = Shape.rounded(
                                            allPercent = 50
                                        ),
                                        color = weightColor
                                    ),
                                    label = rememberAxisLabelComponent(),
                                    labelText = "Berat Badan (kg)"
                                ),
                                rememberLegendItem(
                                    icon = rememberShapeComponent(
                                        shape = Shape.rounded(
                                            allPercent = 50
                                        ),
                                        color = waistCircumferenceColor
                                    ),
                                    label = rememberAxisLabelComponent(),
                                    labelText = "Lingkar Pinggang (cm)"
                                ),

                                ),
                            iconSize = 12.dp,
                            iconPadding = 4.dp
                        )
                    ),
                    modelProducer = weightAndWaistCircumference,
                    modifier = Modifier.height(280.dp)
                )
            }
        } else
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
                            .height(Random.nextInt(150, 210).dp)
                            .shimmer()
                    ) {}
                }
            }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "✦ Nutrisi",
            style = MaterialTheme.typography.titleLarge,
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
                maxDailyFat = maxDailyFat,
                modifier = Modifier.padding(PaddingValues())
            )
        } else {
            UserNutritionCardShimmer(modifier = Modifier.padding(PaddingValues()))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            repeat(
                2
            ) {
                FilterChip(
                    selected = it == selectedFilter,
                    onClick = { onReportFilterChange(it) },
                    leadingIcon = {
                        if (it == selectedFilter) {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = null,
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = if (it == 0) "Mingguan"
                            else "Bulanan"
                        )
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
        AnimatedVisibility(visible = selectedFilter == 0 && selectedDate.isNotBlank()) {
            Column(
                modifier = Modifier
                    .align(Alignment.Start)
                    .fillMaxWidth()
            ) {
                FilterChip(
                    selected = true,
                    onClick = {
                        if (selectedDate.isNotBlank())
                            isExpanded = true
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier
                                .size(FilterChipDefaults.IconSize)
                                .rotate(if (isExpanded) 180f else 0f)
                        )
                    },
                    label = {
                        Text(
                            text = selectedDate
                        )
                    },
                    modifier = Modifier.align(Alignment.Start)
                )
                DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
                    datePages.forEachIndexed { index, datePage ->
                        DropdownMenuItem(text = { Text(text = datePage.dateRange) }, onClick = {
                            isExpanded = false
                            onSelectedRange(index)
                        })
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        AnimatedVisibility(!isNutritionReportLoading) {
            if (nutritionReports.isEmpty()) {
                Image(
                    painter = painterResource(R.drawable.empty_report),
                    contentDescription = null,
                    modifier = Modifier.size(260.dp)
                )
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    val firstModel = remember { CartesianChartModelProducer.build() }
                    val secondModel = remember { CartesianChartModelProducer.build() }
                    val labelListKey = remember {
                        ExtraStore.Key<List<String>>()
                    }
                    LaunchedEffect(nutritionReports, selectedDate, selectedFilter) {
                        firstModel.tryRunTransaction {
                            lineSeries {
                                series(nutritionReports.map { it.averageCalories })
                            }
                            lineSeries {
                                series(nutritionReports.map { it.averageCarbohydrate })
                            }
                            updateExtras {
                                it[labelListKey] = nutritionReports.map { report -> report.label }
                            }
                        }
                        secondModel.tryRunTransaction {
                            lineSeries {
                                series(nutritionReports.map { it.averageProtein })
                            }
                            lineSeries {
                                series(nutritionReports.map { it.averageFat })
                            }
                            updateExtras {
                                it[labelListKey] = nutritionReports.map { report -> report.label }
                            }
                        }
                    }
                    CartesianChartHost(
                        marker = rememberDefaultCartesianMarker(
                            label = rememberAxisLabelComponent(),
                            valueFormatter = { _, targets ->
                                nutritionReports[targets[0].x.toInt()].description
                            }
                        ),
                        chart = rememberCartesianChart(
                            rememberLineCartesianLayer(
                                listOf(
                                    rememberLineSpec(
                                        shader = DynamicShader.color(caloriesColor),
                                        backgroundShader =
                                        DynamicShader.verticalGradient(
                                            arrayOf(
                                                caloriesColor.copy(alpha = 0.4f),
                                                caloriesColor.copy(alpha = 0f)
                                            ),
                                        ),
                                    ),
                                ),
                            ),
                            rememberLineCartesianLayer(
                                listOf(
                                    rememberLineSpec(
                                        shader = DynamicShader.color(carbohydrateColor),
                                        backgroundShader =
                                        DynamicShader.verticalGradient(
                                            arrayOf(
                                                carbohydrateColor.copy(alpha = 0.4f),
                                                carbohydrateColor.copy(alpha = 0f)
                                            ),
                                        ),
                                    ),
                                ),
                            ),
                            startAxis = rememberStartAxis(
                                title = "Kalori dan Karbohidrat",
                                titleComponent = rememberAxisLabelComponent(
                                    textAlignment = Layout.Alignment.ALIGN_CENTER,
                                ),
                            ),
                            bottomAxis = rememberBottomAxis(
                                title = if (selectedFilter == 0) "Hari" else "Minggu",
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
                                            color = caloriesColor
                                        ),
                                        label = rememberAxisLabelComponent(),
                                        labelText = "Kalori (kcal)"
                                    ),
                                    rememberLegendItem(
                                        icon = rememberShapeComponent(
                                            shape = Shape.rounded(
                                                allPercent = 50
                                            ),
                                            color = carbohydrateColor
                                        ),
                                        label = rememberAxisLabelComponent(),
                                        labelText = "Karbohidrat (g)"
                                    ),

                                    ),
                                iconSize = 12.dp,
                                iconPadding = 4.dp
                            )
                        ),
                        modelProducer = firstModel,
                        modifier = Modifier.height(280.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CartesianChartHost(
                        marker = rememberDefaultCartesianMarker(
                            label = rememberAxisLabelComponent(),
                            valueFormatter = { _, targets ->
                                nutritionReports[targets[0].x.toInt()].description
                            }
                        ),
                        chart = rememberCartesianChart(
                            rememberLineCartesianLayer(
                                listOf(
                                    rememberLineSpec(
                                        shader = DynamicShader.color(proteinColor),
                                        backgroundShader =
                                        DynamicShader.verticalGradient(
                                            arrayOf(
                                                proteinColor.copy(alpha = 0.4f),
                                                proteinColor.copy(alpha = 0f)
                                            ),
                                        ),
                                    ),
                                ),
                            ),
                            rememberLineCartesianLayer(
                                listOf(
                                    rememberLineSpec(
                                        shader = DynamicShader.color(fatColor),
                                        backgroundShader =
                                        DynamicShader.verticalGradient(
                                            arrayOf(
                                                fatColor.copy(alpha = 0.4f),
                                                fatColor.copy(alpha = 0f)
                                            ),
                                        ),
                                    ),
                                ),
                            ),
                            startAxis = rememberStartAxis(
                                title = "Protein dan Lemak",
                                titleComponent = rememberAxisLabelComponent(
                                    textAlignment = Layout.Alignment.ALIGN_CENTER,
                                ),
                            ),
                            bottomAxis = rememberBottomAxis(
                                title = if (selectedFilter == 0) "Hari" else "Minggu",
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
                                            color = proteinColor
                                        ),
                                        label = rememberAxisLabelComponent(),
                                        labelText = "Protein (g)"
                                    ),
                                    rememberLegendItem(
                                        icon = rememberShapeComponent(
                                            shape = Shape.rounded(
                                                allPercent = 50
                                            ),
                                            color = fatColor
                                        ),
                                        label = rememberAxisLabelComponent(),
                                        labelText = "Lemak (g)"
                                    ),

                                    ),
                                iconSize = 12.dp,
                                iconPadding = 4.dp
                            )
                        ),
                        modelProducer = secondModel,
                        modifier = Modifier.height(280.dp)
                    )
                }
            }
        }
        AnimatedVisibility(isNutritionReportLoading) {
            repeat(2) {
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
                                .height(Random.nextInt(150, 210).dp)
                                .shimmer()
                        ) {}
                    }
                }
            }
        }
    }
}
