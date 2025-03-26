package id.ac.umn.ujournal.ui.auth

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import id.ac.umn.ujournal.ui.components.common.OutlinedPasswordTextField
import id.ac.umn.ujournal.viewmodel.UserViewModel

@Composable
fun LoginScreen(
    userViewModel: UserViewModel = viewModel(),
    onSignUpClick: () -> Unit = {},
    navigateToHomeScreen: () -> Unit = {}
) {
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }

    fun onLoginClick() {
        // TODO: add validation

        try {
            userViewModel.login(email = emailInput, password = passwordInput)
            navigateToHomeScreen()
        }catch (e: Exception){

            Log.d("LoginScreen.onLoginClick", e.message ?: "Unknown Error")
            Log.d("LoginScreen.onLoginClick", e.stackTraceToString())

            // TODO: show snackbar if user login failed
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
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
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onLoginClick()
                }
            ) {
                Text(text = "Login")
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(text = "Don't have an account?")
                TextButton(onClick = onSignUpClick) {
                    Text(text = "Sign Up")
                }
            }

        }
    }
}