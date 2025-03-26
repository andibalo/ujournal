package id.ac.umn.ujournal.ui.auth

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.ac.umn.ujournal.model.User
import id.ac.umn.ujournal.ui.components.common.OutlinedPasswordTextField
import id.ac.umn.ujournal.ui.components.common.snackbar.Severity
import id.ac.umn.ujournal.ui.components.common.snackbar.SnackbarController
import id.ac.umn.ujournal.viewmodel.UserViewModel
import java.util.UUID

@Composable
fun RegisterScreen(
    userViewModel: UserViewModel = viewModel(),
    onLoginClick: () -> Unit = {},
    navigateToHomeScreen: () -> Unit = {},
    snackbarHostState: SnackbarHostState
) {
    var firstNameInput by remember { mutableStateOf("") }
    var lastNameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    val snackbar = SnackbarController.current

    fun onRegisterClick() {
        // TODO: add validation

        try {
            userViewModel.register(User(
                id = UUID.randomUUID(),
                firstName = firstNameInput,
                lastName = lastNameInput,
                email = emailInput,
                profileImageURL = null,
                password = confirmPasswordInput
            ))

            navigateToHomeScreen()
        }catch (e: Exception){

            Log.d("RegisterScreen.onRegisterClick", e.message ?: "Unknown Error")
            Log.d("RegisterScreen.onRegisterClick", e.stackTraceToString())

            snackbar.showMessage(
                message = e.message ?: "Something went wrong",
                severity = Severity.ERROR
            )
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding: PaddingValues ->
        Surface(
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding()).fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 30.dp)
            ) {
                Text(
                    text = "U-Journal",
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    text = "By GoonPlatoon",
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = firstNameInput,
                    onValueChange = { firstNameInput = it },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = lastNameInput,
                    onValueChange = { lastNameInput = it },
                    label = { Text("Last Name") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedPasswordTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = passwordInput,
                ) {
                    passwordInput = it
                }
                OutlinedPasswordTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = confirmPasswordInput,
                    label = "Confirm Password",
                ) {
                    confirmPasswordInput = it
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onRegisterClick()
                    }
                ) {
                    Text(text = "Register")
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Already have an account?")
                    TextButton(onClick = onLoginClick) {
                        Text(text = "Log In")
                    }
                }
            }
        }
    }
}