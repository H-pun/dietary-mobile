package dev.cisnux.dietary.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.cisnux.dietary.R
import dev.cisnux.dietary.presentation.ui.theme.DietaryTheme

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
            sugar = String.format("%.2f", 9.8f),
            feedback = listOf(
                "Bagian gosong pada makanan yang dibakar mengandung karsinogenik (senyawa yang berpotensi menyebabkan kanker), jangan terlalu sering mengkonsumsi makanan yang diolah dengan cara dibakar",
                "Bagian gosong pada makanan yang dibakar mengandung karsinogenik (senyawa yang berpotensi menyebabkan kanker), jangan terlalu sering mengkonsumsi makanan yang diolah dengan cara dibakar",
                "Bagian gosong pada makanan yang dibakar mengandung karsinogenik (senyawa yang berpotensi menyebabkan kanker), jangan terlalu sering mengkonsumsi makanan yang diolah dengan cara dibakar",
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodListItem(
    foodName: String,
    calorie: String,
    fat: String,
    protein: String,
    carbohydrates: String,
    sugar: String?,
    modifier: Modifier = Modifier,
    feedback: List<String> = listOf()
) {
    var isContentExpanded by rememberSaveable {
        mutableStateOf(false)
    }
    var isFeedbackExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    Surface {
        Column(modifier = modifier) {
            Text(
                text = "✧ $foodName (100 g)",
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .clickable { isContentExpanded = !isContentExpanded },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "Kandungan",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(onClick = { isContentExpanded = !isContentExpanded }) {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isContentExpanded)
                        }
                    }
                    HorizontalDivider(thickness = 1.5.dp)
                    AnimatedVisibility(visible = isContentExpanded) {
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
                                        text = stringResource(R.string.calorie),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = stringResource(R.string.kcal, calorie),
                                        fontWeight = FontWeight.Light,
                                        style = MaterialTheme.typography.labelMedium,
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
                                        text = stringResource(R.string.fat),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = stringResource(R.string.g, fat),
                                        fontWeight = FontWeight.Light,
                                        style = MaterialTheme.typography.labelMedium,
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
                                        text = stringResource(R.string.protein),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = stringResource(R.string.g, protein),
                                        fontWeight = FontWeight.Light,
                                        style = MaterialTheme.typography.labelMedium,
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
                                        text = stringResource(R.string.carbohydrate),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = stringResource(R.string.g, carbohydrates),
                                        fontWeight = FontWeight.Light,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(thickness = 1.5.dp)
                                sugar?.let {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = stringResource(R.string.sugar),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = stringResource(R.string.g, sugar),
                                            fontWeight = FontWeight.Light,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    HorizontalDivider(thickness = 1.5.dp)
                                }
                            }
                        }
                    }
                    if (feedback.isNotEmpty()) {
                        Row(
                            modifier = modifier
                                .fillMaxWidth()
                                .clickable { isFeedbackExpanded = !isFeedbackExpanded },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = stringResource(id = R.string.feedback),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            IconButton(onClick = { isFeedbackExpanded = !isFeedbackExpanded }) {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isFeedbackExpanded)
                            }
                        }
                        HorizontalDivider(thickness = 1.5.dp)
                    }
                    AnimatedVisibility(visible = isFeedbackExpanded) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Spacer(modifier = Modifier.width(30.dp))
                            Column {
                                feedback.forEach { feedback ->
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = feedback,
                                        textAlign = TextAlign.Start,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    HorizontalDivider(thickness = 1.5.dp)
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}