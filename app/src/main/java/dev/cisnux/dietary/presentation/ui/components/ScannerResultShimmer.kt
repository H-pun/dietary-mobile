package dev.cisnux.dietary.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
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

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun ScannerResultShimmerPreview() {
    DietaryTheme {
        Surface {
            ScannerResultShimmer()
        }
    }
}

@Composable
fun ScannerResultShimmer(
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Surface(
                    color = placeholder,
                    modifier = Modifier
                        .width(110.dp)
                        .height(18.dp)
                        .shimmer()
                ) {}
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = placeholder, modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .shimmer()
                ) {}
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                repeat(3) {
                    Surface(
                        color = placeholder,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(25.dp)
                            .shimmer()
                    ) {}
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(thickness = 1.5.dp)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        repeat(5) {
            Surface(
                color = placeholder, modifier = Modifier
                    .fillMaxWidth()
                    .height(25.dp)
                    .shimmer()
            ) {}
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(thickness = 1.5.dp)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
