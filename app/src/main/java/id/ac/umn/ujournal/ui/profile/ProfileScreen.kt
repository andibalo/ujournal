package id.ac.umn.ujournal.ui.profile

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import id.ac.umn.ujournal.R
import id.ac.umn.ujournal.ui.components.common.MediaActions
import id.ac.umn.ujournal.ui.components.common.UJournalBottomSheet
import id.ac.umn.ujournal.ui.components.common.UJournalTopAppBar
import id.ac.umn.ujournal.viewmodel.AuthViewModel
import id.ac.umn.ujournal.viewmodel.ThemeMode
import id.ac.umn.ujournal.viewmodel.ThemeViewModel
import id.ac.umn.ujournal.viewmodel.UserState
import id.ac.umn.ujournal.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    themeViewModel: ThemeViewModel,
    authViewModel : AuthViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel(),
    onBackButtonClick : () -> Unit = {},
) {
    val themeState by themeViewModel.themeMode.collectAsState()
    val userState by userViewModel.userState.collectAsState()
    val user = (userState as UserState.Success).user

    var photoUri: Uri? by rememberSaveable { mutableStateOf(null) }

    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    val storage = Firebase.storage("gs://ujournal-7ec75.firebasestorage.app")
    val storageRef = storage.reference

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

    fun uploadProfileImage(photoUri: Uri) {
        val ref = storageRef.child("profile_images/${UUID.randomUUID()}.jpg") // Use a unique file name for each upload

        val uploadTask = ref.putFile(photoUri)

        uploadTask.addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { uri ->
                val downloadUrl = uri.toString()

                user.profileImageURL = downloadUrl
                userViewModel.updateUserData(user)

                hideBottomSheet()
            }
        }.addOnFailureListener { exception ->
            Log.e("Upload", "Upload failed: ${exception.message}")
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
                    AsyncImage(
                        model = user.profileImageURL ?: R.drawable.default_profile_picture,
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
                            text = user.firstName + " " + user.lastName,
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
                            text = user.email,
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

        if (sheetState.isVisible) {
            UJournalBottomSheet(
                sheetState = sheetState,
                onDismiss = { hideBottomSheet() }
            ) {
                MediaActions(
                    onSuccessTakePicture = {
                        photoUri = it
                        uploadProfileImage(photoUri!!)
                        hideBottomSheet()
                    },
                    onSuccessChooseFromGallery = {
                        photoUri = it
                        uploadProfileImage(photoUri!!)
                        hideBottomSheet()
                    }
                )
            }
        }
    }
}