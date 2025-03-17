package id.ac.umn.ujournal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import id.ac.umn.ujournal.ui.components.BottomNavigationBar
import id.ac.umn.ujournal.ui.navigation.CreateJournalEntry
import id.ac.umn.ujournal.ui.navigation.Home
import id.ac.umn.ujournal.ui.navigation.Login
import id.ac.umn.ujournal.ui.navigation.Profile
import id.ac.umn.ujournal.ui.navigation.Register
import id.ac.umn.ujournal.ui.navigation.UJournalNavHost
import id.ac.umn.ujournal.ui.navigation.uJournalAppScreens
import id.ac.umn.ujournal.ui.theme.UJournalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UJournalApp()
        }
    }
}

@Composable
fun UJournalApp() {
    UJournalTheme {
        val navController = rememberNavController()

        val bottomBarState = rememberSaveable { (mutableStateOf(true)) }

        val topBarState = rememberSaveable { (mutableStateOf(true)) }

        val currentBackStack by navController.currentBackStackEntryAsState()

        val currentDestination = currentBackStack?.destination

        // Change the variable to this and use home as a backup screen if this returns null
        val currentScreen = uJournalAppScreens.find { it.route == currentDestination?.route } ?: Home

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

        ) { innerPadding ->
            UJournalNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
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
        else -> {
            return true
        }
    }
}