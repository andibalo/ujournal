package id.ac.umn.ujournal.ui.components.common.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import id.ac.umn.ujournal.ui.navigation.TOP_LEVEL_DESTINATIONS
import id.ac.umn.ujournal.ui.util.hasRoute

@Composable
fun UJournalNavigationRail(
    currentDestination: NavDestination?,
    navController: NavHostController,
    onDrawerClicked: () -> Unit = {},
    onAddJournalClick: () -> Unit = {},
    isVisible: Boolean
) {
    AnimatedVisibility(
        visible = isVisible,
    ) {
        NavigationRail(
            modifier = Modifier.fillMaxHeight(),
            containerColor = MaterialTheme.colorScheme.inverseOnSurface
        ) {
            Column(
                modifier = Modifier.layoutId(LayoutType.HEADER),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                NavigationRailItem(
                    selected = false,
                    onClick = onDrawerClicked,
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Drawer button"
                        )
                    }
                )
                FloatingActionButton(
                    onClick = onAddJournalClick,
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        Icons.Filled.Add,
                        "Add new journal entry floating action button",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(Modifier.height(8.dp)) // NavigationRailHeaderPadding
                Spacer(Modifier.height(4.dp)) // NavigationRailVerticalPadding
            }

            Column(
                modifier = Modifier.layoutId(LayoutType.CONTENT),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TOP_LEVEL_DESTINATIONS.forEach { destination ->
                    NavigationRailItem(
                        selected = currentDestination.hasRoute(destination.route),
                        onClick = {  navController.navigate(destination.route) },
                        icon = { destination.icon?.let { Icon(it, contentDescription = destination.route) } },
                    )
                }
            }
        }
    }
}
