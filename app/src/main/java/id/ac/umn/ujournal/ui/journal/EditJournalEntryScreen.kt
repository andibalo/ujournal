package id.ac.umn.ujournal.ui.journal

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import id.ac.umn.ujournal.ui.components.common.DatePickerModal
import id.ac.umn.ujournal.ui.components.common.LocationPicker
import id.ac.umn.ujournal.ui.components.common.MediaActions
import id.ac.umn.ujournal.ui.components.common.UJournalBottomSheet
import id.ac.umn.ujournal.ui.components.common.UJournalTopAppBar
import id.ac.umn.ujournal.ui.components.journalentry.CreateJournalEntryBottomTab
import id.ac.umn.ujournal.ui.util.ddMMMMyyyyDateTimeFormatter
import id.ac.umn.ujournal.ui.util.getAddressFromLatLong
import id.ac.umn.ujournal.ui.util.toLocalMilliseconds
import id.ac.umn.ujournal.viewmodel.JournalEntryViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditJournalEntryScreen(
    journalEntryViewModel: JournalEntryViewModel = viewModel(),
    journalEntryID: String?,
    onBackButtonClick: () -> Unit = {},
) {
    if (journalEntryID == null) {
        onBackButtonClick()
        return
    }

    val journalEntry = remember(journalEntryID) { journalEntryViewModel.getJournalEntry(journalEntryID) }
    if (journalEntry == null) {
        onBackButtonClick()
        return
    }

    var entryTitle by rememberSaveable { mutableStateOf(journalEntry.title) }
    var entryBody by rememberSaveable { mutableStateOf(journalEntry.description) }
    var photoUri by rememberSaveable { mutableStateOf(journalEntry.imageURI?.let { Uri.parse(it) }) }
    var latitude by rememberSaveable { mutableStateOf(journalEntry.latitude) }
    var longitude by rememberSaveable { mutableStateOf(journalEntry.longitude) }
    var createdAt by rememberSaveable { mutableStateOf(journalEntry.createdAt) }
    var showLocationPicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    var isBottomSheetVisible by remember { mutableStateOf(false) }

    fun showBottomSheet() {
        coroutineScope.launch {
            sheetState.show()
            isBottomSheetVisible = true
        }
    }

    fun hideBottomSheet() {
        coroutineScope.launch {
            sheetState.hide()
            isBottomSheetVisible = false
        }
    }

    fun onSubmitClick() {
        journalEntryViewModel.update(
            journalEntryID = journalEntry.id.toString(),
            newTitle = entryTitle,
            newDescription = entryBody,
            newImageURI = photoUri.toString(),
            newLatitude = latitude,
            newLongitude = longitude,
            updatedAt = LocalDateTime.now(),
            newDate = createdAt
        )
        onBackButtonClick()
    }

    if (showLocationPicker) {
        LocationPicker(
            onDismiss = { showLocationPicker = false },
            onLocationSelect = { coordinates ->
                latitude = coordinates.latitude
                longitude = coordinates.longitude
                showLocationPicker = false
            }
        )
        return
    }

    if(showDatePicker){
        DatePickerModal(
            onDismiss = {
                showDatePicker = false
            },
            onDateSelected = {
                    selectedTimestamp ->
                if (selectedTimestamp != null) {
                    createdAt = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(selectedTimestamp),
                        ZoneId.systemDefault()
                    )
                }
            },
            dataInSeconds = createdAt.toLocalMilliseconds()
        )
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            UJournalTopAppBar(
                title = { Text(text = "Edit Journal") },
                onBackButtonClick = onBackButtonClick,
                actions = {
                    IconButton(onClick = { onSubmitClick() }) {
                        Icon(
                            Icons.Filled.Done,
                            contentDescription = "Save changes",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        bottomBar = {
            CreateJournalEntryBottomTab(
                onMediaClick = {
                    showBottomSheet()
                },
                onLocationClick = {
                    showLocationPicker = true
                }
            )
        }
    ) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (photoUri != null) {
                AsyncImage(
                    model = photoUri,
                    modifier = Modifier.fillMaxWidth().height(250.dp),
                    contentDescription = "Journal Entry Photo",
                    contentScale = ContentScale.Crop
                )
            }

            if (latitude != null && longitude != null) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = "Location icon",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Column {
                        getAddressFromLatLong(
                            useDeprecated = true,
                            lat = latitude!!,
                            lon = longitude!!,
                            context = LocalContext.current
                        )?.let {
                            Text(text = it.getAddressLine(0), style = MaterialTheme.typography.labelMedium)
                            Spacer(Modifier.padding(2.dp))
                        }
                        Text(
                            text = "%.4f, %.4f".format(latitude, longitude),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            TextButton(
                onClick = {
                    showDatePicker = true
                }
            ) {
                Text(
                    text = createdAt.format(ddMMMMyyyyDateTimeFormatter)
                )
                Spacer(Modifier.padding(horizontal = 1.dp))
                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Date dropdown")
            }

            Column(modifier = Modifier.padding(10.dp)) {
                TextField(
                    value = entryTitle,
                    onValueChange = { entryTitle = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Title") }
                )
                Spacer(Modifier.padding(vertical = 8.dp))
                TextField(
                    value = entryBody,
                    onValueChange = { entryBody = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .verticalScroll(rememberScrollState()),
                    label = { Text("Description") }
                )
            }
            if (sheetState.isVisible) {
                UJournalBottomSheet(
                    sheetState = sheetState,
                    onDismiss = { hideBottomSheet() }
                ) {
                    MediaActions(
                        onSuccessTakePicture = {
                            photoUri = it
                            hideBottomSheet()
                        },
                        onSuccessChooseFromGallery = {
                            photoUri = it
                            hideBottomSheet()
                        }
                    )
                }
            }
        }
    }
}