package id.ac.umn.ujournal.ui.components.journalentry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import id.ac.umn.ujournal.ui.util.HourTimeFormatter24
import java.time.LocalDateTime

@Composable
fun JournalEntryItem(
    title: String,
    description: String,
    createdAt: LocalDateTime,
    modifier: Modifier = Modifier
) {
    Card(
        modifier =  modifier,
        colors = CardDefaults.cardColors(
          MaterialTheme.colorScheme.primaryContainer
        )
    ){
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = CenterVertically,
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
        ) {
            Column {
                val formattedTime = createdAt.format(HourTimeFormatter24)

                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(androidx.compose.ui.graphics.Color.Gray)
                    .padding(16.dp)
            ){
            }
        }
    }
}