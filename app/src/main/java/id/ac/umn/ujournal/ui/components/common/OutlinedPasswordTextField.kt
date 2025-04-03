package id.ac.umn.ujournal.ui.components.common

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun OutlinedPasswordTextField(
    modifier: Modifier = Modifier,
    value: String,
    label: String = "Password",
    placeholder: String = "",
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    onPasswordChange: (String) -> Unit
) {
        var passwordVisibility by remember { mutableStateOf(false) }

        val icon = if (passwordVisibility)
          Icons.Filled.Visibility
        else
            Icons.Filled.VisibilityOff

        OutlinedTextField(
            modifier = modifier,
            value = value,
            onValueChange = onPasswordChange,
            placeholder = { Text(text = placeholder) },
            label = { Text(text = label) },
            isError = isError,
            supportingText = supportingText,
            trailingIcon = {
                IconButton(onClick = {
                    passwordVisibility = !passwordVisibility
                }) {
                    Icon(
                        icon,
                        contentDescription = "Visibility Icon"
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = if (passwordVisibility) VisualTransformation.None
            else PasswordVisualTransformation()
        )
}