package org.cisnux.mydietary.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer
import org.cisnux.mydietary.presentation.ui.theme.DietaryTheme
import org.cisnux.mydietary.presentation.ui.theme.darkProgress
import org.cisnux.mydietary.presentation.ui.theme.lightProgress

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun ScannerResultShimmerPreview() {
    DietaryTheme {
        Surface {
            AddedDiaryShimmer()
        }
    }
}

@Composable
fun AddedDiaryShimmer(
    modifier: Modifier = Modifier
) {
    val placeholder = if(!isSystemInDarkTheme())lightProgress
        else darkProgress
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        Column {
            Surface(
                color = placeholder, modifier = Modifier
                    .width(180.dp)
                    .height(20.dp)
                    .shimmer()
            ) {}
            Spacer(modifier = Modifier.height(8.dp))
            UserNutritionCardShimmer()
        }
        Spacer(modifier = Modifier.height(16.dp))
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
