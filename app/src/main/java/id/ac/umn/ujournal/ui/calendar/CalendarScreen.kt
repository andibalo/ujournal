package id.ac.umn.ujournal.ui.calendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import id.ac.umn.ujournal.ui.components.common.UJournalTopAppBar
import id.ac.umn.ujournal.ui.components.calendar.Calendar
import id.ac.umn.ujournal.viewmodel.JournalEntryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    journalEntryViewModel: JournalEntryViewModel = viewModel(),
    onProfileClick : () -> Unit = {},
) {

    val journalEntries by journalEntryViewModel.journalEntries.collectAsState()

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
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = "User Profile")
                    }
                },
                showBackButton = false
            )
        },
    ) { innerPadding: PaddingValues ->
        Column (
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding()).fillMaxSize(),
        ) {
           Calendar(
               journalEntries = journalEntries
           )
        }
    }
}