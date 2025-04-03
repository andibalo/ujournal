package id.ac.umn.ujournal.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.ac.umn.ujournal.R
import id.ac.umn.ujournal.ui.components.common.UJournalTopAppBar
import id.ac.umn.ujournal.viewmodel.UserState
import id.ac.umn.ujournal.viewmodel.UserViewModel

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
                    Image(
//                        TODO: onClick change pfp
                        painter = painterResource(id = R.drawable.default_profile_picture),
                        contentDescription = " Profile Picture",
                        modifier = Modifier
                            .clip(CircleShape)
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
                            contentDescription = "Name"
                        )
                        Text(
                            text = user.firstName + " " + user.lastName,
                            fontSize = 18.sp
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
                            contentDescription = "Name"
                        )
                        Text(
                            text = user.email,
                            fontSize = 18.sp
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
                                contentDescription = "Dark Mode Toggle"
                            )
                            Text(
                                text = "Dark Mode",
                                fontSize = 18.sp
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
                            onClick = {
                                onLogoutClick()
                            }
                        ) {
                            Text(text = "Logout")
                        }
                        Text(
                            text = "Kelompok 3",
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
//            Card(
//                colors = CardDefaults.cardColors(
//                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
//                ),
//                modifier = Modifier
//                    .size(width = 300.dp, height = 450.dp)
//            ) {
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    modifier = Modifier
//                        .padding(20.dp)
//                        .fillMaxHeight(),
//                    verticalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Column (
//                        modifier = Modifier
//                            .weight(1f)
//                            .fillMaxWidth(),
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
//                    ) {
//                        Box {
//                            Image(
//                                painter = painterResource(id = R.drawable.default_profile_picture),
//                                contentDescription = " Profile Picture",
//                                modifier = Modifier
//                                    .size(150.dp)
//                                    .clip(CircleShape)
//                            )
//                        }
//                        Text(
////                            text = user.firstName + " " + user.lastName,
//                            text = "John Doe", //temp hardcode for preview
//                            textAlign = TextAlign.Center,
//                            fontSize = 24.sp,
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
//                    Column (
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.spacedBy(15.dp)
//                    ) {
//                        HorizontalDivider(color = MaterialTheme.colorScheme.secondary)
//                        Text(
//                            text = "Kelompok 3",
//                            textAlign = TextAlign.Center,
//                        )
//                    }
//                }
//            }
}