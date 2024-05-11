package org.cisnux.mydietary.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer
import org.cisnux.mydietary.presentation.ui.theme.darkProgress
import org.cisnux.mydietary.presentation.ui.theme.lightProgress

@Composable
fun UserNutritionCardShimmer(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val placeholder = when (context.resources.configuration.uiMode) {
        Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL -> lightProgress
        else -> darkProgress
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(4) {
            Surface(
                color = placeholder, modifier = Modifier
                    .size(115.dp)
                    .clip(CircleShape)
                    .shimmer()
            ) {}
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}