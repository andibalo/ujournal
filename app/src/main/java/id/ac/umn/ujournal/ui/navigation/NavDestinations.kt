package id.ac.umn.ujournal.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.ui.graphics.vector.ImageVector


interface UJournalDestination {
    val icon: ImageVector
    val name: String
    val route: String
}

object Home : UJournalDestination {
    override val icon = Icons.Filled.Home
    override val name = "Home"
    override val route = "home"
}

object Calendar : UJournalDestination {
    override val icon = Icons.Default.CalendarMonth
    override val name = "Calendar"
    override val route = "calendar"
}

object Media : UJournalDestination {
    override val icon = Icons.Filled.PermMedia
    override val name = "Media"
    override val route = "media"
}

object Map : UJournalDestination {
    override val icon = Icons.Filled.Map
    override val name = "Map"
    override val route = "map"
}

val bottomTabRowScreens = listOf(Home, Calendar, Media, Map)