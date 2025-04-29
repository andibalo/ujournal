package id.ac.umn.ujournal.ui.journal

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
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
import androidx.window.core.layout.WindowWidthSizeClass
import coil3.compose.AsyncImage
import com.google.firebase.Firebase
import id.ac.umn.ujournal.data.model.JournalEntry
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
import id.ac.umn.ujournal.R
import id.ac.umn.ujournal.ui.components.common.snackbar.Severity
import id.ac.umn.ujournal.ui.components.common.snackbar.SnackbarController
import id.ac.umn.ujournal.ui.components.common.snackbar.UJournalSnackBar
import id.ac.umn.ujournal.ui.components.common.snackbar.UJournalSnackBarVisuals
import id.ac.umn.ujournal.ui.util.getFileExtension
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.UUID

data class CreateJournalInput(
    var entryTitle: String = "",
    var entryBody: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateJournalEntryScreen(
    journalEntryViewModel: JournalEntryViewModel = viewModel(),
    onBackButtonClick : () -> Unit = {},
    snackbarHostState: SnackbarHostState
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    var isLoading by rememberSaveable { mutableStateOf(false) }
    var photoUri: Uri? by rememberSaveable { mutableStateOf(null) }
    var entryTitle by rememberSaveable { mutableStateOf("") }
    var entryTitleInputErrMsg by remember { mutableStateOf("") }
    var entryBody by rememberSaveable { mutableStateOf("") }
    var entryBodyInputErrMsg by remember { mutableStateOf("") }
    var showLocationPicker by remember { mutableStateOf(false) }
    var latitude: Double? by rememberSaveable { mutableStateOf(null) }
    var longitude: Double? by rememberSaveable { mutableStateOf(null) }

    val adaptiveInfo = currentWindowAdaptiveInfo()

    val currentDate = LocalDateTime.now()
    var entryDate: LocalDateTime by rememberSaveable {
        mutableStateOf(currentDate)
    }

    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    val snackbar = SnackbarController.current
    val context = LocalContext.current
    val storage = Firebase.storage(context.getString(R.string.firebase_bucket_url))
    val storageRef = storage.reference

    var isBottomSheetVisible by remember { mutableStateOf(false) }

    val validateCreateJournalInput = Validation {
        CreateJournalInput::entryTitle {
            notBlank() hint NOT_BLANK_VALIDATION_HINT
            maxLength(100)
        }

        CreateJournalInput::entryBody  {
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
        val validationResult = validateCreateJournalInput(CreateJournalInput(entryTitle, entryBody))

        if (!validationResult.isValid) {
            val validationErrors = validationResult.errors

            if (validationErrors.messagesAtPath(CreateJournalInput::entryTitle).isNotEmpty()) {
                entryTitleInputErrMsg = validationErrors.messagesAtPath(CreateJournalInput::entryTitle).first()
            }

            if (validationErrors.messagesAtPath(CreateJournalInput::entryBody).isNotEmpty()) {
                entryBodyInputErrMsg = validationErrors.messagesAtPath(CreateJournalInput::entryBody).first()
            }

            return
        }

        isLoading = true

        if (photoUri != null) {
            val currentDate =  SimpleDateFormat("yyyyMMdd").format(Date())

            val ext = photoUri!!.getFileExtension(context)
            val fileName = "${UUID.randomUUID()}_${currentDate}.${ext}"
            val ref = storageRef.child("${context.getString(R.string.journal_image_bucket_folder)}/${fileName}")

            val uploadTask = ref.putFile(photoUri!!)

            uploadTask.addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    val journalEntry = JournalEntry(
                        id = UUID.randomUUID(),
                        title = entryTitle,
                        description = entryBody,
                        imageURI = downloadUrl,
                        latitude = latitude,
                        longitude = longitude,
                        createdAt = entryDate,
                        updatedAt = null
                    )

                    journalEntryViewModel.addJournalEntry(journalEntry)

                    isLoading = false

                    onBackButtonClick()
                }
            }.addOnFailureListener { exception ->
                isLoading = false
                Log.e("Upload", "Upload failed: ${exception.message}")
                snackbar.showMessage(
                    message = exception.message ?: "Upload failed: ${exception.message}",
                    severity = Severity.ERROR
                )
            }
        } else {
            val journalEntry = JournalEntry(
                id = UUID.randomUUID(),
                title = entryTitle,
                description = entryBody,
                imageURI = null,
                latitude = latitude,
                longitude = longitude,
                createdAt = entryDate,
                updatedAt = null
            )

            journalEntryViewModel.addJournalEntry(journalEntry)

            isLoading = false

            onBackButtonClick()
        }
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
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.navigationBars),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackBarData ->
                val sbData = (snackBarData.visuals as UJournalSnackBarVisuals)

                UJournalSnackBar(snackbarData = snackBarData, severity = sbData.severity)
            }
        },
        topBar = {
            UJournalTopAppBar(
                title = {
                    Text(
                        text = "Create Journal",
                    )
                },
                onBackButtonClick = {
                    if(!isLoading){
                        onBackButtonClick()
                    }
                },
                actions = {
                    if(isLoading){
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .size(26.dp)
                        ){
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.surface
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                onSubmitClick()
                            }
                        ) {
                            Icon(
                                Icons.Filled.Done,
                                contentDescription = "Create journal entry",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(
                            when (
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

            if (showDatePicker) {
                DatePickerModal(
                    onDismiss = {
                        showDatePicker = false
                    },
                    onDateSelected = { selectedTimestamp ->
                        if (selectedTimestamp != null) {
                            val selectedDate = LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(selectedTimestamp),
                                ZoneId.systemDefault()
                            )

                            if(selectedDate.toLocalDate() == currentDate.toLocalDate()) {
                                entryDate = LocalDateTime.now()
                            } else {
                                entryDate = selectedDate
                            }
                        }
                    },
                    dataInSeconds = entryDate.toLocalMilliseconds()
                )
            }

            if (showTimePicker) {
                TimePickerModal(
                    onDismiss = {
                        showTimePicker = false
                    },
                    onConfirm = {
                        entryDate = entryDate.withHour(it.hour).withMinute(it.minute)
                        showTimePicker = false
                    },
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

            if(entryDate.toLocalDate() != currentDate.toLocalDate()){
                TextButton(
                    onClick = {
                        showTimePicker = true
                    }
                ) {
                    Text(
                        text = entryDate.format(HourTimeFormatter24)
                    )
                    Spacer(Modifier.padding(horizontal = 1.dp))
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = "Time dropdown")
                }
            }

            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = entryTitle,
                    onValueChange = {
                        if(entryTitleInputErrMsg.isNotBlank()){
                            entryTitleInputErrMsg = ""
                        }
                        entryTitle = it
                    },
                    label = {
                        Text("Title")
                    },
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
                    label = {
                        Text("Description")
                    },
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