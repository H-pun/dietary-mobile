package dev.cisnux.dietary.presentation.scannerresult

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.cisnux.dietary.R
import dev.cisnux.dietary.domain.models.Food
import dev.cisnux.dietary.domain.models.FoodScannerReport
import dev.cisnux.dietary.presentation.ui.theme.DietaryTheme
import dev.cisnux.dietary.presentation.ui.theme.onSurfaceDark
import dev.cisnux.dietary.presentation.ui.theme.onSurfaceLight
import dev.cisnux.dietary.presentation.ui.theme.secondaryContainerDark
import dev.cisnux.dietary.presentation.ui.theme.secondaryContainerLight
import dev.cisnux.dietary.presentation.ui.theme.surfaceDark
import dev.cisnux.dietary.presentation.ui.theme.surfaceLight

@Composable
fun ScannerResultScreen(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScannerResultViewModel = hiltViewModel(),
) {
    val foodPicture = viewModel.foodPicture
    val foodScannerReport = FoodScannerReport(
        reportId = "1",
        totalFoodCalories = 300.24f,
        userDailyBmiCalorie = 525.2f,
        foods = List(5) {
            Food(
                id = it.toString(),
                foodName = "Ayam Goreng $it",
                calorie = 300.3f,
                fat = 20.5f,
                carbohydrates = 20f,
                protein = 9.8f,
            )
        }
    )

    ScannerResultContent(
        onAdditionalInformation = {},
        navigateUp = onNavigateUp,
        body = {
            ScannerResultBody(
                totalFoodCalories = foodScannerReport.totalFoodCalories,
                userDailyBmiCalorie = foodScannerReport.userDailyBmiCalorie,
                foods = foodScannerReport.foods,
                modifier = Modifier.padding(it)
            )
        },
        modifier = modifier
    )
}

@Composable
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
private fun ScannerResultContentPreview() {
    val foodScannerReport = FoodScannerReport(
        reportId = "1",
        totalFoodCalories = 300.24f,
        userDailyBmiCalorie = 525.2f,
        foods = List(5) {
            Food(
                id = it.toString(),
                foodName = "Ayam $it",
                calorie = 300.3f,
                fat = 20.5f,
                carbohydrates = 20f,
                protein = 9.8f,
            )
        }
    )

    DietaryTheme {
        ScannerResultContent(
            onAdditionalInformation = {},
            navigateUp = {},
            body = {
                ScannerResultBody(
                    totalFoodCalories = foodScannerReport.totalFoodCalories,
                    userDailyBmiCalorie = foodScannerReport.userDailyBmiCalorie,
                    foods = foodScannerReport.foods,
                    modifier = Modifier.padding(it)
                )
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScannerResultContent(
    onAdditionalInformation: () -> Unit,
    navigateUp: () -> Unit,
    body: @Composable (PaddingValues) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Food & Side Dishes", color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Hello",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip {
                                Text(text = "add additional information")
                            }
                        },
                        state = rememberTooltipState()
                    ) {
                        IconButton(onClick = onAdditionalInformation) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_add_circle_outline_24dp),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        body(innerPadding)
    }
}

@Composable
private fun ScannerResultBody(
    totalFoodCalories: Float,
    userDailyBmiCalorie: Float,
    foods: List<Food>,
    modifier: Modifier = Modifier,
    isFoodsLoading: Boolean = false,
) {
    val context = LocalContext.current
    val percentage = totalFoodCalories / userDailyBmiCalorie

    Row(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(contentAlignment = Alignment.Center) {
            Spacer(
                modifier = Modifier
                    .size(120.dp)
                    .drawBehind {
                        drawCircle(
                            color = when (context.resources.configuration.uiMode) {
                                Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL -> onSurfaceLight
                                else -> onSurfaceDark
                            }
                        )
                    }
            )
            Spacer(
                modifier = Modifier
                    .size(120.dp)
                    .drawBehind {
                        drawArc(
                            color = when (context.resources.configuration.uiMode) {
                                Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL -> secondaryContainerLight
                                else -> secondaryContainerDark
                            },
                            startAngle = 270f,
                            sweepAngle = 360f * percentage,
                            useCenter = true
                        )
                    }
            )
            Spacer(
                modifier = Modifier
                    .size(110.dp)
                    .drawBehind {
                        drawCircle(
                            color = when (context.resources.configuration.uiMode) {
                                Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL -> surfaceLight
                                else -> surfaceDark
                            }
                        )
                    }
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = String.format("%.2f", totalFoodCalories),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = String.format("/%.2f kcal", userDailyBmiCalorie),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(foods, key = { it.id }, contentType = { it }) {
                FoodListItem(
                    foodName = it.foodName,
                    calorie = String.format("%.2f", it.calorie),
                    fat = String.format("%.2f", it.fat),
                    carbohydrates = String.format("%.2f", it.carbohydrates),
                    protein = String.format("%.2f", it.protein),
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun FoodListItemPreview() {
    DietaryTheme {
        FoodListItem(
            foodName = "Ayam Goreng",
            calorie = String.format("%.2f", 300.3f),
            fat = String.format("%.2f", 20.5f),
            carbohydrates = String.format("%.2f", 20f),
            protein = String.format("%.2f", 9.8f),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FoodListItem(
    foodName: String,
    calorie: String,
    fat: String,
    protein: String,
    carbohydrates: String,
    modifier: Modifier = Modifier
) {
    var isExpanded by rememberSaveable {
        mutableStateOf(true)
    }

    Surface {
        Column {
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = foodName,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = { isExpanded = !isExpanded }) {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                }
            }
            HorizontalDivider(thickness = 1.5.dp)
            AnimatedVisibility(visible = isExpanded) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.width(30.dp))
                    Column {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Calories",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$calorie kcal",
                                fontWeight = FontWeight.Light,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(thickness = 1.5.dp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Fat",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$fat g",
                                fontWeight = FontWeight.Light,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(thickness = 1.5.dp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Protein",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$protein g",
                                fontWeight = FontWeight.Light,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(thickness = 1.5.dp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Carbohydrates",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$carbohydrates g",
                                fontWeight = FontWeight.Light,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(thickness = 1.5.dp)
                    }
                }
            }
        }
    }
}
