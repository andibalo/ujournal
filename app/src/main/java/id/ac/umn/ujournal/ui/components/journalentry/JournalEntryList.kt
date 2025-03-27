package id.ac.umn.ujournal.ui.components.journalentry

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.ac.umn.ujournal.model.JournalEntry

@Composable
fun JournalEntryList(
    modifier: Modifier = Modifier,
    list: List<JournalEntry>,
    onJournalEntryClick : (journalEntryID: String) -> Unit = {},
    state : LazyListState = rememberLazyListState()
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(8.dp),
        state = state
    ) {
        items(
            items = list,
            key = { entry -> entry.id }
        ) { entry ->
            JournalEntryItem(
                title = entry.title,
                description = entry.description,
                imageURI = entry.imageURI,
                createdAt = entry.createdAt,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onJournalEntryClick(entry.id.toString())
                    }
            )
            Spacer(
                modifier = Modifier
                    .padding(8.dp)
            )
        }
    }
}