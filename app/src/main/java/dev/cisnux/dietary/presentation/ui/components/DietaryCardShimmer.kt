package dev.cisnux.dietary.presentation.ui.components

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer
import dev.cisnux.dietary.presentation.ui.theme.DietaryTheme
import dev.cisnux.dietary.presentation.ui.theme.placeholder

@Preview(showBackground = true)
@Composable
private fun DiaryCardShimmerPreview() {

    DietaryTheme {
        DiaryCardShimmer()
    }
}

@Composable
fun DiaryCardShimmer(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Surface(
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
                    Surface(
                        color = placeholder,
                        modifier = Modifier
                            .size(height = 25.dp, width = 120.dp)
                            .shimmer(),
                        content = {}
                    )
                    Surface(
                        color = placeholder,
                        modifier = Modifier
                            .size(height = 20.dp, width = 50.dp)
                            .shimmer(),
                        content = {}
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Surface(
                    color = placeholder,
                    modifier = Modifier
                        .size(height = 18.dp, width = 100.dp)
                        .shimmer(),
                    content = {}
                )
                Spacer(modifier = Modifier.height(2.dp))
                Surface(
                    color = placeholder,
                    modifier = Modifier
                        .size(height = 16.dp, width = 90.dp)
                        .shimmer(),
                    content = {}
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(thickness = 1.5.dp)
    }
}
