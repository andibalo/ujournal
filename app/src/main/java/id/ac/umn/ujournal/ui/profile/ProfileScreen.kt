package id.ac.umn.ujournal.ui.profile

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import id.ac.umn.ujournal.R
import id.ac.umn.ujournal.ui.components.common.MediaActions
import id.ac.umn.ujournal.ui.components.common.UJournalBottomSheet
import id.ac.umn.ujournal.ui.components.common.UJournalTopAppBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import id.ac.umn.ujournal.ui.components.common.LoadingScreen
import id.ac.umn.ujournal.ui.components.common.snackbar.Severity
import id.ac.umn.ujournal.ui.components.common.snackbar.SnackbarController
import id.ac.umn.ujournal.ui.components.common.snackbar.UJournalSnackBar
import id.ac.umn.ujournal.ui.components.common.snackbar.UJournalSnackBarVisuals
import id.ac.umn.ujournal.ui.util.getFileExtension
import id.ac.umn.ujournal.ui.util.shimmerLoading
import id.ac.umn.ujournal.viewmodel.AuthViewModel
import id.ac.umn.ujournal.viewmodel.ThemeMode
import id.ac.umn.ujournal.viewmodel.ThemeViewModel
import id.ac.umn.ujournal.viewmodel.UserViewModel
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    themeViewModel: ThemeViewModel,
    authViewModel : AuthViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel(),
    onBackButtonClick : () -> Unit = {},
    snackbarHostState: SnackbarHostState
) {
    val themeState by themeViewModel.themeMode.collectAsState()
    val user by userViewModel.user.collectAsState()
    var isUploadingProfilePicture by rememberSaveable { mutableStateOf(false) }
    var isFetchingUserData by rememberSaveable { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbar = SnackbarController.current
    val storage = Firebase.storage(context.getString(R.string.firebase_bucket_url))
    val storageRef = storage.reference

    LaunchedEffect(Unit) {
        isFetchingUserData = true

        try {
            userViewModel.loadUserData()
        } catch (e: Exception) {
            snackbar.showMessage(
                message = e.message ?: "Something went wrong",
                severity = Severity.ERROR
            )
        } finally {
            isFetchingUserData = false
        }
    }

    fun showBottomSheet() {
        coroutineScope.launch {
            sheetState.show()
        }
    }

    fun hideBottomSheet() {
        coroutineScope.launch {
            sheetState.hide()
        }
    }

    fun onLogoutClick() {
        userViewModel.logout()
        authViewModel.logout()
    }

    fun formatFileSize(sizeInBytes: Long): String {
        val kb = 1024L
        val mb = kb * 1024
        val gb = mb * 1024

        return when {
            sizeInBytes >= gb -> String.format("%.2f GB", sizeInBytes.toDouble() / gb)
            sizeInBytes >= mb -> String.format("%.2f MB", sizeInBytes.toDouble() / mb)
            sizeInBytes >= kb -> String.format("%.2f KB", sizeInBytes.toDouble() / kb)
            else -> "$sizeInBytes Bytes"
        }
    }

    fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("profile_image", ".jpg", context.cacheDir)

        inputStream?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        Log.d("Upload", "Temp file converted: ${tempFile.absolutePath}")
        return tempFile
    }

    fun uploadProfileImage(it: Uri) {
        coroutineScope.launch {
            isUploadingProfilePicture = true
            try {
                val originalFile = uriToFile(context, it)
                Log.d("Upload", "Original size: ${formatFileSize(originalFile.length())}")

                val compressedFile = Compressor.compress(context, originalFile) {
                    quality(90)
                }
                Log.d(
                    "Compression",
                    "Compressed file size: ${formatFileSize(compressedFile.length())}"
                )
                Log.i("Compression", "File compressed successfully: ${compressedFile.absolutePath}")

                val currentDate = SimpleDateFormat("yyyyMMdd").format(Date())
                val ext = it.getFileExtension(context)
                val fileName = "${UUID.randomUUID()}_${currentDate}.${ext}"

                val compressedRef =
                    storageRef.child("${context.getString(R.string.profile_picture_bucket_folder)}/${fileName}")

                val compressedFileUri = Uri.fromFile(compressedFile)

                val compressedUploadTask = compressedRef.putFile(compressedFileUri)

                compressedUploadTask.addOnSuccessListener {
                    compressedRef.metadata.addOnSuccessListener { metadata ->
                        Log.d(
                            "Firebase",
                            "Compressed uploaded size: ${formatFileSize(metadata.sizeBytes)}"
                        )
                    }
                    compressedRef.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()

                        coroutineScope.launch {
                            try {
                                userViewModel.updateProfileImage(user!!.id, downloadUrl)

                                isUploadingProfilePicture = false
                                hideBottomSheet()
                            }catch (e: Exception){
                                isUploadingProfilePicture = false

                                Log.d("ProfileScreen.uploadProfileImage", e.message ?: "Unknown Error")
                                Log.d("ProfileScreen.uploadProfileImage", e.stackTraceToString())

                                snackbar.showMessage(
                                    message = e.message ?: "Something went wrong",
                                    severity = Severity.ERROR
                                )
                            }
                        }
                    }
                }.addOnFailureListener { exception ->
                    isUploadingProfilePicture = false
                    hideBottomSheet()

                    Log.e("Upload", "Upload failed: ${exception.message}")
                    snackbar.showMessage(
                        message = exception.message ?: "Upload failed: ${exception.message}",
                        severity = Severity.ERROR
                    )
                }

            } catch (e: Exception) {
                isUploadingProfilePicture = false
                hideBottomSheet()

                Log.e("Upload", "Upload failed: ${e.message}")
                snackbar.showMessage(
                    message = e.message ?: "Upload failed",
                    severity = Severity.ERROR
                )
            }
        }
    }

    if (isFetchingUserData) {
        LoadingScreen()
        return
    }

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
                title = {
                    Text(
                        text = "Profile"
                    )
                },
                onBackButtonClick = onBackButtonClick,
            )
        },
    ) { innerPadding: PaddingValues ->
        user?.let {
            Column(
                modifier = Modifier
                    .padding(top = innerPadding.calculateTopPadding())
                    .fillMaxSize()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.primary
                        ), contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier.padding(30.dp)
                    ) {
                        if(isUploadingProfilePicture){
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(120.dp)
                                    .clickable {
                                        showBottomSheet()
                                    }
                                    .shimmerLoading()
                                ,
                            )
                        } else {
                            AsyncImage(
                                model = it.profileImageURL ?: R.drawable.default_profile_picture,
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(120.dp)
                                    .clickable {
                                        showBottomSheet()
                                    },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Filled.Badge,
                                contentDescription = "Name",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = it.firstName + " " + it.lastName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.W400
                            )
                        }
                        Spacer(Modifier.padding(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Filled.Mail,
                                contentDescription = "Email",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = it.email,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.W400
                            )
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Filled.DarkMode,
                                    contentDescription = "Dark Mode Toggle",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Dark Mode",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                            Switch(
                                checked = themeState == ThemeMode.DARK,
                                onCheckedChange = {
                                    themeViewModel.toggleTheme()
                                }
                            )
                        }
                        Column(
                            modifier = Modifier.padding( vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    onLogoutClick()
                                }
                            ) {
                                Text(
                                    text = "Logout"
                                )
                            }
                            Spacer(Modifier.padding(8.dp))
                            Text(
                                text = "GoonPlatoon (Kelompok 3)",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }


        if (sheetState.isVisible) {
            UJournalBottomSheet(
                sheetState = sheetState,
                onDismiss = { hideBottomSheet() }
            ) {
                MediaActions(
                    onSuccessTakePicture = {
                        uploadProfileImage(it)
                        hideBottomSheet()
                    },
                    onSuccessChooseFromGallery = {
                        uploadProfileImage(it!!)
                        hideBottomSheet()
                    }
                )
            }
        }
    }
}