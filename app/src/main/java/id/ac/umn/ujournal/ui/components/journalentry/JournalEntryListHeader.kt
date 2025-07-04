package id.ac.umn.ujournal.ui.components.journalentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun JournalEntryListHeader(
    modifier: Modifier = Modifier,
    onToggleSort: () -> Unit = {},
    title: String = "Entries",
    isSortedDescending: Boolean = true
){
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isSortedDescending) "Newest" else "Oldest",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = onToggleSort) {
                Icon(
                    Icons.AutoMirrored.Filled.Sort,
                    contentDescription = "Toggle Sorting Icon"
                )
            }
        }
    }
}
