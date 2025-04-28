package id.ac.umn.ujournal.ui.map

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import id.ac.umn.ujournal.data.model.JournalEntry
import id.ac.umn.ujournal.ui.util.HourTimeFormatter24
import id.ac.umn.ujournal.ui.util.ddMMMMyyyyDateTimeFormatter
import id.ac.umn.ujournal.viewmodel.JournalEntryViewModel

@SuppressLint("UnrememberedMutableState")
@Composable
fun MapScreen(
    journalEntryViewModel: JournalEntryViewModel = viewModel(),
    navigateToJournalDetail: (JournalEntry) -> Unit
) {
    val journalEntries by journalEntryViewModel.journalEntries.collectAsState()
    var showMarkerInfo by remember { mutableStateOf(false) }
    var selectedJournalEntry: JournalEntry? by remember { mutableStateOf(null) }

    val defaultLocation = journalEntries.firstOrNull {
        it.latitude != null && it.longitude != null
    }?.let {
        LatLng(it.latitude!!, it.longitude!!)
    } ?: LatLng(-6.31, 106.66)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 15f)
    }

    LaunchedEffect(key1 = cameraPositionState.isMoving) {
        if (showMarkerInfo){
            showMarkerInfo = false
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ){
            for (journalEntry in journalEntries) {
                if (journalEntry.latitude != null && journalEntry.longitude != null) {
                    Marker(
                        state = MarkerState(position = LatLng(
                            journalEntry.latitude!!,
                            journalEntry.longitude!!
                        )),
                        onClick = {
                            selectedJournalEntry = journalEntry
                            showMarkerInfo = !showMarkerInfo
                            true
                        }
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = showMarkerInfo && selectedJournalEntry != null,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight }
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight }
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        navigateToJournalDetail(selectedJournalEntry!!)
                    },
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            ){
                Row(
                    modifier = Modifier.padding(15.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ){
                        val formattedTime = selectedJournalEntry?.createdAt?.format(HourTimeFormatter24)
                        val formattedDate = selectedJournalEntry?.createdAt?.format(ddMMMMyyyyDateTimeFormatter)

                        if(selectedJournalEntry?.imageURI != null) {
                            AsyncImage(
                                model = selectedJournalEntry!!.imageURI,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentDescription = "Journal Entry Photo",
                                contentScale = ContentScale.Crop
                            )
                        }
                        Column {
                            Text(
                                text =  formattedDate + ", " +formattedTime,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = selectedJournalEntry?.title!!,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                    }
                    Icon(Icons.Filled.ChevronRight, contentDescription = "Arrow right")
                }
            }
        }
    }
}