package id.ac.umn.ujournal.ui.components.journalentry

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import id.ac.umn.ujournal.ui.util.HourTimeFormatter24
import id.ac.umn.ujournal.ui.util.ddMMMMyyyyDateTimeFormatter
import java.time.LocalDateTime

@Composable
fun JournalEntryItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    imageURI: String? = "",
    createdAt: LocalDateTime,
) {
    Card(
        modifier =  modifier,
        colors = CardDefaults.cardColors(
            containerColor =  MaterialTheme.colorScheme.primaryContainer
        )
    ){
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = CenterVertically,
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
        ) {
            Column {
                val formattedTime = createdAt.format(HourTimeFormatter24)
                val formattedDate = createdAt.format(ddMMMMyyyyDateTimeFormatter)

                Text(
                    text =  formattedDate + ", " +formattedTime,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer

                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            AsyncImage(
                model = imageURI,
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)),
                contentDescription = "Journal Entry Photo",
                contentScale = ContentScale.Crop
            )
        }
    }
}