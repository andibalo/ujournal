package id.ac.umn.ujournal.ui.components.common

import id.ac.umn.ujournal.BuildConfig
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import java.util.Objects
import android.Manifest
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Modifier
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraLauncher(
    modifier: Modifier = Modifier,
    onSuccessTakePicture: (Uri) -> Unit = {},
    content: @Composable (() -> Unit)? = null,
) {
    val context = LocalContext.current

    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        BuildConfig.APPLICATION_ID + ".provider", file
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) onSuccessTakePicture(uri)
        }
    )

    val cameraPermissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA,
        onPermissionResult = { granted ->
            if (granted) {
                cameraLauncher.launch(uri)
            } else print("camera permission is denied")
        }
    )

    if(content != null) {
        Box(
            modifier = modifier.clickable {
                cameraPermissionState.launchPermissionRequest()
            },
        ) {
            content()
        }

        return
    }

    IconButton(onClick = cameraPermissionState::launchPermissionRequest) {
        Icon(
            Icons.Filled.Camera,
            contentDescription = "Take a photo with Camera",
        )
    }
}

fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir /* directory */
    )
}