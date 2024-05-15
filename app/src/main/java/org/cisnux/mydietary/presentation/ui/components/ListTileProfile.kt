package org.cisnux.mydietary.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.cisnux.mydietary.R
import org.cisnux.mydietary.presentation.ui.theme.DietaryTheme

@Preview(showBackground = true)
@Composable
private fun ListTileProfilePreview() {
    DietaryTheme {
        ListTileProfile(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_age_100dp),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = stringResource(id = R.string.age_label),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            },
            bodyLabel = {
                Text(
                    text = "40 years old",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
        )
    }
}

@Composable
fun ListTileProfile(
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit,
    bodyLabel: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        Row {
            icon()
            Spacer(modifier = Modifier.width(8.dp))
            label()
        }
        bodyLabel()
    }
    Spacer(modifier = Modifier.height(8.dp))
    HorizontalDivider(thickness = 1.5.dp)
}
