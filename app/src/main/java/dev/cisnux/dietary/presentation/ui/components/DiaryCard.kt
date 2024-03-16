package dev.cisnux.dietary.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.valentinilk.shimmer.shimmer
import dev.cisnux.dietary.domain.models.FoodDiary
import dev.cisnux.dietary.presentation.ui.theme.DietaryTheme
import dev.cisnux.dietary.presentation.ui.theme.placeholder
import dev.cisnux.dietary.utils.dayDateMonthYear
import dev.cisnux.dietary.utils.hoursAndMinutes
import java.time.Instant

@Preview(showBackground = true)
@Composable
private fun DiaryCardPreview() {
    val food = FoodDiary(
        id = "1",
        title = "Nasi Padang",
        date = Instant.now().dayDateMonthYear(),
        time = Instant.now().hoursAndMinutes(),
        foodPictureUrl = "https://awsimages.detik.net.id/community/media/visual/2020/07/06/nasi-padang.jpeg?w=600&q=90",
        totalFoodCalories = 500f,
    )

    DietaryTheme {
        DiaryCard(
            foodName = food.title,
            date = food.date,
            time = food.time,
            foodImageUrl = food.foodPictureUrl,
            calorie = food.totalFoodCalories,
            onClick = {}
        )
    }
}

@Composable
fun DiaryCard(
    foodName: String,
    date: String,
    time: String,
    foodImageUrl: String?,
    calorie: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            foodImageUrl?.let {
                SubcomposeAsyncImage(
                    model = foodImageUrl,
                    error = {
                        Surface(
                            color = placeholder,
                            modifier = Modifier
                                .size(height = 90.dp, width = 90.dp)
                                .clip(shape = MaterialTheme.shapes.small)
                                .shimmer(),
                            content = {}
                        )
                    },
                    loading = {
                        Surface(
                            color = placeholder,
                            modifier = Modifier
                                .size(height = 90.dp, width = 90.dp)
                                .clip(shape = MaterialTheme.shapes.small)
                                .shimmer(),
                            content = {}
                        )
                    },
                    contentScale = ContentScale.FillBounds,
                    contentDescription = foodName,
                    modifier = modifier
                        .size(height = 90.dp, width = 90.dp)
                        .clip(shape = MaterialTheme.shapes.small),
                )
            } ?: Surface(
                color = placeholder,
                modifier = Modifier
                    .size(height = 90.dp, width = 90.dp)
                    .clip(shape = MaterialTheme.shapes.small)
                    .shimmer(),
                content = {}
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = foodName,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "$calorie kcal",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = date,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = time,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(thickness = 1.5.dp)
    }
}
