package id.ac.umn.ujournal.ui.components.journalentry

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.ac.umn.ujournal.ui.journal.JournalEntry

@Composable
fun JournalEntryList(
    list: List<JournalEntry>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
    ) {
        items(
            items = list,
            key = { entry -> entry.id }
        ) { entry ->
            JournalEntryItem(
                title = entry.title,
                description = entry.description,
                createdAt = entry.createdAt,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(
                modifier = Modifier
                    .padding(6.dp)
            )
        }
    }
}