package id.ac.umn.ujournal.ui.journal

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import id.ac.umn.ujournal.model.JournalEntry
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
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateJournalEntryScreen(
    journalEntryViewModel: JournalEntryViewModel = viewModel(),
    onBackButtonClick : () -> Unit = {},
) {
    var photoUri: Uri? by rememberSaveable { mutableStateOf(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var entryTitle by rememberSaveable { mutableStateOf("") }
    var entryBody by rememberSaveable { mutableStateOf("") }
    var showLocationPicker by remember { mutableStateOf(false) }
    var latitude: Double? by rememberSaveable { mutableStateOf(null) }
    var longitude: Double? by rememberSaveable { mutableStateOf(null) }

    val currentDate = LocalDateTime.now()
    var entryDate: LocalDateTime by rememberSaveable {
        mutableStateOf(currentDate)
    }

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
        // TODO: add validation

        val journalEntry = JournalEntry(
            id = UUID.randomUUID(),
            title = entryTitle,
            description = entryBody,
            imageURI = photoUri.toString(),
            latitude = latitude,
            longitude = longitude,
            createdAt = entryDate,
            updatedAt = null
        )

        journalEntryViewModel.addJournalEntry(journalEntry)
    }

    if (showLocationPicker) {
        LocationPicker(
            onDismiss = {
                showLocationPicker = false
            },
            onLocationSelect = { coordinates ->
                latitude = coordinates.latitude
                longitude = coordinates.longitude
                showLocationPicker = false
            }
        )
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            UJournalTopAppBar(
                title = {
                    Text(
                        text = "Create Journal",
                    )
                },
                onBackButtonClick = onBackButtonClick,
                actions = {
                    IconButton(
                        onClick = {
                            onSubmitClick()
                            onBackButtonClick()
                        }
                    ) {
                        Icon(
                            Icons.Filled.Done,
                            contentDescription = "Create journal entry",
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
            modifier =
                Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
        ) {
            if (photoUri != null) {
                AsyncImage(
                    model = photoUri,
                    modifier = Modifier.fillMaxWidth().height(250.dp),
                    contentDescription = "Journal Entry Photo",
                    contentScale = ContentScale.Crop
                )
            }

            if (showDatePicker) {
                DatePickerModal(
                    onDismiss = {
                        showDatePicker = false
                    },
                    onDateSelected = { selectedTimestamp ->
                        if (selectedTimestamp != null) {
                            entryDate = LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(selectedTimestamp),
                                ZoneId.systemDefault()
                            )
                        }
                    },
                    dataInSeconds = entryDate.toLocalMilliseconds()
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
                            Text(
                                text = it.getAddressLine(0),
                                style = MaterialTheme.typography.labelMedium
                            )
                            Spacer(Modifier.padding(2.dp))
                        }
                        Text(
                            text = "%.4f".format(latitude) + ", %.4f".format(longitude),
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
                    text = entryDate.format(ddMMMMyyyyDateTimeFormatter)
                )
                Spacer(Modifier.padding(horizontal = 1.dp))
                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Date dropdown")
            }

            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                TextField(
                    value = entryTitle,
                    onValueChange = { entryTitle = it }, modifier = Modifier
                        .fillMaxWidth(),
                    label = {
                        Text("Title")
                    }
                )
                Spacer(Modifier.padding(vertical = 8.dp))
                TextField(
                    value = entryBody,
                    onValueChange = { entryBody = it }, modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .verticalScroll(rememberScrollState()),
                    label = {
                        Text("Description")
                    }
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