package id.ac.umn.ujournal.ui.components.common.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import id.ac.umn.ujournal.R
import id.ac.umn.ujournal.ui.navigation.TOP_LEVEL_DESTINATIONS
import id.ac.umn.ujournal.ui.util.UJournalNavigationContentPosition
import id.ac.umn.ujournal.ui.util.hasRoute

@Composable
fun PermanentNavigationDrawerContent(
    currentDestination: NavDestination?,
    navigationContentPosition: UJournalNavigationContentPosition,
    navController: NavHostController,
    onAddJournalClick: () -> Unit = {},
) {
    PermanentDrawerSheet(
        modifier = Modifier.sizeIn(minWidth = 200.dp, maxWidth = 300.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Layout(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(16.dp),
            content = {
                Column(
                    modifier = Modifier.layoutId(LayoutType.HEADER),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_blue),
                        modifier = Modifier.fillMaxWidth().height(75.dp),
                        contentDescription = "App Logo"
                    )
                    Text(
                        modifier = Modifier
                            .padding(16.dp).fillMaxWidth(),
                        text = "U-Journal",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                    )
                    ExtendedFloatingActionButton(
                        onClick = onAddJournalClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 40.dp),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            "Add new journal entry floating action button",
                        )
                        Text(
                            text = "Add Journal",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .layoutId(LayoutType.CONTENT)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    TOP_LEVEL_DESTINATIONS.forEach { destination ->
                        NavigationDrawerItem(
                            selected = currentDestination.hasRoute(destination.route),
                            label = {
                                Text(
                                    text = destination.name,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            },
                            icon = { destination.icon?.let { Icon(it, contentDescription = destination.route) } },
                            colors = NavigationDrawerItemDefaults.colors(
                                unselectedContainerColor = Color.Transparent
                            ),
                            onClick = { navController.navigate(destination.route) }
                        )
                    }
                }
            },
            measurePolicy = navigationMeasurePolicy(navigationContentPosition)
        )
    }
}

@Composable
fun ModalNavigationDrawerContent(
    currentDestination: NavDestination?,
    navigationContentPosition: UJournalNavigationContentPosition,
    navController: NavHostController,
    onDrawerClicked: () -> Unit = {},
    onAddJournalClick: () -> Unit = {},
) {
    ModalDrawerSheet {
        Layout(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.inverseOnSurface)
                .padding(16.dp),
            content = {
                Column(
                    modifier = Modifier.layoutId(LayoutType.HEADER),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_blue),
                        modifier = Modifier.fillMaxWidth().height(75.dp),
                        contentDescription = "App Logo"
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "U-Journal",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                        )
                        IconButton(onClick = onDrawerClicked) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuOpen,
                                contentDescription = "Drawer Icon"
                            )
                        }
                    }

                    ExtendedFloatingActionButton(
                        onClick = onAddJournalClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 40.dp),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            "Add new journal entry floating action button",
                        )
                        Text(
                            text = "Add Journal",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .layoutId(LayoutType.CONTENT)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    TOP_LEVEL_DESTINATIONS.forEach { destination ->
                        NavigationDrawerItem(
                            selected = currentDestination.hasRoute(destination.route),
                            label = {
                                Text(
                                    text = destination.name,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            },
                            icon = { destination.icon?.let { Icon(it, contentDescription = destination.route) } },
                            colors = NavigationDrawerItemDefaults.colors(
                                unselectedContainerColor = Color.Transparent
                            ),
                            onClick = { navController.navigate(destination.route) }
                        )
                    }
                }
            },
            measurePolicy = navigationMeasurePolicy(navigationContentPosition)
        )
    }
}

fun navigationMeasurePolicy(
    navigationContentPosition: UJournalNavigationContentPosition,
): MeasurePolicy {
    return MeasurePolicy { measurables, constraints ->
        lateinit var headerMeasurable: Measurable
        lateinit var contentMeasurable: Measurable
        measurables.forEach {
            when (it.layoutId) {
                LayoutType.HEADER -> headerMeasurable = it
                LayoutType.CONTENT -> contentMeasurable = it
                else -> error("Unknown layoutId encountered!")
            }
        }

        val headerPlaceable = headerMeasurable.measure(constraints)
        val contentPlaceable = contentMeasurable.measure(
            constraints.offset(vertical = -headerPlaceable.height)
        )
        layout(constraints.maxWidth, constraints.maxHeight) {
            // Place the header, this goes at the top
            headerPlaceable.placeRelative(0, 0)

            // Determine how much space is not taken up by the content
            val nonContentVerticalSpace = constraints.maxHeight - contentPlaceable.height

            val contentPlaceableY = when (navigationContentPosition) {
                // Figure out the place we want to place the content, with respect to the
                // parent (ignoring the header for now)
                UJournalNavigationContentPosition.TOP -> 0
                UJournalNavigationContentPosition.CENTER -> nonContentVerticalSpace / 2
            }
                // And finally, make sure we don't overlap with the header.
                .coerceAtLeast(headerPlaceable.height)

            contentPlaceable.placeRelative(0, contentPlaceableY)
        }
    }
}
