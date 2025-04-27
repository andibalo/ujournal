package id.ac.umn.ujournal.ui.journal

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.core.layout.WindowWidthSizeClass
import coil3.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import id.ac.umn.ujournal.model.JournalEntry
import id.ac.umn.ujournal.ui.components.common.DatePickerModal
import id.ac.umn.ujournal.ui.components.common.LocationPicker
import id.ac.umn.ujournal.ui.components.common.MediaActions
import id.ac.umn.ujournal.ui.components.common.TimePickerModal
import id.ac.umn.ujournal.ui.components.common.UJournalBottomSheet
import id.ac.umn.ujournal.ui.components.common.UJournalTopAppBar
import id.ac.umn.ujournal.ui.components.journalentry.CreateJournalEntryBottomTab
import id.ac.umn.ujournal.ui.constant.NOT_BLANK_VALIDATION_HINT
import id.ac.umn.ujournal.ui.util.HourTimeFormatter24
import id.ac.umn.ujournal.ui.util.ddMMMMyyyyDateTimeFormatter
import id.ac.umn.ujournal.ui.util.getAddressFromLatLong
import id.ac.umn.ujournal.ui.util.toLocalMilliseconds
import id.ac.umn.ujournal.viewmodel.JournalEntryViewModel
import io.konform.validation.Validation
import io.konform.validation.constraints.maxLength
import io.konform.validation.constraints.notBlank
import io.konform.validation.messagesAtPath
import kotlinx.coroutines.launch
import com.google.firebase.storage.storage
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

data class EditJournalInput(
    var entryTitle: String = "",
    var entryBody: String = ""
)

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
    var entryTitleInputErrMsg by remember { mutableStateOf("") }
    var entryBody by rememberSaveable { mutableStateOf(journalEntry.description) }
    var entryBodyInputErrMsg by remember { mutableStateOf("") }
    var photoUri by rememberSaveable { mutableStateOf(journalEntry.imageURI?.let { Uri.parse(it) }) }
    var latitude by rememberSaveable { mutableStateOf(journalEntry.latitude) }
    var longitude by rememberSaveable { mutableStateOf(journalEntry.longitude) }
    var createdAt by rememberSaveable { mutableStateOf(journalEntry.createdAt) }
    var showLocationPicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    val adaptiveInfo = currentWindowAdaptiveInfo()
    val storage = Firebase.storage("gs://ujournal-7ec75.firebasestorage.app")
    val storageRef = storage.reference

    var isBottomSheetVisible by remember { mutableStateOf(false) }

    val validateEditJournalInput = Validation {
        EditJournalInput::entryTitle {
            notBlank() hint NOT_BLANK_VALIDATION_HINT
            maxLength(100)
        }

        EditJournalInput::entryBody  {
            notBlank() hint NOT_BLANK_VALIDATION_HINT
            maxLength(255)
        }
    }

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
        val validationResult = validateEditJournalInput(EditJournalInput(entryTitle, entryBody))

        if(!validationResult.isValid) {
            val validationErrors = validationResult.errors

            if (validationErrors.messagesAtPath(EditJournalInput::entryTitle).isNotEmpty()) {
                entryTitleInputErrMsg = validationErrors.messagesAtPath(EditJournalInput::entryTitle).first()
            }

            if (validationErrors.messagesAtPath(EditJournalInput::entryBody).isNotEmpty()) {
                entryBodyInputErrMsg = validationErrors.messagesAtPath(EditJournalInput::entryBody).first()
            }

            return
        }

        if (photoUri != null) {
            val ref = storageRef.child("journal_images/${UUID.randomUUID()}.jpg") // Use a unique file name for each upload

            val uploadTask = ref.putFile(photoUri!!)

            uploadTask.addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    journalEntryViewModel.update(
                        journalEntryID = journalEntry.id.toString(),
                        newTitle = entryTitle,
                        newDescription = entryBody,
                        newImageURI = downloadUrl,
                        newLatitude = latitude,
                        newLongitude = longitude,
                        updatedAt = LocalDateTime.now(),
                        newDate = createdAt
                    )

                    journalEntryViewModel.addJournalEntry(journalEntry)

                    onBackButtonClick()
                }
            }.addOnFailureListener { exception ->
                Log.e("Upload", "Upload failed: ${exception.message}")
            }
        } else {
            journalEntryViewModel.update(
                journalEntryID = journalEntry.id.toString(),
                newTitle = entryTitle,
                newDescription = entryBody,
                newImageURI = null,
                newLatitude = latitude,
                newLongitude = longitude,
                updatedAt = LocalDateTime.now(),
                newDate = createdAt
            )

            journalEntryViewModel.addJournalEntry(journalEntry)

            onBackButtonClick()
        }
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

    if (showTimePicker) {
        TimePickerModal(
            onDismiss = {
                showTimePicker = false
            },
            onConfirm = {
                createdAt = createdAt.withHour(it.hour).withMinute(it.minute)
                showTimePicker = false
            },
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.navigationBars),
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
                    modifier = Modifier.fillMaxWidth().height(
                        when(
                            adaptiveInfo.windowSizeClass.windowWidthSizeClass
                        ) {
                            WindowWidthSizeClass.MEDIUM -> 350.dp
                            WindowWidthSizeClass.EXPANDED -> 350.dp
                            else -> 250.dp
                        }
                    ),
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

            TextButton(
                onClick = {
                    showTimePicker = true
                }
            ) {
                Text(
                    text = createdAt.format(HourTimeFormatter24)
                )
                Spacer(Modifier.padding(horizontal = 1.dp))
                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Time dropdown")
            }

            Column(modifier = Modifier.padding(10.dp)) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = entryTitle,
                    onValueChange = {
                        if(entryTitleInputErrMsg.isNotBlank()){
                            entryTitleInputErrMsg = ""
                        }
                        entryTitle = it
                    },
                    label = { Text("Title") },
                    isError = entryTitleInputErrMsg.isNotBlank(),
                    supportingText =  if (entryTitleInputErrMsg.isNotBlank()) {
                        { Text(text = entryTitleInputErrMsg) }
                    } else {
                        null
                    }
                )
                Spacer(Modifier.padding(vertical = 8.dp))
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .verticalScroll(rememberScrollState()),
                    value = entryBody,
                    onValueChange = {
                        if(entryBodyInputErrMsg.isNotBlank()){
                            entryBodyInputErrMsg = ""
                        }
                        entryBody = it
                    },
                    label = { Text("Description") },
                    isError = entryBodyInputErrMsg.isNotBlank(),
                    supportingText =  if (entryBodyInputErrMsg.isNotBlank()) {
                        { Text(text = entryBodyInputErrMsg) }
                    } else {
                        null
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