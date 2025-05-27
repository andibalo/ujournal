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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.ac.umn.ujournal.R
import id.ac.umn.ujournal.ui.components.common.DividerText
import id.ac.umn.ujournal.ui.components.common.GoogleAuthButton
import id.ac.umn.ujournal.ui.components.common.OutlinedPasswordTextField
import id.ac.umn.ujournal.ui.components.common.snackbar.Severity
import id.ac.umn.ujournal.ui.components.common.snackbar.SnackbarController
import id.ac.umn.ujournal.ui.components.common.snackbar.UJournalSnackBar
import id.ac.umn.ujournal.ui.components.common.snackbar.UJournalSnackBarVisuals
import id.ac.umn.ujournal.ui.constant.EMAIL_REGEX
import id.ac.umn.ujournal.ui.constant.EMAIL_VALIDATION_HINT
import id.ac.umn.ujournal.ui.constant.NOT_BLANK_VALIDATION_HINT
import id.ac.umn.ujournal.viewmodel.AuthState
import id.ac.umn.ujournal.viewmodel.AuthViewModel
import id.ac.umn.ujournal.viewmodel.UserViewModel
import io.konform.validation.Validation
import io.konform.validation.constraints.notBlank
import io.konform.validation.constraints.pattern
import io.konform.validation.messagesAtPath
import kotlinx.coroutines.launch

data class LoginInput(
    var email: String = "",
    var password: String = ""
)

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel(),
    onSignUpClick: () -> Unit = {},
    navigateToHomeScreen: () -> Unit = {},
    snackbarHostState: SnackbarHostState
) {
    var emailInput by remember { mutableStateOf("") }
    var emailInputErrMsg by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var passwordInputErrMsg by remember { mutableStateOf("") }
    val snackbar = SnackbarController.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val authState = authViewModel.authState.collectAsState()

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Authenticated -> navigateToHomeScreen()
            else -> Unit
        }
    }

    val validateLoginInput = Validation {
        LoginInput::email {
            notBlank() hint NOT_BLANK_VALIDATION_HINT
            pattern(EMAIL_REGEX) hint EMAIL_VALIDATION_HINT
        }

        LoginInput::password  {
            notBlank() hint NOT_BLANK_VALIDATION_HINT
        }
    }

    fun onLoginClick() {
        val validationResult = validateLoginInput(LoginInput(emailInput, passwordInput))

        if(!validationResult.isValid) {
            val validationErrors = validationResult.errors

            if (validationErrors.messagesAtPath(LoginInput::email).isNotEmpty()) {
                emailInputErrMsg = validationErrors.messagesAtPath(LoginInput::email).first()
            }

            if (validationErrors.messagesAtPath(LoginInput::password).isNotEmpty()) {
                passwordInputErrMsg = validationErrors.messagesAtPath(LoginInput::password).first()
            }

            return
        }

        scope.launch {
            try {
                val userAuth = authViewModel.firebaseAuthBasicLogin(emailInput, passwordInput)

                userViewModel.fetchUserFromFirebase(userAuth.uid)

                authViewModel.setAuthStatus(true)
                navigateToHomeScreen()
            }catch (e: Exception){

                Log.d("LoginScreen.onLoginClick", e.message ?: "Unknown Error")
                Log.d("LoginScreen.onLoginClick", e.stackTraceToString())

                snackbar.showMessage(
                    message = e.message ?: "Something went wrong",
                    severity = Severity.ERROR
                )
            }
        }
    }

    fun onSignInWithGoogle() {
        scope.launch {
            try {
                val userAuth = authViewModel.firebaseAuthWithGoogle(context)

                if (userAuth == null){
                    throw Exception("User data is null")
                }

                userViewModel.fetchUserFromFirebase(userAuth.uid)
                authViewModel.setAuthStatus(true)
                navigateToHomeScreen()
            }catch (e: Exception){
                Log.d("LoginScreen.onSignInWithGoogle", e.message ?: "Unknown Error")
                Log.d("LoginScreen.onSignInWithGoogle", e.stackTraceToString())

                if (e.message != "activity is cancelled by the user.") {
                    snackbar.showMessage(
                        message = e.message ?: "Something went wrong",
                        severity = Severity.ERROR
                    )

                    authViewModel.logout()
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackBarData ->
                val sbData = (snackBarData.visuals as UJournalSnackBarVisuals)

                UJournalSnackBar(snackbarData = snackBarData, severity = sbData.severity)
            }
        }
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
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onLoginClick()
                    }
                ) {
                    Text(text = "Login")
                }
                Button(
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        authViewModel.logout()
                    }
                ) {
                    Text(text = "Login")
                }
                DividerText(
                    text = "Or continue with"
                )
                GoogleAuthButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Google"
                ) {
                    onSignInWithGoogle()
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
}