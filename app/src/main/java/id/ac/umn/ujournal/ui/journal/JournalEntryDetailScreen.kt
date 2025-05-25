package id.ac.umn.ujournal.ui.journal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
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
import id.ac.umn.ujournal.R
import id.ac.umn.ujournal.data.model.JournalEntry
import id.ac.umn.ujournal.ui.components.common.ErrorScreen
import id.ac.umn.ujournal.ui.components.common.LoadingScreen
import id.ac.umn.ujournal.ui.components.common.UJournalTopAppBar
import id.ac.umn.ujournal.ui.components.common.snackbar.Severity
import id.ac.umn.ujournal.ui.components.common.snackbar.SnackbarController
import id.ac.umn.ujournal.ui.components.common.snackbar.UJournalSnackBar
import id.ac.umn.ujournal.ui.components.common.snackbar.UJournalSnackBarVisuals
import id.ac.umn.ujournal.ui.util.HourTimeFormatter24
import id.ac.umn.ujournal.ui.util.ddMMMMyyyyDateTimeFormatter
import id.ac.umn.ujournal.ui.util.getAddressFromLatLong
import id.ac.umn.ujournal.viewmodel.JournalEntryState
import id.ac.umn.ujournal.viewmodel.JournalEntryViewModel
import id.ac.umn.ujournal.viewmodel.UserState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalEntryDetailScreen(
    journalEntryViewModel: JournalEntryViewModel = viewModel(),
    journalEntryID: String?,
    onBackButtonClick: () -> Unit = {},
    onEditButtonClick: (String) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    if (journalEntryID == null) {
        onBackButtonClick()
        return
    }

    var journalEntry by remember { mutableStateOf<JournalEntry?>(null) }
    val journalEntryState by journalEntryViewModel.journalEntryState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val adaptiveInfo = currentWindowAdaptiveInfo()
    var isLoading by rememberSaveable { mutableStateOf(false) }
    val snackbar = SnackbarController.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(journalEntryID) {
        isLoading = true

        val je = journalEntryViewModel.getJournalEntryByID(journalEntryID)

        try {
            journalEntry = journalEntryViewModel.getJournalEntryByID(journalEntryID)

            if (je == null) {
                onBackButtonClick()
            } else {
                journalEntry = je
            }
        } catch (e: Exception) {
            snackbar.showMessage(
                message = e.message ?: "Something went wrong",
                severity = Severity.ERROR
            )
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        LoadingScreen()
        return
    }

    if (journalEntryState is JournalEntryState.Error) {
        ErrorScreen()
        return
    }

    journalEntry?.let {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { snackBarData ->
                    val sbData = (snackBarData.visuals as UJournalSnackBarVisuals)

                    UJournalSnackBar(snackbarData = snackBarData, severity = sbData.severity)
                }
            },
            topBar = {
                UJournalTopAppBar(
                    title = { Text(text = "Journal Detail") },
                    onBackButtonClick = onBackButtonClick,
                    actions = {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Filled.Delete,
                                tint = MaterialTheme.colorScheme.error,
                                contentDescription = "Delete journal entry"
                            )
                        }
                        Spacer(Modifier.padding(4.dp))
                        IconButton(onClick = {
                            onEditButtonClick(journalEntryID)
                        }) {
                            Icon(
                                Icons.Filled.Edit,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                contentDescription = "Edit journal entry"
                            )
                        }
                    }
                )
            },
        ) { padding: PaddingValues ->
            Surface(
                Modifier.padding(padding).fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                ) {
                    AsyncImage(
                        model = if (it.imageURI != null && it.imageURI != "") it.imageURI else R.drawable.default_placeholder_image,
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
                    Column(modifier = Modifier.padding(10.dp)) {
                        if (it.latitude != null && it.longitude != null) {
                            Row(
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
                                        lat = it.latitude!!,
                                        lon = it.longitude!!,
                                        context = LocalContext.current
                                    )?.let {
                                        Text(
                                            text = it.getAddressLine(0),
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                        Spacer(Modifier.padding(2.dp))
                                    }
                                    Text(
                                        text = "%.4f, %.4f".format(it.latitude, it.longitude),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.padding(vertical = 6.dp))
                        Text(
                            text = it.createdAt.format(ddMMMMyyyyDateTimeFormatter) + ", " +
                                    it.createdAt.format(HourTimeFormatter24),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(Modifier.padding(vertical = 4.dp))
                        Text(
                            text = it.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.padding(vertical = 4.dp))
                        Text(
                            text = it.description,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Journal Entry") },
                text = { Text("Are you sure you want to delete this journal entry?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    journalEntryViewModel.deleteJournalEntryByID(journalEntryID)
                                    showDeleteDialog = false
                                    onBackButtonClick()
                                } catch (e: Exception) {
                                    showDeleteDialog = false
                                    snackbar.showMessage(
                                        message = e.message ?: "Something went wrong",
                                        severity = Severity.ERROR
                                    )
                                }
                            }
                        }
                    ) {
                        Text(
                            "Yes",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text(
                            "No",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    }
}