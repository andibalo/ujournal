package id.ac.umn.ujournal.ui.journal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.ac.umn.ujournal.model.JournalEntry
import id.ac.umn.ujournal.ui.components.common.UJournalTopAppBar
import id.ac.umn.ujournal.viewmodel.JournalEntryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditJournalEntryScreen(
    journalEntryViewModel: JournalEntryViewModel = viewModel(),
    journalEntryID: String?,
    onBackButtonClick: () -> Unit = {},
) {
    if (journalEntryID == null) {
        onBackButtonClick()
        return
    }

    val journalEntry = remember(journalEntryID) { journalEntryViewModel.getJournalEntry(journalEntryID) }
    if (journalEntry == null) {
        onBackButtonClick()
        return
    }

    var entryTitle by rememberSaveable { mutableStateOf(journalEntry.title) }
    var entryBody by rememberSaveable { mutableStateOf(journalEntry.description) }

    fun onSubmitClick() {
        journalEntryViewModel.update(
            journalEntryID = journalEntry.id.toString(),
            newTitle = entryTitle,
            newDescription = entryBody
        )
        onBackButtonClick()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            UJournalTopAppBar(
                title = { Text(text = "Edit Journal") },
                onBackButtonClick = onBackButtonClick,
                actions = {
                    IconButton(onClick = { onSubmitClick() }) {
                        Icon(Icons.Filled.Done, contentDescription = "Save changes")
                    }
                }
            )
        }
    ) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                TextField(
                    value = entryTitle,
                    onValueChange = { entryTitle = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Title") }
                )
                Spacer(Modifier.height(8.dp))
                TextField(
                    value = entryBody,
                    onValueChange = { entryBody = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    label = { Text("Description") }
                )
                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { onSubmitClick() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }
            }
        }
    }
}