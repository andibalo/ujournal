package id.ac.umn.ujournal.ui.journal

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material.icons.filled.PersonPinCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import id.ac.umn.ujournal.ui.components.DatePickerModal
import id.ac.umn.ujournal.ui.components.UJournalTopAppBar
import id.ac.umn.ujournal.ui.util.ddMMMMyyyyDateTimeFormatter
import id.ac.umn.ujournal.ui.util.toLocalMilliseconds
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

    val currentDate = LocalDateTime.now()
    var entryDate: LocalDateTime by rememberSaveable {
        mutableStateOf(currentDate)
    }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            photoUri = uri
        }
    }

    fun onUploadImageClick()  {
        imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    fun onSubmitClick() {
        // TODO: add validation

        val journalEntry = JournalEntry(
            id = UUID.randomUUID(),
            title = entryTitle,
            description = entryBody,
            imageURI = photoUri.toString(),
            geotag = listOf(),
            createdAt = entryDate,
            updatedAt = null
        )

        journalEntryViewModel.addJournalEntry(journalEntry)
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
                        Icon(Icons.Filled.Done, contentDescription = "Create journal entry")
                    }
                }
            )
        },
        bottomBar = {
            Surface {
                Row (
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onUploadImageClick()
                        }
                    ) {
                        Text(text = "Media")
                        Spacer(Modifier.padding(horizontal = 4.dp))
                        Icon(Icons.Filled.PermMedia, contentDescription = "Upload media")
                    }

                    Spacer(Modifier.padding(horizontal = 10.dp))

                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            // TODO: add geotag functionality
                        }
                    ) {
                        Text(text = "Geotag")
                        Spacer(Modifier.padding(horizontal = 4.dp))
                        Icon(Icons.Filled.PersonPinCircle, contentDescription = "Add journal entry geotag")
                    }
                }
            }
        }
    ) { padding: PaddingValues ->
        Column(
            modifier =
                Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ,
        ) {
            if(photoUri != null){
                AsyncImage(
                    model = photoUri,
                    modifier = Modifier.fillMaxWidth().height(250.dp),
                    contentDescription = "Journal Entry Photo",
                    contentScale = ContentScale.Crop
                )
            }

            if(showDatePicker){
                DatePickerModal(
                    onDismiss = {
                        showDatePicker = false
                    },
                    onDateSelected = {
                        selectedTimestamp ->
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
        }
    }
}