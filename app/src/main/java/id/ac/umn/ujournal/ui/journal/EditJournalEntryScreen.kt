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
import id.ac.umn.ujournal.R
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import id.ac.umn.ujournal.data.model.JournalEntry
import id.ac.umn.ujournal.ui.components.common.LoadingScreen
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
    snackbarHostState: SnackbarHostState
) {
    if (journalEntryID == null) {
        onBackButtonClick()
        return
    }

    var journalEntry by remember { mutableStateOf<JournalEntry?>(null) }

    var isFetchingInitialData by rememberSaveable { mutableStateOf(false) }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var entryTitle by rememberSaveable { mutableStateOf("") }
    var entryTitleInputErrMsg by remember { mutableStateOf("") }
    var entryBody by rememberSaveable { mutableStateOf("") }
    var entryBodyInputErrMsg by remember { mutableStateOf("") }
    var photoUri: Uri? by rememberSaveable { mutableStateOf(null) }
    var newPhotoUri : Uri? by rememberSaveable { mutableStateOf(null)}
    var latitude: Double? by rememberSaveable { mutableStateOf(null) }
    var longitude: Double? by rememberSaveable { mutableStateOf(null) }
    var createdAt: LocalDateTime? by rememberSaveable { mutableStateOf(null) }
    var showLocationPicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    val adaptiveInfo = currentWindowAdaptiveInfo()
    val context = LocalContext.current
    val snackbar = SnackbarController.current
    val storage = Firebase.storage(context.getString(R.string.firebase_bucket_url))
    val storageRef = storage.reference

    var isBottomSheetVisible by remember { mutableStateOf(false) }

    LaunchedEffect(journalEntryID) {
        isFetchingInitialData = true

        val je = journalEntryViewModel.getJournalEntryByID(journalEntryID)

        try {
            journalEntry = journalEntryViewModel.getJournalEntryByID(journalEntryID)

            if (je == null) {
                onBackButtonClick()
            } else {
                journalEntry = je

                entryTitle = je.title
                entryBody = je.description

                if(je.imageURI != null) {
                    photoUri = Uri.parse(je.imageURI)
                }

                latitude = je.latitude
                longitude = je.longitude
                createdAt = je.createdAt
            }
        } catch (e: Exception) {
            snackbar.showMessage(
                message = e.message ?: "Something went wrong",
                severity = Severity.ERROR
            )
        } finally {
            isFetchingInitialData = false
        }
    }

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

        isLoading = true

        if (newPhotoUri != null) {
            val currentDate =  SimpleDateFormat("yyyyMMdd").format(Date())

            val ext = newPhotoUri!!.getFileExtension(context)
            val fileName = "${UUID.randomUUID()}_${currentDate}.${ext}"
            val ref = storageRef.child("${context.getString(R.string.journal_image_bucket_folder)}/${fileName}")

            val uploadTask = ref.putFile(newPhotoUri!!)

            uploadTask.addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()

                    coroutineScope.launch {
                        try {

                            journalEntryViewModel.updateJournalEntryByID(
                                journalEntryID = journalEntry!!.id,
                                journalEntry = journalEntry!!.copy(
                                    title = entryTitle,
                                    description = entryBody,
                                    imageURI = downloadUrl,
                                    latitude = latitude,
                                    longitude = longitude,
                                    createdAt = createdAt!!
                                )
                            )

                            isLoading = false
                            onBackButtonClick()
                        }catch (e: Exception){
                            isLoading = false
                            Log.d("EditJournalEntryScreen.onSubmitClick", e.message ?: "Unknown Error")
                            Log.d("EditJournalEntryScreen.onSubmitClick", e.stackTraceToString())

                            snackbar.showMessage(
                                message = e.message ?: "Something went wrong",
                                severity = Severity.ERROR
                            )
                        }
                    }
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
            coroutineScope.launch {
                try {

                    journalEntryViewModel.updateJournalEntryByID(
                        journalEntryID = journalEntry!!.id,
                        journalEntry = journalEntry!!.copy(
                            title = entryTitle,
                            description = entryBody,
                            latitude = latitude,
                            longitude = longitude,
                            createdAt = createdAt!!
                        )
                    )

                    isLoading = false
                    onBackButtonClick()
                }catch (e: Exception){
                    isLoading = false
                    Log.d("EditJournalEntryScreen.onSubmitClick", e.message ?: "Unknown Error")
                    Log.d("EditJournalEntryScreen.onSubmitClick", e.stackTraceToString())

                    snackbar.showMessage(
                        message = e.message ?: "Something went wrong",
                        severity = Severity.ERROR
                    )
                }
            }
        }
    }

    if (isFetchingInitialData) {
        LoadingScreen()
        return
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
        createdAt?.let {
            DatePickerModal(
                onDismiss = {
                    showDatePicker = false
                },
                onDateSelected = { selectedTimestamp ->
                    if (selectedTimestamp != null) {
                        createdAt = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(selectedTimestamp),
                            ZoneId.systemDefault()
                        )
                    }
                },
                dataInSeconds = it.toLocalMilliseconds()
            )
        }
    }

    if (showTimePicker) {
        TimePickerModal(
            onDismiss = {
                showTimePicker = false
            },
            onConfirm = {
                createdAt = createdAt?.withHour(it.hour)?.withMinute(it.minute)
                showTimePicker = false
            },
        )
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
                title = { Text(text = "Edit Journal") },
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
                        IconButton(onClick = { onSubmitClick() }) {
                            Icon(
                                Icons.Filled.Done,
                                contentDescription = "Save changes",
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
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (photoUri != null || newPhotoUri != null) {
                AsyncImage(
                    model = if (newPhotoUri != null) newPhotoUri else photoUri,
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
                createdAt?.let {
                    Text(
                        text = it.format(ddMMMMyyyyDateTimeFormatter)
                    )
                }
                Spacer(Modifier.padding(horizontal = 1.dp))
                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Date dropdown")
            }

            TextButton(
                onClick = {
                    showTimePicker = true
                }
            ) {
                createdAt?.let {
                    Text(
                        text = it.format(HourTimeFormatter24)
                    )
                }
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
                            newPhotoUri = it
                            hideBottomSheet()
                        },
                        onSuccessChooseFromGallery = {
                            newPhotoUri = it
                            hideBottomSheet()
                        }
                    )
                }
            }
        }
    }
}