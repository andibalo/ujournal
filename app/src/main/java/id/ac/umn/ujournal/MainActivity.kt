package id.ac.umn.ujournal

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import id.ac.umn.ujournal.ui.components.common.BottomNavigationBar
import id.ac.umn.ujournal.ui.navigation.CalendarDateDetail
import id.ac.umn.ujournal.ui.navigation.CreateJournalEntry
import id.ac.umn.ujournal.ui.navigation.EditJournalEntry
import id.ac.umn.ujournal.ui.navigation.Home
import id.ac.umn.ujournal.ui.navigation.JournalEntryDetail
import id.ac.umn.ujournal.ui.navigation.Login
import id.ac.umn.ujournal.ui.navigation.Profile
import id.ac.umn.ujournal.ui.navigation.Register
import id.ac.umn.ujournal.ui.navigation.UJournalNavHost
import id.ac.umn.ujournal.ui.navigation.uJournalAppScreens
import id.ac.umn.ujournal.ui.theme.UJournalTheme
import id.ac.umn.ujournal.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            UJournalTheme(
                themeViewModel = themeViewModel
            ) {
                UJournalApp(themeViewModel = themeViewModel)
            }
        }
    }
}

@Composable
fun UJournalApp(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()

    val bottomBarState = rememberSaveable { (mutableStateOf(true)) }

    val topBarState = rememberSaveable { (mutableStateOf(true)) }

    val currentBackStack by navController.currentBackStackEntryAsState()

    val currentDestination = currentBackStack?.destination

    // Change the variable to this and use home as a backup screen if this returns null
    val currentScreen = uJournalAppScreens.find {
        it.route == currentDestination?.route || it.routeWithArgs == currentDestination?.route
    } ?: Home

    bottomBarState.value = shouldShowBottomNavBar(currentScreen.route)
    topBarState.value = shouldShowTopAppBar(currentScreen.route)

    Scaffold(
        modifier = Modifier
            .statusBarsPadding(),
        bottomBar = {
            BottomNavigationBar(
               navController,
               bottomBarState
            )
        },

    ) { innerPadding: PaddingValues ->
        UJournalNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            themeViewModel = themeViewModel
        )
    }
}


fun shouldShowBottomNavBar(route : String) : Boolean {

    when (route) {
        Profile.route -> {
            return false
        }
        Login.route -> {
            return false
        }
        Register.route -> {
            return false
        }
        CreateJournalEntry.route -> {
            return false
        }
        EditJournalEntry.route -> {
            return false
        }
        JournalEntryDetail.route -> {
            return false
        }
        CalendarDateDetail.route -> {
            return false
        }
        else -> {
            return true
        }
    }
}

fun shouldShowTopAppBar(route : String) : Boolean {

    when (route) {
        Profile.route -> {
            return false
        }
        Login.route -> {
            return false
        }
        Register.route -> {
            return false
        }
        CreateJournalEntry.route -> {
            return false
        }
        JournalEntryDetail.route -> {
            return false
        }
        CalendarDateDetail.route -> {
            return false
        }
        else -> {
            return true
        }
    }
}

@Preview(
    uiMode = UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Composable
fun UJournalAppPreviewDark() {
//    UJournalTheme {
//        UJournalApp()
//    }
}

@Preview(
    uiMode = UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
@Composable
fun UJournalAppPreviewLight() {
//    UJournalTheme {
//        UJournalApp()
//    }
}
