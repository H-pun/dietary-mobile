package org.cisnux.mydietary.presentation.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer
import org.cisnux.mydietary.R
import org.cisnux.mydietary.presentation.ui.theme.DietaryTheme
import org.cisnux.mydietary.presentation.ui.theme.darkProgress
import org.cisnux.mydietary.presentation.ui.theme.lightProgress

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
private fun UserAccountCardPreview() {
    DietaryTheme {
        UserAccountCard(
            email = "fajrarisqulla@gmail.com",
            username = "fajrarisqulla",
            isVerified = true
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UserAccountCardShimmerPreview() {
    DietaryTheme {
        UserAccountCard()
    }
}

@Composable
fun UserAccountCard(
    modifier: Modifier = Modifier,
    username: String? = null,
    email: String? = null,
    isVerified: Boolean? = null
) {
    val context = LocalContext.current
    val placeholder = when (context.resources.configuration.uiMode) {
        Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL -> lightProgress
        else -> darkProgress
    }

    Surface {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                ) {}
                Text(
                    text = (username?.getOrNull(0) ?: 'u').uppercase(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    username?.let {
                        Text(
                            text = username,
                            style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                            fontWeight = FontWeight.SemiBold
                        )
                    } ?: Surface(
                        color = placeholder,
                        modifier = Modifier
                            .size(height = 22.dp, width = 100.dp)
                            .shimmer(),
                        content = {}
                    )
                    Spacer(Modifier.height(4.dp))
                    email?.let {
                        Text(
                            text = email,
                            style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onSurface),
                            fontWeight = FontWeight.SemiBold
                        )
                    } ?: Surface(
                        color = placeholder,
                        modifier = Modifier
                            .size(height = 18.dp, width = 150.dp)
                            .shimmer(),
                        content = {}
                    )
                }
                if (isVerified == true) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_verified_account_100dp),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp),
                        tint = MaterialTheme.colorScheme.tertiary,
                    )
                }
            }
        }
    }
}