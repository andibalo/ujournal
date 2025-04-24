package id.ac.umn.ujournal.ui.components.common

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import id.ac.umn.ujournal.viewmodel.AuthState
import id.ac.umn.ujournal.viewmodel.AuthViewModel

@Composable
fun ProtectedScreen(
    redirectToLogin: () -> Unit = {},
    authViewModel: AuthViewModel,
    content: @Composable () -> Unit,
) {
    val authState = authViewModel.authState.collectAsState()
    Log.d("AuthState", authState.value.toString())

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> redirectToLogin()
            else -> Unit
        }
    }

    content()
}

