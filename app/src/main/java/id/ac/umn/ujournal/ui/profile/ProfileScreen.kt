package id.ac.umn.ujournal.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.Uri
import coil3.compose.AsyncImage
import coil3.toCoilUri
import id.ac.umn.ujournal.R
import id.ac.umn.ujournal.ui.components.common.MediaActions
import id.ac.umn.ujournal.ui.components.common.UJournalBottomSheet
import id.ac.umn.ujournal.ui.components.common.UJournalTopAppBar
import id.ac.umn.ujournal.viewmodel.UserState
import id.ac.umn.ujournal.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userViewModel: UserViewModel = viewModel(),
    onBackButtonClick : () -> Unit = {},
    navigateToLoginScreen : () -> Unit = {}
) {
    val userState by userViewModel.userState.collectAsState()
    val user = (userState as UserState.Success).user

    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var photoUri by rememberSaveable { mutableStateOf<Uri?>(null) }

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
        navigateToLoginScreen()
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
                    .background(MaterialTheme.colorScheme.secondary), contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.padding(30.dp)
                ) {
                    AsyncImage(
                        model = photoUri ?: R.drawable.default_profile_picture,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(120.dp)
                            .clickable {
                                showBottomSheet()
                            }
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
                            .fillMaxWidth()
                            .height(45.dp),
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp),
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
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp),
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
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Switch(
                            checked = false,
                            onCheckedChange = {
                                // TODO: Dark mode
                            }
                        )
                    }
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            shape = RoundedCornerShape(5.dp),
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                onLogoutClick()
                            }
                        ) {
                            Text(
                                text = "Logout"
                            )
                        }
                        Text(
                            text = "Kelompok 3",
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
                    onSuccessTakePicture = { uri ->
                        photoUri = uri.toCoilUri()
                        hideBottomSheet()
                    },
                    onSuccessChooseFromGallery = { uri ->
                        photoUri = uri?.toCoilUri()
                        hideBottomSheet()
                    }
                )
            }
        }
    }
}