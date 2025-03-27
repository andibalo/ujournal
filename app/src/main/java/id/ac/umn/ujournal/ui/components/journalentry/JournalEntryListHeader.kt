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
    title: String = "Entries"
){
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.scrim
        )
        IconButton(onClick = onToggleSort) {
            Row {
                Icon(
                    Icons.AutoMirrored.Filled.Sort,
                    contentDescription = "Visibility Icon"
                )
            }
        }
    }
}
