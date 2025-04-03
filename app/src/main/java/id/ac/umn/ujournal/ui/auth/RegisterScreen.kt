package id.ac.umn.ujournal.ui.auth

import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.ac.umn.ujournal.R
import id.ac.umn.ujournal.model.User
import id.ac.umn.ujournal.ui.components.common.OutlinedPasswordTextField
import id.ac.umn.ujournal.ui.components.common.snackbar.Severity
import id.ac.umn.ujournal.ui.components.common.snackbar.SnackbarController
import id.ac.umn.ujournal.ui.constant.EMAIL_REGEX
import id.ac.umn.ujournal.ui.constant.EMAIL_VALIDATION_HINT
import id.ac.umn.ujournal.ui.constant.NOT_BLANK_VALIDATION_HINT
import id.ac.umn.ujournal.viewmodel.UserViewModel
import io.konform.validation.Validation
import io.konform.validation.constraints.notBlank
import io.konform.validation.constraints.pattern
import io.konform.validation.messagesAtPath
import java.util.UUID

data class RegisterInput(
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var password: String = "",
    var confirmPassword: String = "",
)

@Composable
fun RegisterScreen(
    userViewModel: UserViewModel = viewModel(),
    onLoginClick: () -> Unit = {},
    navigateToHomeScreen: () -> Unit = {},
    snackbarHostState: SnackbarHostState
) {
    var firstNameInput by remember { mutableStateOf("") }
    var firstNameInputErrMsg by remember { mutableStateOf("") }
    var lastNameInput by remember { mutableStateOf("") }
    var lastNameInputErrMsg by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var emailInputErrMsg by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var passwordInputErrMsg by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var confirmPasswordInputErrMsg by remember { mutableStateOf("") }
    val snackbar = SnackbarController.current

    val validateRegisterInput = Validation {
        RegisterInput::firstName {
            notBlank() hint NOT_BLANK_VALIDATION_HINT
        }

        RegisterInput::lastName {
            notBlank() hint NOT_BLANK_VALIDATION_HINT
        }

        RegisterInput::email {
            notBlank() hint NOT_BLANK_VALIDATION_HINT
            pattern(EMAIL_REGEX) hint EMAIL_VALIDATION_HINT
        }

        RegisterInput::password  {
            notBlank() hint NOT_BLANK_VALIDATION_HINT
        }

        RegisterInput::confirmPassword  {
            notBlank() hint NOT_BLANK_VALIDATION_HINT
        }
    }

    fun onRegisterClick() {
        val validationResult = validateRegisterInput(
            RegisterInput(
                firstNameInput, lastNameInput, emailInput, passwordInput
            )
        )

        if(!validationResult.isValid) {
            val validationErrors = validationResult.errors

            if (validationErrors.messagesAtPath(RegisterInput::firstName).isNotEmpty()) {
                firstNameInputErrMsg = validationErrors.messagesAtPath(RegisterInput::firstName).first()
            }

            if (validationErrors.messagesAtPath(RegisterInput::lastName).isNotEmpty()) {
                lastNameInputErrMsg = validationErrors.messagesAtPath(RegisterInput::lastName).first()
            }

            if (validationErrors.messagesAtPath(RegisterInput::email).isNotEmpty()) {
                emailInputErrMsg = validationErrors.messagesAtPath(RegisterInput::email).first()
            }

            if (validationErrors.messagesAtPath(RegisterInput::password).isNotEmpty()) {
                passwordInputErrMsg = validationErrors.messagesAtPath(RegisterInput::password).first()
            }

            if (validationErrors.messagesAtPath(RegisterInput::confirmPassword).isNotEmpty()) {
                confirmPasswordInputErrMsg = validationErrors.messagesAtPath(RegisterInput::confirmPassword).first()
            }

            if(confirmPasswordInput != passwordInput){
                confirmPasswordInputErrMsg = "Password does not match"
            }

            return
        }

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
                Image(
                    painter = painterResource(id = R.drawable.logo_blue),
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentDescription = "App Logo"
                )
                Spacer(modifier = Modifier.height(12.dp))
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
                    onValueChange = {
                        if(firstNameInputErrMsg.isNotBlank()){
                            firstNameInputErrMsg = ""
                        }

                        firstNameInput = it
                    },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = firstNameInputErrMsg.isNotBlank(),
                    supportingText =  if (firstNameInputErrMsg.isNotBlank()) {
                        { Text(text = firstNameInputErrMsg) }
                    } else {
                        null
                    }
                )
                OutlinedTextField(
                    value = lastNameInput,
                    onValueChange = {
                        if(lastNameInputErrMsg.isNotBlank()){
                            lastNameInputErrMsg = ""
                        }

                        lastNameInput = it
                    },
                    label = { Text("Last Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = lastNameInputErrMsg.isNotBlank(),
                    supportingText =  if (lastNameInputErrMsg.isNotBlank()) {
                        { Text(text = lastNameInputErrMsg) }
                    } else {
                        null
                    }
                )
                OutlinedTextField(
                    value = emailInput,
                    onValueChange = {
                        if(emailInputErrMsg.isNotBlank()){
                            emailInputErrMsg = ""
                        }

                        emailInput = it
                    },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = emailInputErrMsg.isNotBlank(),
                    supportingText =  if (emailInputErrMsg.isNotBlank()) {
                        { Text(text = emailInputErrMsg) }
                    } else {
                        null
                    }
                )
                OutlinedPasswordTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = passwordInput,
                    isError = passwordInputErrMsg.isNotBlank(),
                    supportingText =  if (passwordInputErrMsg.isNotBlank()) {
                        { Text(text = passwordInputErrMsg) }
                    } else {
                        null
                    }
                ) {
                    if(passwordInputErrMsg.isNotBlank()){
                        passwordInputErrMsg = ""
                    }

                    passwordInput = it
                }
                OutlinedPasswordTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = confirmPasswordInput,
                    label = "Confirm Password",
                    isError = confirmPasswordInputErrMsg.isNotBlank(),
                    supportingText =  if (confirmPasswordInputErrMsg.isNotBlank()) {
                        { Text(text = confirmPasswordInputErrMsg) }
                    } else {
                        null
                    }
                ) {
                    if(confirmPasswordInputErrMsg.isNotBlank()){
                        confirmPasswordInputErrMsg = ""
                    }

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