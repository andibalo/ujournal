package id.ac.umn.ujournal.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavType
import androidx.navigation.navArgument

interface UJournalDestination {
    val icon: ImageVector?
    val name: String
    val route: String
    val routeWithArgs: String?
}

object Home : UJournalDestination {
    override val icon = Icons.Filled.Book
    override val name = "Journey"
    override val route = "home"
    override val routeWithArgs = null
}

object Calendar : UJournalDestination {
    override val icon = Icons.Default.CalendarMonth
    override val name = "Calendar"
    override val route = "calendar"
    override val routeWithArgs = null
}

object Media : UJournalDestination {
    override val icon = Icons.Filled.PermMedia
    override val name = "Media"
    override val route = "media"
    override val routeWithArgs = null
}

object Map : UJournalDestination {
    override val icon = Icons.Filled.Map
    override val name = "Atlas"
    override val route = "map"
    override val routeWithArgs = null
}

object Profile : UJournalDestination {
    override val icon = null
    override val name = "Profile"
    override val route = "profile"
    override val routeWithArgs = null
}

object Login : UJournalDestination {
    override val icon = null
    override val name = "Login"
    override val route = "login"
    override val routeWithArgs = null
}

object Register : UJournalDestination {
    override val icon = null
    override val name = "Register"
    override val route = "register"
    override val routeWithArgs = null
}

object CreateJournalEntry : UJournalDestination {
    override val icon = null
    override val name = "Create Journal Entry"
    override val route = "journal-entry/create"
    override val routeWithArgs = null
}

object EditJournalEntry : UJournalDestination {
    override val icon = null
    override val name = "Edit Journal Entry"
    override val route = "journal-entry/edit"
    const val journalEntryIDArg = "journalEntryID"
    override val routeWithArgs = "${route}/{${journalEntryIDArg}}"
}

object JournalEntryDetail : UJournalDestination {

    override val icon = null
    override val name = "Journal Entry Detail"
    override val route = "journal-entry/detail"

    const val journalEntryIDArg = "journal_entry_id"

    override val routeWithArgs = "${route}/{${journalEntryIDArg}}"

    val arguments = listOf(
        navArgument(journalEntryIDArg) { type = NavType.StringType }
    )
}

object CalendarDateDetail : UJournalDestination {

    override val icon = null
    override val name = "Calendar Date Detail"
    override val route = "calendar/date"

    const val calendarDateArg = "calendar_date"

    override val routeWithArgs = "${route}/{${calendarDateArg}}"

    val arguments = listOf(
        navArgument(calendarDateArg) { type = NavType.StringType }
    )
}


val TOP_LEVEL_DESTINATIONS = listOf(Home, Calendar, Media, Map)
val uJournalAppScreens = listOf(
    Home,
    Calendar,
    CalendarDateDetail,
    Media,
    Map,
    Profile,
    Login,
    Register,
    CreateJournalEntry,
    EditJournalEntry,
    JournalEntryDetail
)