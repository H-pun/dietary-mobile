package org.cisnux.mydietary.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.cisnux.mydietary.R
import org.cisnux.mydietary.presentation.navigation.NavigationItem
import org.cisnux.mydietary.presentation.ui.theme.DietaryTheme
import org.cisnux.mydietary.commons.utils.AppDestination

@Preview(showBackground = true)
@Composable
private fun NavigationDrawerPreview() {
    DietaryTheme {
        NavigationDrawer(
            currentRoute = AppDestination.HomeRoute,
            title = {
                Text("Dietary", modifier = Modifier.padding(horizontal = 16.dp))
            },
            onSelectedDestination = { _, _ -> },
            content = {
                Scaffold {
                    Column(Modifier.padding(it)) {
                        Text(text = "Body")
                    }
                }
            })
    }
}

@Composable
fun NavigationDrawer(
    currentRoute: AppDestination,
    onSelectedDestination: (destination: AppDestination, currentRoute: AppDestination) -> Unit,
    modifier: Modifier = Modifier,
    signOut: () -> Unit = {},
    title: @Composable ColumnScope.() -> Unit = {},
    navigationItems: List<NavigationItem<*>> = listOf(
        NavigationItem(
            title = stringResource(id = R.string.home_title),
            icon = Icons.Rounded.Home,
            destination = AppDestination.HomeRoute,
            contentDescription = stringResource(id = R.string.home_title)
        ),
        NavigationItem(
            title = stringResource(id = R.string.account_and_security_title),
            icon = painterResource(id = R.drawable.ic_protect_100dp),
            destination = AppDestination.SecurityAccountRoute,
            contentDescription = stringResource(id = R.string.account_and_security_title)
        ),
        NavigationItem(
            title = stringResource(id = R.string.sign_out_title),
            icon = painterResource(id = R.drawable.ic_logout_24dp),
            destination = AppDestination.SignInRoute,
            contentDescription = stringResource(id = R.string.sign_out_title)
        ),
    ),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    content: @Composable () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        modifier = modifier,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                title()
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                navigationItems.forEach { navigationItem ->
                    NavigationDrawerItem(
                        icon = {
                            when (navigationItem.icon) {
                                is ImageVector -> Icon(
                                    imageVector = navigationItem.icon,
                                    contentDescription = null
                                )

                                is Painter ->
                                    Icon(
                                        painter = navigationItem.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                            }
                        },
                        label = {
                            Text(
                                navigationItem.title,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.clearAndSetSemantics {},
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        selected = currentRoute == navigationItem.destination,
                        onClick = {
                            coroutineScope.launch {
                                drawerState.close()
                                if(navigationItem.destination != AppDestination.SignInRoute)
                                    onSelectedDestination(
                                        navigationItem.destination,
                                        currentRoute
                                    )
                                else
                                    signOut()
                            }
                        }
                    )
                }
            }
        },
        content = content
    )
}
