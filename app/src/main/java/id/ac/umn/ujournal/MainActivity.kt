package id.ac.umn.ujournal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import id.ac.umn.ujournal.ui.components.BottomNavigationBar
import id.ac.umn.ujournal.ui.navigation.UJournalNavHost
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

        Scaffold(
            modifier = Modifier
                .statusBarsPadding(),
            bottomBar = {
                BottomNavigationBar(
                   navController
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