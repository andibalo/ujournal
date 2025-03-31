package id.ac.umn.ujournal.ui.calendar

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.ac.umn.ujournal.ui.components.common.UJournalTopAppBar
import id.ac.umn.ujournal.ui.components.journalentry.JournalEntryList
import id.ac.umn.ujournal.ui.components.journalentry.JournalEntryListHeader
import id.ac.umn.ujournal.viewmodel.JournalEntryViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarDataDetailScreen(
    selectedDate: String?,
    journalEntryViewModel: JournalEntryViewModel = viewModel(),
    onBackButtonClick : () -> Unit = {},
    onJournalEntryClick : (journalEntryID: String) -> Unit = {},
) {
    // TODO: remove log from production
    Log.d("CalendarDataDetailScreen", "selectedDate: $selectedDate")

    if (selectedDate == null) {
        onBackButtonClick()
        return
    }

    var isSortedDescending by remember { mutableStateOf(true) }
    val journalEntries by journalEntryViewModel.journalEntries.collectAsState()

    val sortedEntries by remember(journalEntries, isSortedDescending) {
        derivedStateOf {
            if (isSortedDescending) {
                journalEntries.sortedByDescending { it.createdAt }
            } else {
                journalEntries.sortedBy { it.createdAt }
            }
        }
    }

    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)

    val selectedDateTime = try {
        LocalDate.parse(selectedDate, DateTimeFormatter.ISO_DATE)
    } catch (e: Exception) {
        Log.e("CalendarDataDetailScreen", "error: $e")

        onBackButtonClick()
        null
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            UJournalTopAppBar(
                title = {
                    Text(
                        text = dateFormatter.format(selectedDateTime),
                    )
                },
                onBackButtonClick = onBackButtonClick,
            )
        },
    ) { innerPadding: PaddingValues ->
        Column (
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding()).fillMaxSize(),
        ) {
            JournalEntryList(
                list = sortedEntries.filter { journalEntry ->
                    journalEntry.createdAt.toLocalDate().toString() == selectedDate
                },
                modifier = Modifier.fillMaxSize(),
                onJournalEntryClick = onJournalEntryClick,
                contentPadding = PaddingValues(8.dp),
                listHeaderContent = {
                    JournalEntryListHeader(
                        modifier = Modifier.fillMaxWidth(),
                        onToggleSort = {
                            isSortedDescending = !isSortedDescending
                        }
                    )
                },
            )
        }
    }
}