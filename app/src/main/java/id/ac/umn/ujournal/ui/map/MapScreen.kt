package id.ac.umn.ujournal.ui.map

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import id.ac.umn.ujournal.model.JournalEntry
import id.ac.umn.ujournal.viewmodel.JournalEntryViewModel

@SuppressLint("UnrememberedMutableState")
@Composable
fun MapScreen(
    journalEntryViewModel: JournalEntryViewModel = viewModel(),
    onMarkerClick: (JournalEntry) -> Unit
) {
    val journalEntries by journalEntryViewModel.journalEntries.collectAsState()

    // TODO: adjust flow to show selected short info before navigation
    // var selectedJournalEntry: JournalEntry? by rememberSaveable { mutableStateOf(null) }

    val defaultLocation = journalEntries.firstOrNull {
        it.latitude != null && it.longitude != null
    }?.let {
        LatLng(it.latitude!!, it.longitude!!)
    } ?: LatLng(-6.31, 106.66)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 15f)
    }

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
                        // TODO: adjust flow to show selected short info before navigation
                        // selectedJournalEntry = journalEntry

                        onMarkerClick(journalEntry)
                        true
                    }
                )
            }
        }
    }
}