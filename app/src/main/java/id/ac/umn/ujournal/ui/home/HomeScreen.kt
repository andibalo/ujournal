package id.ac.umn.ujournal.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import id.ac.umn.ujournal.ui.components.common.ErrorScreen
import id.ac.umn.ujournal.ui.components.common.LoadingScreen
import id.ac.umn.ujournal.ui.components.common.UJournalTopAppBar
import id.ac.umn.ujournal.ui.components.journalentry.JournalEntryList
import id.ac.umn.ujournal.viewmodel.JournalEntryViewModel
import id.ac.umn.ujournal.viewmodel.UserState
import id.ac.umn.ujournal.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userViewModel: UserViewModel = viewModel(),
    journalEntryViewModel: JournalEntryViewModel = viewModel(),
    onProfileClick : () -> Unit = {},
    onFABClick : () -> Unit = {},
    onJournalEntryClick : (journalEntryID: String) -> Unit = {},
) {

    val journalEntries by journalEntryViewModel.journalEntries.collectAsState()
    val userState by userViewModel.userState.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.loadUserData()
    }

    if (userState == UserState.Loading) {
        LoadingScreen()
        return
    }

    if (userState is UserState.Error) {
        ErrorScreen()
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            UJournalTopAppBar(
                title = {
                    val user = (userState as UserState.Success).user
                    Text(
                        text = "Hello, " + user.firstName
                    )
                },
                actions = {
                    // TODO: implement search
                    IconButton(onClick = { /* Do something */ }) {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = "User Profile")
                    }
                },
                showBackButton = false
            )
        },
        floatingActionButton = {
            FloatingActionButton (
                onClick = onFABClick,
                shape = CircleShape
                ) {
                Icon(Icons.Filled.Add, "Add new journal entry floating action button")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { innerPadding: PaddingValues ->
        Column (
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding()).fillMaxSize(),
        ) {
            JournalEntryList(
                list = journalEntries,
                modifier = Modifier.fillMaxSize(),
                onJournalEntryClick = onJournalEntryClick
            )
        }
    }
}

