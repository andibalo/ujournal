package id.ac.umn.ujournal

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import id.ac.umn.ujournal.ui.components.common.navigation.UJournalNavigationWrapper
import id.ac.umn.ujournal.ui.navigation.UJournalNavHost
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


    val currentBackStack by navController.currentBackStackEntryAsState()

    val currentDestination = currentBackStack?.destination

    Surface {
        UJournalNavigationWrapper(
            currentDestination = currentDestination,
            navController = navController,
        ) {
            UJournalNavHost(
                navController = navController,
                themeViewModel = themeViewModel
            )
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
