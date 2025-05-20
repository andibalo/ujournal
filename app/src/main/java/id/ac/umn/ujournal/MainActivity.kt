package id.ac.umn.ujournal

import android.content.Context
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
import androidx.credentials.CredentialManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import id.ac.umn.ujournal.data.repository.FirebaseRepository
import id.ac.umn.ujournal.data.repository.FirebaseRepositoryImpl
import id.ac.umn.ujournal.ui.components.common.navigation.UJournalNavigationWrapper
import id.ac.umn.ujournal.ui.navigation.UJournalNavHost
import id.ac.umn.ujournal.ui.theme.UJournalTheme
import id.ac.umn.ujournal.viewmodel.AuthViewModel
import id.ac.umn.ujournal.viewmodel.JournalEntryViewModel
import id.ac.umn.ujournal.viewmodel.ThemeViewModel
import id.ac.umn.ujournal.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val context: Context = applicationContext

        setContent {

            val firebaseRepo : FirebaseRepository = FirebaseRepositoryImpl(
                auth = FirebaseAuth.getInstance(),
                db =  FirebaseFirestore.getInstance()
            )

            val themeViewModel: ThemeViewModel = viewModel()
            val journalEntryViewModel: JournalEntryViewModel = viewModel()
            val userViewModel =  UserViewModel(
                firebaseRepository = firebaseRepo
            )

            val authViewModel = AuthViewModel(
                credentialManager = CredentialManager.create(context),
                firebaseRepository = firebaseRepo
            )

            UJournalTheme(
                themeViewModel = themeViewModel
            ) {
                UJournalApp(
                    themeViewModel = themeViewModel,
                    authViewModel = authViewModel,
                    journalEntryViewModel = journalEntryViewModel,
                    userViewModel = userViewModel
                )
            }
        }
    }
}

@Composable
fun UJournalApp(
    themeViewModel: ThemeViewModel,
    authViewModel: AuthViewModel,
    journalEntryViewModel: JournalEntryViewModel,
    userViewModel: UserViewModel
) {
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
                themeViewModel = themeViewModel,
                authViewModel = authViewModel,
                journalEntryViewModel = journalEntryViewModel,
                userViewModel = userViewModel
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
