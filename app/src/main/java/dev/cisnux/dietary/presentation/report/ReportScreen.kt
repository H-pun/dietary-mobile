package dev.cisnux.dietary.presentation.report

import android.content.res.Configuration
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.axis.DataCategoryOptions
import co.yml.charts.common.utils.DataUtils
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarChartType
import co.yml.charts.ui.barchart.models.BarStyle
import dev.cisnux.dietary.domain.models.FoodDiary
import dev.cisnux.dietary.presentation.ui.theme.DietaryTheme
import dev.cisnux.dietary.utils.withFullDateFormat
import dev.cisnux.dietary.utils.withTimeFormat

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
)
@Composable
private fun ReportContentPreview() {

    DietaryTheme {
        Surface {
            ReportContent()
        }
    }
}

@Composable
fun ReportContent() {
    val foodsBreakfastDiary = List(20) {
        FoodDiary(
            id = it.toString(),
            title = "Mie Ayam",
            date = 1706351552829.withFullDateFormat(),
            time = 1706351552829.withTimeFormat(),
            foodPictureUrl = "https://allofresh.id/blog/wp-content/uploads/2023/08/cara-membuat-mie-ayam-4.jpg",
            totalFoodCalories = 500f / it
        )
    }.sortedBy { it.totalFoodCalories }

    val maxRange = 50
    val barData = DataUtils.getBarChartData(
        foodsBreakfastDiary.size,
        maxRange,
        BarChartType.VERTICAL,
        DataCategoryOptions()
    )
    val yStepSize = 10

    val xAxisData = AxisData.Builder()
        .axisLabelColor(MaterialTheme.colorScheme.onSurface)
        .axisLineColor(MaterialTheme.colorScheme.onSurface)
        .backgroundColor(MaterialTheme.colorScheme.surface)
        .axisStepSize(30.dp)
        .steps(barData.size)
        .bottomPadding(16.dp)
        .axisLabelAngle(20f)
        .startDrawPadding(24.dp)
        .labelData { index -> foodsBreakfastDiary[index].time }
        .build()

    val yAxisData = AxisData.Builder()
        .axisLabelColor(MaterialTheme.colorScheme.onSurface)
        .axisLineColor(MaterialTheme.colorScheme.onSurface)
        .backgroundColor(MaterialTheme.colorScheme.surface)
        .steps(yStepSize)
        .labelAndAxisLinePadding(20.dp)
        .axisOffset(20.dp)
        .labelData { index -> String.format("%.1f", foodsBreakfastDiary[index].totalFoodCalories) }
        .build()

    val barChartData = BarChartData(
        chartData = barData,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        backgroundColor = MaterialTheme.colorScheme.surface,
        barStyle = BarStyle(
            paddingBetweenBars = 20.dp,
            barWidth = 25.dp
        ),
        showYAxis = true,
        showXAxis = true,
        horizontalExtraSpace = 10.dp,
    )
    BarChart(modifier = Modifier.height(350.dp), barChartData = barChartData)
}
