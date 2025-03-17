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
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import id.ac.umn.ujournal.ui.components.UJournalTopAppBar
import id.ac.umn.ujournal.ui.components.journalentry.JournalEntryList
import id.ac.umn.ujournal.ui.journal.JournalEntryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    journalEntryViewModel: JournalEntryViewModel = viewModel(),
    onProfileClick : () -> Unit = {},
    onFABClick : () -> Unit = {},
) {

    // TODO: list not recomposing after adding new journal entry from create journal entry screen

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            UJournalTopAppBar(
                title = {
                    // TODO:  make dynamic
                    Text(
                        text = "Hello, Andi",
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
    ) { padding: PaddingValues ->
        Column (
            modifier = Modifier.padding(padding).fillMaxSize(),
        ) {
            JournalEntryList(
                list = journalEntryViewModel.journalEntries,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

