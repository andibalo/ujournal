package id.ac.umn.ujournal.ui.components.journalentry

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material.icons.filled.PersonPinCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreateJournalEntryBottomTab(
    modifier: Modifier = Modifier,
    onMediaClick: () -> Unit = {},
    onLocationClick: () -> Unit = {},
) {
    Surface {
        Row(
            modifier = modifier.fillMaxWidth().padding(horizontal = 10.dp),
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = onMediaClick
            ) {
                Text(text = "Media")
                Spacer(Modifier.padding(horizontal = 4.dp))
                Icon(Icons.Filled.PermMedia, contentDescription = "Upload media")
            }

            Spacer(Modifier.padding(horizontal = 10.dp))

            Button(
                modifier = Modifier.weight(1f),
                onClick = onLocationClick
            ) {
                Text(text = "Geotag")
                Spacer(Modifier.padding(horizontal = 4.dp))
                Icon(
                    Icons.Filled.PersonPinCircle,
                    contentDescription = "Add journal entry geotag"
                )
            }
        }
    }
}